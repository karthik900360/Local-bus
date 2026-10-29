package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DigitalTicketDialog
import com.example.ui.components.NotificationInboxDialog
import com.example.ui.components.TransitBottomNavBar
import com.example.ui.components.TransitTopBar
import com.example.ui.screens.BusFrequencyScreen
import com.example.ui.screens.FareCalculatorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveMapScreen
import com.example.ui.screens.RouteTimingsScreen
import com.example.ui.screens.TrafficAlertsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TransitTab
import com.example.ui.viewmodel.TransitViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        TransitApp()
      }
    }
  }
}

@Composable
fun TransitApp(viewModel: TransitViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val notifications by viewModel.pushNotifications.collectAsStateWithLifecycle()
  val context = LocalContext.current

  var showNotificationDialog by remember { mutableStateOf(false) }

  // Request POST_NOTIFICATIONS permission on Android 13+
  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { _ ->
    // Permission handled
  }

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  // BackHandler: If on secondary tab, back navigates to ROUTES tab
  BackHandler(enabled = uiState.currentTab != TransitTab.ROUTES) {
    viewModel.selectTab(TransitTab.ROUTES)
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TransitTopBar(
        title = when (uiState.currentTab) {
          TransitTab.ROUTES -> "Yatri Bus"
          TransitTab.TIMINGS -> "Route Timings"
          TransitTab.LIVE_MAP -> "Live GPS Map"
          TransitTab.FARE_CALCULATOR -> "Fare Calculator"
          TransitTab.BUS_FREQUENCY -> "Bus Frequency"
          TransitTab.TRAFFIC_ALERTS -> "Traffic Alerts"
        },
        selectedCity = uiState.selectedCity.substringBefore(" "),
        notificationCount = notifications.size,
        onNotificationClick = { showNotificationDialog = true },
        onCityClick = { viewModel.selectTab(TransitTab.ROUTES) }
      )
    },
    bottomBar = {
      TransitBottomNavBar(
        currentTab = uiState.currentTab,
        onTabSelected = { tab -> viewModel.selectTab(tab) },
        alertCount = viewModel.trafficAlerts.size
      )
    }
  ) { innerPadding ->
    val contentModifier = Modifier
      .fillMaxSize()
      .padding(innerPadding)

    when (uiState.currentTab) {
      TransitTab.ROUTES -> {
        HomeScreen(
          uiState = uiState,
          filteredRoutes = viewModel.getFilteredRoutes(),
          cities = viewModel.cities,
          onCitySelected = { city -> viewModel.selectCity(city) },
          onSearchQueryChanged = { query -> viewModel.updateSearchQuery(query) },
          onOperatorFilterSelected = { filter -> viewModel.setOperatorFilter(filter) },
          onRouteSelected = { route, targetTab ->
            viewModel.selectRoute(route, targetTab)
          },
          onViewAlerts = { viewModel.selectTab(TransitTab.TRAFFIC_ALERTS) },
          modifier = contentModifier
        )
      }

      TransitTab.TIMINGS -> {
        RouteTimingsScreen(
          route = uiState.selectedRoute,
          trackedStopId = uiState.trackedNotificationStopId,
          onSetStopReminder = { stop -> viewModel.setStopReminder(stop) },
          onNavigateToMap = { viewModel.selectTab(TransitTab.LIVE_MAP) },
          modifier = contentModifier
        )
      }

      TransitTab.LIVE_MAP -> {
        LiveMapScreen(
          route = uiState.selectedRoute,
          liveBus = uiState.liveBus,
          isTrackingPaused = uiState.isTrackingPaused,
          showTrafficOverlay = uiState.showTrafficOverlay,
          onToggleTracking = { viewModel.toggleTrackingPause() },
          onToggleTrafficOverlay = { viewModel.toggleTrafficOverlay() },
          onTriggerNotificationAlert = {
            viewModel.setStopReminder(uiState.selectedRoute.stops[2])
          },
          modifier = contentModifier
        )
      }

      TransitTab.FARE_CALCULATOR -> {
        FareCalculatorScreen(
          route = uiState.selectedRoute,
          fareEstimate = uiState.fareEstimate,
          selectedOrigin = uiState.fareOriginStop,
          selectedDestination = uiState.fareDestinationStop,
          onOriginSelected = { stop -> viewModel.setFareOrigin(stop) },
          onDestinationSelected = { stop -> viewModel.setFareDestination(stop) },
          onSwapStops = { viewModel.swapFareStops() },
          onGenerateDigitalTicket = { type -> viewModel.openPassDialog(type) },
          modifier = contentModifier
        )
      }

      TransitTab.BUS_FREQUENCY -> {
        BusFrequencyScreen(
          route = uiState.selectedRoute,
          allRoutes = viewModel.allRoutes,
          timeSlots = uiState.timeSlotSchedules,
          selectedSlotId = uiState.selectedTimeSlotId,
          onSelectRoute = { newRoute -> viewModel.selectRoute(newRoute) },
          onSelectSlot = { slotId -> viewModel.selectTimeSlot(slotId) },
          onTrackBusOnMap = { routeToTrack ->
            viewModel.selectRoute(routeToTrack, TransitTab.LIVE_MAP)
          },
          modifier = contentModifier
        )
      }

      TransitTab.TRAFFIC_ALERTS -> {
        TrafficAlertsScreen(
          alerts = viewModel.trafficAlerts,
          onTestPushNotification = { viewModel.triggerManualTrafficAlertTest() },
          modifier = contentModifier
        )
      }
    }

    // Digital Ticket QR Dialog
    if (uiState.showPassDialog) {
      DigitalTicketDialog(
        route = uiState.selectedRoute,
        fareEstimate = uiState.fareEstimate,
        passType = uiState.passTypeSelected,
        onDismiss = { viewModel.closePassDialog() }
      )
    }

    // Notification Inbox Dialog
    if (showNotificationDialog) {
      NotificationInboxDialog(
        notifications = notifications,
        onDismiss = { showNotificationDialog = false }
      )
    }
  }
}
