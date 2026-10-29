package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BusOperatorType
import com.example.data.model.BusRoute
import com.example.data.model.BusStopSchedule
import com.example.data.model.FareEstimate
import com.example.data.model.LiveBus
import com.example.data.model.TimeSlotSchedule
import com.example.data.model.TrafficAlert
import com.example.data.repository.TransitRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TransitTab {
  ROUTES,
  TIMINGS,
  LIVE_MAP,
  FARE_CALCULATOR,
  BUS_FREQUENCY,
  TRAFFIC_ALERTS
}

data class TransitUiState(
  val currentTab: TransitTab = TransitTab.ROUTES,
  val selectedCity: String = "Coimbatore (TNSTC & Private)",
  val searchQuery: String = "",
  val operatorFilter: String = "ALL", // "ALL", "GOVERNMENT", "PRIVATE", "WOMEN_FREE"
  val selectedRoute: BusRoute,
  val liveBus: LiveBus? = null,
  val isTrackingPaused: Boolean = false,
  val showTrafficOverlay: Boolean = true,
  val fareOriginStop: BusStopSchedule? = null,
  val fareDestinationStop: BusStopSchedule? = null,
  val fareEstimate: FareEstimate? = null,
  val selectedTimeSlotId: String = "slot_morning_peak",
  val timeSlotSchedules: List<TimeSlotSchedule> = emptyList(),
  val trackedNotificationStopId: String? = null,
  val showPassDialog: Boolean = false,
  val passTypeSelected: String = "ORDINARY",
  val bannerDismissed: Boolean = false
)

class TransitViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = TransitRepository()
  val cities = repository.cities
  val allRoutes = repository.allRoutes
  val trafficAlerts = repository.activeTrafficAlerts

  private val _uiState = MutableStateFlow(
    TransitUiState(
      selectedCity = "Coimbatore (TNSTC & Private)",
      selectedRoute = repository.allRoutes.first(),
      fareOriginStop = repository.allRoutes.first().stops.first(),
      fareDestinationStop = repository.allRoutes.first().stops.last(),
      timeSlotSchedules = repository.getTimeSlotSchedules(repository.allRoutes.first())
    )
  )
  val uiState: StateFlow<TransitUiState> = _uiState.asStateFlow()

  val pushNotifications = NotificationHelper.notificationsHistory

  private var trackingJob: Job? = null

  init {
    NotificationHelper.init(application.applicationContext)
    val initialRoute = _uiState.value.selectedRoute
    calculateFare(initialRoute.stops.first(), initialRoute.stops.last())
    startLiveTracking(initialRoute)

    viewModelScope.launch {
      NotificationHelper.triggerPushNotification(
        getApplication(),
        "Coimbatore Transit Live 🚦",
        "Tracking ${initialRoute.routeNumber} (${initialRoute.routeName}). Real-time GPS, TNSTC Govt & Private bus schedules active.",
        "ARRIVAL"
      )
    }
  }

  fun selectTab(tab: TransitTab) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  fun selectCity(city: String) {
    val routeInCity = repository.allRoutes.firstOrNull { it.city == city } ?: repository.allRoutes.first()
    _uiState.value = _uiState.value.copy(
      selectedCity = city,
      selectedRoute = routeInCity,
      fareOriginStop = routeInCity.stops.first(),
      fareDestinationStop = routeInCity.stops.last(),
      timeSlotSchedules = repository.getTimeSlotSchedules(routeInCity)
    )
    calculateFare(_uiState.value.fareOriginStop!!, _uiState.value.fareDestinationStop!!)
    startLiveTracking(routeInCity)
  }

  fun updateSearchQuery(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun setOperatorFilter(filter: String) {
    _uiState.value = _uiState.value.copy(operatorFilter = filter)
  }

  fun selectRoute(route: BusRoute, navigateToTab: TransitTab? = null) {
    val origin = route.stops.first()
    val dest = route.stops.last()
    _uiState.value = _uiState.value.copy(
      selectedRoute = route,
      selectedCity = route.city,
      fareOriginStop = origin,
      fareDestinationStop = dest,
      timeSlotSchedules = repository.getTimeSlotSchedules(route),
      currentTab = navigateToTab ?: _uiState.value.currentTab
    )
    calculateFare(origin, dest)
    startLiveTracking(route)
  }

  fun startLiveTracking(route: BusRoute) {
    trackingJob?.cancel()
    trackingJob = viewModelScope.launch {
      repository.observeLiveBusTracking(route).collect { liveBus ->
        if (!_uiState.value.isTrackingPaused) {
          _uiState.value = _uiState.value.copy(liveBus = liveBus)

          val trackedStopId = _uiState.value.trackedNotificationStopId
          if (trackedStopId != null && liveBus.nextStopId == trackedStopId) {
            NotificationHelper.triggerPushNotification(
              getApplication(),
              "Bus Arriving Soon! 🚏",
              "${liveBus.operatorName} (${liveBus.routeNumber}) approaching ${liveBus.nextStopName} in ${liveBus.etaNextStopMin} min. (${liveBus.remainingStopsCount} stops left).",
              "ARRIVAL"
            )
            _uiState.value = _uiState.value.copy(trackedNotificationStopId = null)
          }
        }
      }
    }
  }

  fun toggleTrackingPause() {
    _uiState.value = _uiState.value.copy(isTrackingPaused = !_uiState.value.isTrackingPaused)
  }

  fun toggleTrafficOverlay() {
    _uiState.value = _uiState.value.copy(showTrafficOverlay = !_uiState.value.showTrafficOverlay)
  }

  fun setFareOrigin(stop: BusStopSchedule) {
    _uiState.value = _uiState.value.copy(fareOriginStop = stop)
    _uiState.value.fareDestinationStop?.let { calculateFare(stop, it) }
  }

  fun setFareDestination(stop: BusStopSchedule) {
    _uiState.value = _uiState.value.copy(fareDestinationStop = stop)
    _uiState.value.fareOriginStop?.let { calculateFare(it, stop) }
  }

  fun swapFareStops() {
    val origin = _uiState.value.fareOriginStop
    val dest = _uiState.value.fareDestinationStop
    if (origin != null && dest != null) {
      _uiState.value = _uiState.value.copy(
        fareOriginStop = dest,
        fareDestinationStop = origin
      )
      calculateFare(dest, origin)
    }
  }

  private fun calculateFare(from: BusStopSchedule, to: BusStopSchedule) {
    val estimate = repository.calculateTicketFare(from, to)
    _uiState.value = _uiState.value.copy(fareEstimate = estimate)
  }

  fun selectTimeSlot(slotId: String) {
    _uiState.value = _uiState.value.copy(selectedTimeSlotId = slotId)
  }

  fun setStopReminder(stop: BusStopSchedule) {
    _uiState.value = _uiState.value.copy(trackedNotificationStopId = stop.stopId)
    NotificationHelper.triggerPushNotification(
      getApplication(),
      "Alert Set for ${stop.stopName}",
      "You will receive an instant push notification when bus ${_uiState.value.selectedRoute.routeNumber} approaches ${stop.stopName}.",
      "ARRIVAL"
    )
  }

  fun triggerManualTrafficAlertTest() {
    val route = _uiState.value.selectedRoute
    NotificationHelper.triggerPushNotification(
      getApplication(),
      "⚠️ Live Traffic Delay Alert: ${route.routeNumber}",
      "Heavy congestion reported on route. Delay of ${route.currentDelayMin} mins expected near central corridors.",
      "DELAY"
    )
  }

  fun openPassDialog(type: String) {
    _uiState.value = _uiState.value.copy(showPassDialog = true, passTypeSelected = type)
  }

  fun closePassDialog() {
    _uiState.value = _uiState.value.copy(showPassDialog = false)
  }

  fun dismissBanner() {
    _uiState.value = _uiState.value.copy(bannerDismissed = true)
  }

  fun getFilteredRoutes(): List<BusRoute> {
    val query = _uiState.value.searchQuery.trim().lowercase()
    val city = _uiState.value.selectedCity
    val filter = _uiState.value.operatorFilter

    return repository.allRoutes.filter { route ->
      val matchesCity = (route.city == city)
      val matchesFilter = when (filter) {
        "GOVERNMENT" -> route.operatorType == BusOperatorType.GOVERNMENT
        "PRIVATE" -> route.operatorType == BusOperatorType.PRIVATE
        "WOMEN_FREE" -> route.isWomenFreeTravel
        else -> true
      }
      val matchesQuery = query.isEmpty() ||
          route.routeNumber.lowercase().contains(query) ||
          route.routeName.lowercase().contains(query) ||
          route.operatorName.lowercase().contains(query) ||
          route.stops.any { it.stopName.lowercase().contains(query) }

      matchesCity && matchesFilter && matchesQuery
    }
  }
}
