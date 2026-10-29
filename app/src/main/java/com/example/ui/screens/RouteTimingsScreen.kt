package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusOperatorType
import com.example.data.model.BusRoute
import com.example.data.model.BusStopSchedule
import com.example.data.model.TrafficSeverity
import com.example.ui.theme.*

@Composable
fun RouteTimingsScreen(
  route: BusRoute,
  trackedStopId: String?,
  onSetStopReminder: (BusStopSchedule) -> Unit,
  onNavigateToMap: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isGovt = route.operatorType == BusOperatorType.GOVERNMENT

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF6F7FA))
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 84.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Route Summary Header Card (Rapido Black)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RapidoBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("route_timings_header")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
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

            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (isGovt) Color(0xFFE3F2FD) else Color(0xFFFFF3E0)
            ) {
              Text(
                text = if (isGovt) "🏛️ TNSTC Government" else "🚌 ${route.operatorName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGovt) Color(0xFF0D47A1) else Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = route.routeName,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Text(
            text = "${route.agency} • ${route.permitNumber}",
            fontSize = 11.sp,
            color = RapidoYellow
          )

          // Free for Women Scheme tag
          if (route.isWomenFreeTravel) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFFCE4EC)
            ) {
              Text(
                text = "🌸 Vidiyal Payanam: 100% Free Travel for Women (₹0 Ticket)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC2185B),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Key Stats Matrix: How many stops, Distance, Duration, Fare
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(RapidoCharcoal, RoundedCornerShape(10.dp))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Total Stops", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
              Text("${route.stops.size} Stops", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column {
              Text("Route Length", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
              Text("${route.totalDistanceKm} km", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column {
              Text("Trip Duration", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
              Text("${route.averageDurationMin} mins", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column {
              Text("Ticket Range", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
              Text(if (route.isWomenFreeTravel) "₹5 (Women ₹0)" else "₹6 - ₹16", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RapidoYellow)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Rapido Yellow Button to Live Map
          ElevatedButton(
            onClick = onNavigateToMap,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.elevatedButtonColors(
              containerColor = RapidoYellow,
              contentColor = RapidoBlack
            ),
            modifier = Modifier.fillMaxWidth().testTag("view_live_map_from_timings")
          ) {
            Icon(Icons.Default.Map, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Track Live Bus Position on Map", fontWeight = FontWeight.Black, fontSize = 13.sp)
          }
        }
      }
    }

    // Traffic Delay Status Strip
    if (route.isDelayed) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TrafficAmber)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = TrafficRed, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Live Traffic Delay: +${route.currentDelayMin} mins",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TrafficRed
              )
              Text(
                text = route.activeTrafficAlert ?: "Bottlenecks along major intersections",
                fontSize = 11.sp,
                color = RapidoBlack
              )
            }
          }
        }
      }
    }

    // Schedule Table Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Which Time Bus Comes (${route.stops.size} Stops)",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RapidoBlack
          )
          Text(
            text = "Every stop scheduled arrival & departure timings",
            fontSize = 10.sp,
            color = RapidoTextMuted
          )
        }
        Text(
          text = "Tap 🔔 for alert",
          fontSize = 11.sp,
          color = RapidoTextMuted
        )
      }
    }

    // Stop Items with detailed timings, distance & remaining stops count
    itemsIndexed(route.stops, key = { _, stop -> stop.stopId }) { index, stop ->
      val isTracked = stop.stopId == trackedStopId
      val isFirst = index == 0
      val isLast = index == route.stops.lastIndex
      val remainingStopsFromHere = route.stops.size - index - 1

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isTracked) RapidoYellowLight else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = androidx.compose.ui.graphics.SolidColor(
            if (isTracked) RapidoYellowDark else CardBorder
          )
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("timing_stop_card_${stop.stopId}")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Stop sequence badge
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(
                when {
                  isFirst -> TrafficGreen
                  isLast -> TrafficRed
                  else -> Color(0xFFF1F3F7)
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "#${index + 1}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = if (isFirst || isLast) Color.White else RapidoBlack
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          // Stop name, platform, distance and remaining stops count
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = stop.stopName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoBlack
              )
              if (isFirst) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                  Text("ORIGIN", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TrafficGreen, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                }
              }
              if (isLast) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(4.dp)) {
                  Text("TERMINUS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TrafficRed, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                }
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${stop.distanceFromOriginKm} km • ${stop.platformBay}",
                fontSize = 11.sp,
                color = RapidoTextMuted
              )
              if (!isLast) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "• $remainingStopsFromHere stops left",
                  fontSize = 10.sp,
                  color = Color(0xFF0D47A1),
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Traffic condition
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(
                    when (stop.trafficCondition) {
                      TrafficSeverity.NORMAL -> TrafficGreen
                      TrafficSeverity.MODERATE -> TrafficAmber
                      TrafficSeverity.HEAVY -> TrafficRed
                    }
                  )
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = stop.trafficCondition.label,
                fontSize = 10.sp,
                color = when (stop.trafficCondition) {
                  TrafficSeverity.NORMAL -> TrafficGreen
                  TrafficSeverity.MODERATE -> TrafficAmber
                  TrafficSeverity.HEAVY -> TrafficRed
                },
                fontWeight = FontWeight.Medium
              )
            }
          }

          // Arrival and Departure Timing
          Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Arr: ", fontSize = 10.sp, color = RapidoTextMuted)
              Text(
                text = stop.scheduledArrival,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TrafficGreen
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Dep: ", fontSize = 10.sp, color = RapidoTextMuted)
              Text(
                text = stop.scheduledDeparture,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = RapidoBlack
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Push alert alarm button
          IconButton(
            onClick = { onSetStopReminder(stop) },
            modifier = Modifier.size(34.dp).testTag("reminder_button_${stop.stopId}")
          ) {
            Icon(
              imageVector = if (isTracked) Icons.Default.AlarmOn else Icons.Default.Alarm,
              contentDescription = "Set push alert for ${stop.stopName}",
              tint = if (isTracked) RapidoBlack else Color(0xFFB0B5C0)
            )
          }
        }
      }
    }
  }
}
