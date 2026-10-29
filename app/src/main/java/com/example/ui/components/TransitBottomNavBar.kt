package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RapidoBlack
import com.example.ui.theme.RapidoCharcoal
import com.example.ui.theme.RapidoTextMuted
import com.example.ui.theme.RapidoYellow
import com.example.ui.viewmodel.TransitTab

@Composable
fun TransitBottomNavBar(
  currentTab: TransitTab,
  onTabSelected: (TransitTab) -> Unit,
  alertCount: Int = 0
) {
  NavigationBar(
    modifier = Modifier
      .navigationBarsPadding()
      .testTag("bottom_nav_bar"),
    containerColor = Color.White,
    tonalElevation = 8.dp
  ) {
    NavigationBarItem(
      selected = currentTab == TransitTab.ROUTES,
      onClick = { onTabSelected(TransitTab.ROUTES) },
      icon = { Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Routes") },
      label = { Text("Routes", fontSize = 11.sp, fontWeight = if (currentTab == TransitTab.ROUTES) FontWeight.Bold else FontWeight.Medium) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RapidoBlack,
        selectedTextColor = RapidoBlack,
        indicatorColor = RapidoYellow,
        unselectedIconColor = RapidoTextMuted,
        unselectedTextColor = RapidoTextMuted
      ),
      modifier = Modifier.testTag("nav_routes")
    )

    NavigationBarItem(
      selected = currentTab == TransitTab.TIMINGS,
      onClick = { onTabSelected(TransitTab.TIMINGS) },
      icon = { Icon(Icons.Default.Schedule, contentDescription = "Timings") },
      label = { Text("Timings", fontSize = 11.sp, fontWeight = if (currentTab == TransitTab.TIMINGS) FontWeight.Bold else FontWeight.Medium) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RapidoBlack,
        selectedTextColor = RapidoBlack,
        indicatorColor = RapidoYellow,
        unselectedIconColor = RapidoTextMuted,
        unselectedTextColor = RapidoTextMuted
      ),
      modifier = Modifier.testTag("nav_timings")
    )

    NavigationBarItem(
      selected = currentTab == TransitTab.LIVE_MAP,
      onClick = { onTabSelected(TransitTab.LIVE_MAP) },
      icon = { Icon(Icons.Default.Map, contentDescription = "Live GPS Map") },
      label = { Text("Live GPS", fontSize = 11.sp, fontWeight = if (currentTab == TransitTab.LIVE_MAP) FontWeight.Bold else FontWeight.Medium) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RapidoBlack,
        selectedTextColor = RapidoBlack,
        indicatorColor = RapidoYellow,
        unselectedIconColor = RapidoTextMuted,
        unselectedTextColor = RapidoTextMuted
      ),
      modifier = Modifier.testTag("nav_live_map")
    )

    NavigationBarItem(
      selected = currentTab == TransitTab.FARE_CALCULATOR,
      onClick = { onTabSelected(TransitTab.FARE_CALCULATOR) },
      icon = { Icon(Icons.Default.CurrencyRupee, contentDescription = "Fare Calculator") },
      label = { Text("Fares", fontSize = 11.sp, fontWeight = if (currentTab == TransitTab.FARE_CALCULATOR) FontWeight.Bold else FontWeight.Medium) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RapidoBlack,
        selectedTextColor = RapidoBlack,
        indicatorColor = RapidoYellow,
        unselectedIconColor = RapidoTextMuted,
        unselectedTextColor = RapidoTextMuted
      ),
      modifier = Modifier.testTag("nav_fare")
    )

    NavigationBarItem(
      selected = currentTab == TransitTab.BUS_FREQUENCY,
      onClick = { onTabSelected(TransitTab.BUS_FREQUENCY) },
      icon = { Icon(Icons.Default.HourglassTop, contentDescription = "Bus Frequency") },
      label = { Text("Buses", fontSize = 11.sp, fontWeight = if (currentTab == TransitTab.BUS_FREQUENCY) FontWeight.Bold else FontWeight.Medium) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RapidoBlack,
        selectedTextColor = RapidoBlack,
        indicatorColor = RapidoYellow,
        unselectedIconColor = RapidoTextMuted,
        unselectedTextColor = RapidoTextMuted
      ),
      modifier = Modifier.testTag("nav_frequency")
    )

    NavigationBarItem(
      selected = currentTab == TransitTab.TRAFFIC_ALERTS,
      onClick = { onTabSelected(TransitTab.TRAFFIC_ALERTS) },
      icon = { Icon(Icons.Default.Warning, contentDescription = "Traffic Alerts") },
      label = { Text("Alerts", fontSize = 11.sp, fontWeight = if (currentTab == TransitTab.TRAFFIC_ALERTS) FontWeight.Bold else FontWeight.Medium) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RapidoBlack,
        selectedTextColor = RapidoBlack,
        indicatorColor = RapidoYellow,
        unselectedIconColor = RapidoTextMuted,
        unselectedTextColor = RapidoTextMuted
      ),
      modifier = Modifier.testTag("nav_alerts")
    )
  }
}
