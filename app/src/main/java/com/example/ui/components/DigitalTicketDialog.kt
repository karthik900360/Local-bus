package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BusOperatorType
import com.example.data.model.BusRoute
import com.example.data.model.FareEstimate
import com.example.ui.theme.PlateText
import com.example.ui.theme.PlateYellow
import com.example.ui.theme.RapidoBlack
import com.example.ui.theme.RapidoCharcoal
import com.example.ui.theme.RapidoTextMuted
import com.example.ui.theme.RapidoYellow
import com.example.ui.theme.TrafficGreen

@Composable
fun DigitalTicketDialog(
  route: BusRoute,
  fareEstimate: FareEstimate?,
  passType: String = "GOVT_ORDINARY",
  onDismiss: () -> Unit
) {
  val isGovt = route.operatorType == BusOperatorType.GOVERNMENT
  val isWomenFree = (passType == "WOMEN_FREE" || (isGovt && route.isWomenFreeTravel && passType == "GOVT_ORDINARY"))

  val displayPrice = when {
    isWomenFree -> "₹0.00 (Free)"
    passType == "PVT_ORDINARY" -> "₹${fareEstimate?.privateOrdinaryFare ?: 8}.00"
    passType == "EXPRESS" -> "₹${fareEstimate?.expressFare ?: 15}.00"
    passType == "AC_LOWFLOOR" || passType == "VAJRA" -> "₹${fareEstimate?.acVajraFare ?: 30}.00"
    else -> "₹${fareEstimate?.governmentOrdinaryFare ?: 5}.00"
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("digital_ticket_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top header with close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = TrafficGreen,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isWomenFree) "VIDIYAL PAYANAM PASS" else "CONFIRMED BOARDING PASS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = if (isWomenFree) Color(0xFFC2185B) else TrafficGreen,
              letterSpacing = 0.5.sp
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = RapidoTextMuted)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Operator Tag & Route Plate
        Row(verticalAlignment = Alignment.CenterVertically) {
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

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = PlateYellow,
          border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.Black)
        ) {
          Text(
            text = "ROUTE ${route.routeNumber}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = PlateText,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // From & To Details
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF6F7FA))
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("FROM (BOARDING)", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TrafficGreen)
            Text(
              text = fareEstimate?.fromStop ?: route.origin,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = RapidoBlack,
              maxLines = 1
            )
          }
          Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text("TO (DESTINATION)", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFE53935))
            Text(
              text = fareEstimate?.toStop ?: route.destination,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = RapidoBlack,
              maxLines = 1
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Simulated QR Code Graphic Canvas
        Box(
          modifier = Modifier
            .size(140.dp)
            .background(Color(0xFFF1F3F7), RoundedCornerShape(14.dp))
            .padding(10.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.size(110.dp)) {
            val cellCount = 12
            val cellSize = size.width / cellCount
            for (r in 0 until cellCount) {
              for (c in 0 until cellCount) {
                val isCorner = (r < 4 && c < 4) || (r < 4 && c >= cellCount - 4) || (r >= cellCount - 4 && c < 4)
                val fill = if (isCorner) {
                  (r == 0 || r == 3 || c == 0 || c == 3 || (r in 1..2 && c in 1..2))
                } else {
                  (r * 7 + c * 13 + 3) % 2 == 0
                }
                if (fill) {
                  drawRect(
                    color = RapidoBlack,
                    topLeft = Offset(c * cellSize, r * cellSize),
                    size = Size(cellSize - 1f, cellSize - 1f)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "TXN-${System.currentTimeMillis().toString().takeLast(8)} • ${route.permitNumber}",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          color = RapidoTextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Stops & Fare Paid Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Stops to Destination", fontSize = 10.sp, color = RapidoTextMuted)
            Text("${fareEstimate?.stopsTraversed ?: 4} Stops • ${fareEstimate?.distanceKm ?: 5.0} km", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RapidoBlack)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Fare Amount", fontSize = 10.sp, color = RapidoTextMuted)
            Text(
              text = displayPrice,
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = if (isWomenFree) Color(0xFFC2185B) else RapidoBlack
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Signature Rapido Yellow Done Button
        ElevatedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = RapidoYellow,
            contentColor = RapidoBlack
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
        ) {
          Text("Show to Conductor / Done", fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
      }
    }
  }
}
