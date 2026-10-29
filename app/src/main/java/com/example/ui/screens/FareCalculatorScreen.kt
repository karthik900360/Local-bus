package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusOperatorType
import com.example.data.model.BusRoute
import com.example.data.model.BusStopSchedule
import com.example.data.model.FareEstimate
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FareCalculatorScreen(
  route: BusRoute,
  fareEstimate: FareEstimate?,
  selectedOrigin: BusStopSchedule?,
  selectedDestination: BusStopSchedule?,
  onOriginSelected: (BusStopSchedule) -> Unit,
  onDestinationSelected: (BusStopSchedule) -> Unit,
  onSwapStops: () -> Unit,
  onGenerateDigitalTicket: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var originExpanded by remember { mutableStateOf(false) }
  var destExpanded by remember { mutableStateOf(false) }
  var selectedTier by remember { mutableStateOf("GOVT_ORDINARY") } // "GOVT_ORDINARY", "PVT_ORDINARY", "EXPRESS", "AC_LOWFLOOR", "WOMEN_FREE"

  val isGovt = route.operatorType == BusOperatorType.GOVERNMENT

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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("fare_header_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Ticket Cost Calculator",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Surface(
              color = RapidoYellow,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "STAGE TARIFF",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = RapidoBlack,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Official Government (TNSTC) & Private Bus Fare Matrix",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f)
          )
        }
      }
    }

    // Stop Selector Card (Rapido pickup to drop layout)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("fare_selector_form")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Pickup Stop
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TrafficGreen))
            Spacer(modifier = Modifier.width(8.dp))
            Text("CURRENT LOCATION (BOARDING STOP)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TrafficGreen)
          }
          Spacer(modifier = Modifier.height(4.dp))
          ExposedDropdownMenuBox(
            expanded = originExpanded,
            onExpandedChange = { originExpanded = !originExpanded },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedOrigin?.let { "${it.stopSequence}. ${it.stopName}" } ?: "Select Boarding Stop",
              onValueChange = {},
              readOnly = true,
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = originExpanded) },
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF6F7FA),
                unfocusedContainerColor = Color(0xFFF6F7FA)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .testTag("fare_origin_dropdown")
            )
            ExposedDropdownMenu(
              expanded = originExpanded,
              onDismissRequest = { originExpanded = false }
            ) {
              route.stops.forEach { stop ->
                DropdownMenuItem(
                  text = { Text("${stop.stopSequence}. ${stop.stopName} (${stop.distanceFromOriginKm} km)") },
                  onClick = {
                    onOriginSelected(stop)
                    originExpanded = false
                  }
                )
              }
            }
          }

          // Swap icon row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            IconButton(
              onClick = onSwapStops,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(RapidoYellowLight)
                .testTag("fare_swap_stops_button")
            ) {
              Icon(Icons.Default.SwapVert, contentDescription = "Swap Origin and Destination", tint = RapidoBlack)
            }
          }

          // Destination Stop
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(TrafficRed))
            Spacer(modifier = Modifier.width(8.dp))
            Text("DESIRED DESTINATION (DROP STOP)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TrafficRed)
          }
          Spacer(modifier = Modifier.height(4.dp))
          ExposedDropdownMenuBox(
            expanded = destExpanded,
            onExpandedChange = { destExpanded = !destExpanded },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedDestination?.let { "${it.stopSequence}. ${it.stopName}" } ?: "Select Destination Stop",
              onValueChange = {},
              readOnly = true,
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destExpanded) },
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF6F7FA),
                unfocusedContainerColor = Color(0xFFF6F7FA)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .testTag("fare_destination_dropdown")
            )
            ExposedDropdownMenu(
              expanded = destExpanded,
              onDismissRequest = { destExpanded = false }
            ) {
              route.stops.forEach { stop ->
                DropdownMenuItem(
                  text = { Text("${stop.stopSequence}. ${stop.stopName} (${stop.distanceFromOriginKm} km)") },
                  onClick = {
                    onDestinationSelected(stop)
                    destExpanded = false
                  }
                )
              }
            }
          }
        }
      }
    }

    // Fare Results & Bus Operator Fare Comparison Cards
    if (fareEstimate != null) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth().testTag("fare_breakdown_card")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Distance, Stops & ETA Header
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF6F7FA))
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "🚏 ${fareEstimate.stopsTraversed} Stops will stop",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoBlack
              )
              Text(
                text = "🛣️ ${fareEstimate.distanceKm} km",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RapidoBlack
              )
              Text(
                text = "⏱️ ~${fareEstimate.approxTravelTimeMin}m ride",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TrafficGreen
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Compare Ticket Cost (Government vs Private)",
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              color = RapidoBlack
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: Tamil Nadu Government Ordinary Bus (Vidiyal Payanam: Women Free!)
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (selectedTier == "GOVT_ORDINARY") RapidoYellowLight else Color.White
              ),
              border = androidx.compose.foundation.BorderStroke(
                width = if (selectedTier == "GOVT_ORDINARY") 2.dp else 1.dp,
                color = if (selectedTier == "GOVT_ORDINARY") RapidoYellowDark else CardBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedTier = "GOVT_ORDINARY" }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0D47A1)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TNSTC Government Bus", fontSize = 14.sp, fontWeight = FontWeight.Black, color = RapidoBlack)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE8F5E9)) {
                      Text("Standard", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TrafficGreen, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                  }
                  Text("Stage fare • Women Free (Vidiyal Payanam)", fontSize = 11.sp, color = Color(0xFFC2185B), fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "₹${fareEstimate.governmentOrdinaryFare}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = RapidoBlack
                  )
                  Text("Free for 👩", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2185B))
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 2: Private Bus Operator (Coimbatore Town Service)
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (selectedTier == "PVT_ORDINARY") RapidoYellowLight else Color.White
              ),
              border = androidx.compose.foundation.BorderStroke(
                width = if (selectedTier == "PVT_ORDINARY") 2.dp else 1.dp,
                color = if (selectedTier == "PVT_ORDINARY") RapidoYellowDark else CardBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedTier = "PVT_ORDINARY" }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE65100)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Private Bus Service", fontSize = 14.sp, fontWeight = FontWeight.Black, color = RapidoBlack)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFFF3E0)) {
                      Text("High Punctuality", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TrafficAmber, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                  }
                  Text("Sri Krishna, Cheran, Hindusthan • Cushion seats", fontSize = 11.sp, color = RapidoTextMuted)
                }
                Text(
                  text = "₹${fareEstimate.privateOrdinaryFare}",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black,
                  color = RapidoBlack
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 3: Express / Fast Track
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (selectedTier == "EXPRESS") RapidoYellowLight else Color.White
              ),
              border = androidx.compose.foundation.BorderStroke(
                width = if (selectedTier == "EXPRESS") 2.dp else 1.dp,
                color = if (selectedTier == "EXPRESS") RapidoYellowDark else CardBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedTier = "EXPRESS" }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(RapidoBlack),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = RapidoYellow, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Express Fast Track", fontSize = 14.sp, fontWeight = FontWeight.Black, color = RapidoBlack)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF1F5F9)) {
                      Text("Faster", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                  }
                  Text("Limited stops • Avinashi Road flyover priority", fontSize = 11.sp, color = RapidoTextMuted)
                }
                Text(
                  text = "₹${fareEstimate.expressFare}",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black,
                  color = RapidoBlack
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 4: AC Low Floor / Airport Flyer
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (selectedTier == "AC_LOWFLOOR") RapidoYellowLight else Color.White
              ),
              border = androidx.compose.foundation.BorderStroke(
                width = if (selectedTier == "AC_LOWFLOOR") 2.dp else 1.dp,
                color = if (selectedTier == "AC_LOWFLOOR") RapidoYellowDark else CardBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedTier = "AC_LOWFLOOR" }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF00897B)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.AcUnit, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("AC Low Floor Deluxe", fontSize = 14.sp, fontWeight = FontWeight.Black, color = RapidoBlack)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE0F2FE)) {
                      Text("Air-Conditioned", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                  }
                  Text("Chilled AC • Airport & IT Corridor", fontSize = 11.sp, color = RapidoTextMuted)
                }
                Text(
                  text = "₹${fareEstimate.acVajraFare}",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black,
                  color = RapidoBlack
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Concession Passes Strip
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F3F7),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Student Pass", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RapidoTextMuted)
                  Text("₹${fareEstimate.studentConcessionFare}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = RapidoBlack)
                  Text("50% Concession", fontSize = 9.sp, color = TrafficGreen, fontWeight = FontWeight.Bold)
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F3F7),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Senior Citizen", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RapidoTextMuted)
                  Text("₹${fareEstimate.seniorCitizenFare}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = RapidoBlack)
                  Text("50% Concession", fontSize = 9.sp, color = TrafficGreen, fontWeight = FontWeight.Bold)
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = RapidoYellowLight,
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text("Day Pass", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RapidoBlack)
                  Text("₹${fareEstimate.dailyPassPrice}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = RapidoBlack)
                  Text("Unlimited Kovai", fontSize = 9.sp, color = RapidoBlack, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Signature Rapido Yellow Action Button
            ElevatedButton(
              onClick = { onGenerateDigitalTicket(selectedTier) },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.elevatedButtonColors(
                containerColor = RapidoYellow,
                contentColor = RapidoBlack
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_qr_ticket_button")
            ) {
              Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Generate Boarding Pass / QR Ticket", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
          }
        }
      }
    }
  }
}
