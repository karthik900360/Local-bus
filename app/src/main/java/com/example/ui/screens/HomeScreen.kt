package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BusOperatorType
import com.example.data.model.BusRoute
import com.example.ui.theme.*
import com.example.ui.viewmodel.TransitTab
import com.example.ui.viewmodel.TransitUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  uiState: TransitUiState,
  filteredRoutes: List<BusRoute>,
  cities: List<String>,
  onCitySelected: (String) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onOperatorFilterSelected: (String) -> Unit = {},
  onRouteSelected: (BusRoute, TransitTab) -> Unit,
  onViewAlerts: () -> Unit,
  modifier: Modifier = Modifier
) {
  val popularHubs = listOf(
    "Gandhipuram", "Ukkadam", "Railway Stn", "Saravanampatti",
    "Peelamedu", "Marudhamalai", "Airport (CJB)", "Singanallur",
    "Vadavalli", "Town Hall", "Saibaba Colony"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF6F7FA))
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 84.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Rapido "Where are you going?" Commute Search Box
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(Color(0xFFECEFF1), Color(0xFFECEFF1)))
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("rapido_where_to_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(TrafficGreen)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "COIMBATORE LOCAL TRANSIT",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TrafficGreen,
                letterSpacing = 0.5.sp
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = RapidoYellowLight
            ) {
              Text(
                text = "GOVT & PRIVATE BUSES",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = RapidoBlack,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Pickup: Current Stop
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFF6F7FA))
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.MyLocation, contentDescription = null, tint = TrafficGreen, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Current Origin / Boarding Stop", fontSize = 10.sp, color = RapidoTextMuted, fontWeight = FontWeight.Bold)
              Text(
                text = uiState.selectedRoute.origin,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Search Destination / Stop Input
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFF1F3F7))
              .padding(horizontal = 12.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEBEE)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = TrafficRed, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
              value = uiState.searchQuery,
              onValueChange = { onSearchQueryChanged(it) },
              placeholder = {
                Text(
                  text = "Search bus no, stop (e.g. 11A, 70A, Gandhipuram, Marudhamalai)...",
                  fontSize = 12.sp,
                  color = RapidoTextMuted,
                  maxLines = 1
                )
              },
              trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                  IconButton(onClick = { onSearchQueryChanged("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = RapidoBlack, modifier = Modifier.size(18.dp))
                  }
                } else {
                  Icon(Icons.Default.Search, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(18.dp))
                }
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
              ),
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("route_search_input")
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Popular Coimbatore Hub Pills
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            popularHubs.forEach { hub ->
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF1F3F7),
                modifier = Modifier
                  .clickable { onSearchQueryChanged(hub) }
                  .testTag("hub_chip_$hub")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.NearMe, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = hub, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RapidoBlack)
                }
              }
            }
          }
        }
      }
    }

    // 2. Local Government vs Private Bus Operator Filter Pills
    item {
      Column {
        Text(
          text = "Filter by Bus Operator Type",
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold,
          color = RapidoBlack
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val filters = listOf(
            Triple("ALL", "All Buses", Icons.Default.DirectionsBus),
            Triple("GOVERNMENT", "🏛️ TNSTC (Govt)", Icons.Default.AccountBalance),
            Triple("PRIVATE", "🚌 Private Buses", Icons.Default.DirectionsBus),
            Triple("WOMEN_FREE", "🌸 Free for Women", Icons.Default.People)
          )

          filters.forEach { (filterKey, label, icon) ->
            val isSelected = uiState.operatorFilter == filterKey
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) RapidoBlack else Color.White,
              shadowElevation = if (isSelected) 3.dp else 1.dp,
              modifier = Modifier
                .clickable { onOperatorFilterSelected(filterKey) }
                .testTag("operator_filter_$filterKey")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (isSelected) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(RapidoYellow)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                  color = if (isSelected) Color.White else RapidoBlack
                )
              }
            }
          }
        }
      }
    }

    // 3. Rapido Mobility Services Grid (City Ordinary, Express, AC Low Floor, Daily Pass)
    item {
      Column {
        Text(
          text = "Quick Commute Actions",
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold,
          color = RapidoBlack
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // City Bus Timings & Stops
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier
              .weight(1f)
              .clickable { onRouteSelected(uiState.selectedRoute, TransitTab.TIMINGS) }
              .testTag("quick_action_timings")
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(RapidoYellow),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Bus Timings", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = RapidoBlack)
              Text("Arrival & Stops", fontSize = 10.sp, color = RapidoTextMuted)
            }
          }

          // Live GPS Map
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier
              .weight(1f)
              .clickable { onRouteSelected(uiState.selectedRoute, TransitTab.LIVE_MAP) }
              .testTag("quick_action_map")
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(RapidoBlack),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Map, contentDescription = null, tint = RapidoYellow, modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Live GPS", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = RapidoBlack)
              Text("Track Location", fontSize = 10.sp, color = RapidoTextMuted)
            }
          }

          // Fare Calculator
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier
              .weight(1f)
              .clickable { onRouteSelected(uiState.selectedRoute, TransitTab.FARE_CALCULATOR) }
              .testTag("quick_action_fare")
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = TrafficGreen, modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Ticket Cost", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = RapidoBlack)
              Text("From ₹5 • Free 👩", fontSize = 10.sp, color = RapidoTextMuted)
            }
          }

          // Bus Availability
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier
              .weight(1f)
              .clickable { onRouteSelected(uiState.selectedRoute, TransitTab.BUS_FREQUENCY) }
              .testTag("quick_action_frequency")
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("Frequency", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = RapidoBlack)
              Text("How many buses", fontSize = 10.sp, color = RapidoTextMuted)
            }
          }
        }
      }
    }

    // 4. Hero Visual Banner
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RapidoBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("hero_radar_banner")
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_hero_banner),
            contentDescription = "Transit Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color.Transparent, Color(0xAA141518), Color(0xEE141518)),
                  startY = 30f
                )
              )
          )

          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(14.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                color = RapidoYellow,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "COIMBATORE LIVE RADAR",
                  color = RapidoBlack,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "TNSTC Govt & Private Buses",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Live GPS, stops count & stage ticket cost",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 5. Live Traffic Delay Alert Banner Ticker
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RapidoYellow, TrafficRed))),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onViewAlerts() }
          .testTag("traffic_alert_ticker")
      ) {
        Row(
          modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(RapidoYellow),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Traffic Warning",
              tint = RapidoBlack,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "COIMBATORE TRAFFIC RADAR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = TrafficRed
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Tap for bottleneck alerts",
                fontSize = 10.sp,
                color = RapidoTextMuted
              )
            }
            Text(
              text = uiState.selectedRoute.activeTrafficAlert ?: "Avinashi Road & Town Hall corridor traffic delays reported",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = RapidoBlack,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "View alerts",
            tint = RapidoBlack,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // 6. City Selector Chips
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        cities.forEach { city ->
          val isSelected = city == uiState.selectedCity
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) RapidoBlack else Color.White,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier
              .clickable { onCitySelected(city) }
              .testTag("city_chip_${city.take(4)}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isSelected) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(RapidoYellow)
                )
                Spacer(modifier = Modifier.width(6.dp))
              }
              Text(
                text = city,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else RapidoBlack
              )
            }
          }
        }
      }
    }

    // 7. Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Buses Available (${filteredRoutes.size})",
          fontSize = 15.sp,
          fontWeight = FontWeight.ExtraBold,
          color = RapidoBlack
        )
        Text(
          text = "Live GPS & Timings",
          fontSize = 11.sp,
          color = RapidoTextMuted
        )
      }
    }

    // 8. High-Contrast Rapido Route Cards with Operator, Stops Count & Ticket Cost
    items(filteredRoutes, key = { it.id }) { route ->
      val isSelected = route.id == uiState.selectedRoute.id
      val isGovt = route.operatorType == BusOperatorType.GOVERNMENT

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = if (isSelected) Brush.horizontalGradient(listOf(RapidoYellow, RapidoYellow)) else Brush.horizontalGradient(listOf(CardBorder, CardBorder))
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("route_card_${route.routeNumber}")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Top Row: Yellow Number Plate, Operator Tag, and Delay Tag
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Indian Yellow Transport Number Plate
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = PlateYellow,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
              ) {
                Text(
                  text = "ROUTE ${route.routeNumber}",
                  color = PlateText,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Spacer(modifier = Modifier.width(6.dp))

              // Operator Badge (TNSTC Govt vs Private)
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isGovt) Color(0xFFE3F2FD) else Color(0xFFFFF3E0)
              ) {
                Text(
                  text = if (isGovt) "🏛️ TNSTC (Govt)" else "🚌 ${route.operatorName}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (isGovt) Color(0xFF0D47A1) else Color(0xFFE65100),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            // Live status / delay badge
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (route.isDelayed) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (route.isDelayed) TrafficRed else TrafficGreen)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (route.isDelayed) "+${route.currentDelayMin}m" else "On Time",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (route.isDelayed) TrafficRed else TrafficGreen
                )
              }
            }
          }

          // Women Free Scheme banner if eligible
          if (route.isWomenFreeTravel) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFFCE4EC)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🌸 Vidiyal Payanam: 100% Free Travel for Women (₹0)",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFC2185B)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Route Path (Origin to Destination)
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TrafficGreen))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "FROM", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = RapidoTextMuted)
              }
              Text(
                text = route.origin,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 14.dp)
              )
            }

            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = RapidoYellowDark,
              modifier = Modifier.padding(horizontal = 4.dp).size(16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(TrafficRed))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "TO", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = RapidoTextMuted)
              }
              Text(
                text = route.destination,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 14.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Key Information Strip: How many stops + Ticket Cost + Next Arrival Time
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFF6F7FA))
              .clickable { onRouteSelected(route, TransitTab.TIMINGS) }
              .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "🚏 ${route.stops.size} Stops will stop",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RapidoBlack
              )
              Text(
                text = "Next: ${route.stops.firstOrNull()?.scheduledArrival ?: "08:15 AM"} • Bay 4",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (route.isWomenFreeTravel) Color(0xFFE8F5E9) else RapidoYellowLight
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                horizontalAlignment = Alignment.End
              ) {
                Text(
                  text = if (route.isWomenFreeTravel) "Fare: ₹5 (Women ₹0)" else "Fare: ₹6 - ₹${if (isGovt) 15 else 18}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = if (route.isWomenFreeTravel) TrafficGreen else RapidoBlack
                )
                Text(
                  text = "Every ${route.peakFrequencyMin}m",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = RapidoTextMuted
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Rapido Action Buttons: Live Map + Stops/Timings + Fare
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Track Live GPS Map
            ElevatedButton(
              onClick = { onRouteSelected(route, TransitTab.LIVE_MAP) },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.elevatedButtonColors(
                containerColor = RapidoYellow,
                contentColor = RapidoBlack
              ),
              modifier = Modifier
                .weight(1.2f)
                .testTag("action_live_map_${route.routeNumber}")
            ) {
              Icon(Icons.Default.Map, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Track Live", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }

            // Timings & Stops
            OutlinedButton(
              onClick = { onRouteSelected(route, TransitTab.TIMINGS) },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = RapidoBlack),
              modifier = Modifier
                .weight(1f)
                .testTag("action_timings_${route.routeNumber}")
            ) {
              Icon(Icons.Default.Schedule, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Stops (${route.stops.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Ticket Cost
            OutlinedButton(
              onClick = { onRouteSelected(route, TransitTab.FARE_CALCULATOR) },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = RapidoBlack),
              modifier = Modifier
                .weight(0.9f)
                .testTag("action_fare_${route.routeNumber}")
            ) {
              Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Fare", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
