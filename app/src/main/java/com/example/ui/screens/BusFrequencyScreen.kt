package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusOperatorType
import com.example.data.model.BusRoute
import com.example.data.model.BusTripDeparture
import com.example.data.model.BusType
import com.example.data.model.OccupancyStatus
import com.example.data.model.TimeSlotSchedule
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusFrequencyScreen(
  route: BusRoute,
  allRoutes: List<BusRoute>,
  timeSlots: List<TimeSlotSchedule>,
  selectedSlotId: String,
  onSelectRoute: (BusRoute) -> Unit,
  onSelectSlot: (String) -> Unit,
  onTrackBusOnMap: (BusRoute) -> Unit,
  modifier: Modifier = Modifier
) {
  var routeDropdownExpanded by remember { mutableStateOf(false) }
  var occupancyFilter by remember { mutableStateOf("ALL") } // "ALL", "SEATS", "AC", "ACTIVE"

  val currentSlot = timeSlots.firstOrNull { it.slotId == selectedSlotId } ?: timeSlots.firstOrNull()

  // Filter trips based on commuter filter
  val filteredTrips = currentSlot?.busTrips?.filter { trip ->
    when (occupancyFilter) {
      "SEATS" -> trip.occupancy == OccupancyStatus.SEATS_AVAILABLE
      "AC" -> trip.busType == BusType.VAJRA_AC || trip.busType == BusType.EXPRESS
      "ACTIVE" -> trip.status == "On Route" || trip.status == "Approaching" || trip.status == "Boarding at Terminal"
      else -> true
    }
  } ?: emptyList()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF6F7FA))
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 84.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Info Card (Rapido Black)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RapidoBlack),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth().testTag("frequency_header_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Bus Availability & Frequency",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Surface(
              color = RapidoYellow,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "REAL-TIME",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = RapidoBlack,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Check the exact number of operating buses for any selected route and time slot",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f)
          )
        }
      }
    }

    // 1. Route Selector (Allows selecting ANY route)
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("route_selector_card")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "SELECTED ROUTE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
          )
          Spacer(modifier = Modifier.height(6.dp))

          ExposedDropdownMenuBox(
            expanded = routeDropdownExpanded,
            onExpandedChange = { routeDropdownExpanded = !routeDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = "${route.routeNumber}: ${route.origin} ➔ ${route.destination}",
              onValueChange = {},
              readOnly = true,
              leadingIcon = {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(route.busType.badgeColorHex),
                  modifier = Modifier.padding(start = 6.dp)
                ) {
                  Text(
                    text = route.routeNumber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }
              },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = routeDropdownExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .testTag("frequency_route_dropdown"),
              shape = RoundedCornerShape(10.dp)
            )

            ExposedDropdownMenu(
              expanded = routeDropdownExpanded,
              onDismissRequest = { routeDropdownExpanded = false }
            ) {
              allRoutes.forEach { r ->
                DropdownMenuItem(
                  text = {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(r.busType.badgeColorHex)
                      ) {
                        Text(
                          text = r.routeNumber,
                          color = Color.White,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                      Column {
                        Text(
                          text = "${r.origin} ➔ ${r.destination}",
                          fontSize = 13.sp,
                          fontWeight = FontWeight.SemiBold,
                          color = Color(0xFF0F172A)
                        )
                        Text(
                          text = "${r.agency} • ${r.city}",
                          fontSize = 11.sp,
                          color = Color(0xFF64748B)
                        )
                      }
                    }
                  },
                  onClick = {
                    onSelectRoute(r)
                    routeDropdownExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Quick Route Switcher Horizontal Carousel Chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            allRoutes.forEach { r ->
              val isSelected = r.id == route.id
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) TransitNavyPrimary else Color(0xFFF1F5F9),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                  .clickable { onSelectRoute(r) }
                  .testTag("quick_route_chip_${r.routeNumber}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = r.routeNumber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color(0xFF334155)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "• ${r.origin.take(8)}...",
                    fontSize = 10.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. Time Slot Selector
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Select Time Slot",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF475569)
          )
          Text(
            text = "Operating 24/7",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          timeSlots.forEach { slot ->
            val isSelected = slot.slotId == selectedSlotId
            FilterChip(
              selected = isSelected,
              onClick = { onSelectSlot(slot.slotId) },
              label = {
                Text(
                  text = slot.slotTitle,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.AccessTime,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp)
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TransitNavyPrimary,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White
              ),
              modifier = Modifier.testTag("time_slot_${slot.slotId}")
            )
          }
        }
      }
    }

    // 3. AVAILABLE BUSES METRIC HIGHLIGHT CARD (Directly answers the user request!)
    if (currentSlot != null) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth().testTag("frequency_metric_card")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Title & Slot Window
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = currentSlot.slotTitle.uppercase(),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TransitNavyPrimary
                )
                Text(
                  text = "Window: ${currentSlot.timeRange}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF64748B)
                )
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFF3E0)
              ) {
                Text(
                  text = currentSlot.frequencySummary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = TransitSaffronSecondary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Highlight: Number of Available Buses (Rapido Style)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(RapidoYellowLight)
                .border(1.dp, RapidoYellowDark, RoundedCornerShape(14.dp))
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(58.dp)
                  .clip(CircleShape)
                  .background(RapidoYellow),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${currentSlot.totalBusesCount}",
                  fontSize = 26.sp,
                  fontWeight = FontWeight.Black,
                  color = RapidoBlack
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Buses Available in this Slot",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = RapidoBlack
                )
                Text(
                  text = "Route ${route.routeNumber} • ${currentSlot.timeRange}",
                  fontSize = 11.sp,
                  color = RapidoTextMuted,
                  fontWeight = FontWeight.Medium
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Mini Breakdown Badges: Total, Govt Buses, Private Buses, Headway
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Total Scheduled
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "${currentSlot.totalBusesCount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RapidoBlack
                  )
                  Text("Total Buses", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                }
              }

              // Government (TNSTC) Buses
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE3F2FD),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "${currentSlot.governmentBusesCount} Govt",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0D47A1)
                  )
                  Text("TNSTC", fontSize = 10.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Medium)
                }
              }

              // Private Buses
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "${currentSlot.privateBusesCount} Pvt",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFE65100)
                  )
                  Text("Private", fontSize = 10.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Medium)
                }
              }

              // Headway
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = RapidoYellowLight,
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "~${currentSlot.averageHeadwayMin}m",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RapidoBlack
                  )
                  Text("Headway", fontSize = 10.sp, color = RapidoBlack, fontWeight = FontWeight.Medium)
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action button: View route live on map (Rapido Yellow Button)
            ElevatedButton(
              onClick = { onTrackBusOnMap(route) },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.elevatedButtonColors(
                containerColor = RapidoYellow,
                contentColor = RapidoBlack
              ),
              modifier = Modifier.fillMaxWidth().testTag("track_route_on_map_from_freq")
            ) {
              Icon(Icons.Default.Map, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Track ${route.routeNumber} Real-Time Buses on Map", fontSize = 13.sp, color = RapidoBlack, fontWeight = FontWeight.Black)
            }
          }
        }
      }

      // Filter chips for the bus trips
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Filter:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
          )

          FilterChip(
            selected = occupancyFilter == "ALL",
            onClick = { occupancyFilter = "ALL" },
            label = { Text("All (${currentSlot.busTrips.size})", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TransitNavyPrimary,
              selectedLabelColor = Color.White
            )
          )

          FilterChip(
            selected = occupancyFilter == "SEATS",
            onClick = { occupancyFilter = "SEATS" },
            label = { Text("🟢 Seats Available (${currentSlot.seatsAvailableCount})", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TrafficGreen,
              selectedLabelColor = Color.White
            )
          )

          FilterChip(
            selected = occupancyFilter == "ACTIVE",
            onClick = { occupancyFilter = "ACTIVE" },
            label = { Text("🚀 Running Now (${currentSlot.activeBusesCount})", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TransitSaffronSecondary,
              selectedLabelColor = Color.White
            )
          )

          FilterChip(
            selected = occupancyFilter == "AC",
            onClick = { occupancyFilter = "AC" },
            label = { Text("⚡ AC / Express", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF00897B),
              selectedLabelColor = Color.White
            )
          )
        }
      }

      // Departure List Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Scheduled Buses in Time Slot (${filteredTrips.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "Vehicle Reg & Seat Status",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
        }
      }

      // Individual Scheduled Trips
      items(filteredTrips, key = { it.tripId }) { trip ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth().testTag("departure_trip_${trip.tripId}")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Departure Time Box
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFE0F2FE),
              modifier = Modifier.width(72.dp)
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp)
              ) {
                Text(
                  text = trip.departureTime.substringBefore(" "),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = TransitNavyPrimary
                )
                Text(
                  text = trip.departureTime.substringAfter(" "),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TransitNavyPrimary
                )
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Bus & Status Info
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = trip.vehicleNumber,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = if (trip.operatorType == BusOperatorType.GOVERNMENT) Color(0xFFE3F2FD) else Color(0xFFFFF3E0)
                ) {
                  Text(
                    text = trip.operatorName,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (trip.operatorType == BusOperatorType.GOVERNMENT) Color(0xFF0D47A1) else Color(0xFFE65100),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
                if (trip.isWomenFree) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFCE4EC)) {
                    Text(
                      text = "Free 👩",
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFC2185B),
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(2.dp))

              Text(
                text = "Arr at dest: ${trip.expectedArrivalAtDest}",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )

              if (trip.delayMin > 0) {
                Text(
                  text = "+${trip.delayMin}m traffic delay",
                  fontSize = 10.sp,
                  color = TrafficRed,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            // Occupancy and Trip Status
            Column(horizontalAlignment = Alignment.End) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = when (trip.status) {
                  "Departed" -> Color(0xFFE2E8F0)
                  "On Route" -> Color(0xFFE8F5E9)
                  "Approaching" -> Color(0xFFFFF3E0)
                  "Boarding at Terminal" -> Color(0xFFE0F2FE)
                  else -> Color(0xFFF1F5F9)
                }
              ) {
                Text(
                  text = trip.status,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = when (trip.status) {
                    "Departed" -> Color(0xFF64748B)
                    "On Route" -> TrafficGreen
                    "Approaching" -> TransitSaffronSecondary
                    "Boarding at Terminal" -> TransitNavyPrimary
                    else -> Color(0xFF334155)
                  },
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(trip.occupancy.colorHex))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = trip.occupancy.label,
                  fontSize = 10.sp,
                  color = Color(trip.occupancy.colorHex),
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }
    }
  }
}
