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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
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
import com.example.data.model.TrafficAlert
import com.example.data.model.TrafficSeverity
import com.example.ui.theme.CardBorder
import com.example.ui.theme.PlateText
import com.example.ui.theme.PlateYellow
import com.example.ui.theme.RapidoBlack
import com.example.ui.theme.RapidoCharcoal
import com.example.ui.theme.RapidoTextMuted
import com.example.ui.theme.RapidoYellow
import com.example.ui.theme.TrafficAmber
import com.example.ui.theme.TrafficRed

@Composable
fun TrafficAlertsScreen(
  alerts: List<TrafficAlert>,
  onTestPushNotification: () -> Unit,
  modifier: Modifier = Modifier
) {
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
        modifier = Modifier.fillMaxWidth().testTag("traffic_alerts_header")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Live Traffic Radar",
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            Surface(
              color = TrafficRed,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "${alerts.size} BOTTLENECKS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Real-time congestion alerts and delay impact across city corridors",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Rapido Yellow Test Button
          ElevatedButton(
            onClick = onTestPushNotification,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.elevatedButtonColors(
              containerColor = RapidoYellow,
              contentColor = RapidoBlack
            ),
            modifier = Modifier.fillMaxWidth().testTag("test_push_notification_btn")
          ) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Test Push Alert Now", fontWeight = FontWeight.Black, fontSize = 13.sp)
          }
        }
      }
    }

    // Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Reported Congestion Corridors",
          fontSize = 15.sp,
          fontWeight = FontWeight.ExtraBold,
          color = RapidoBlack
        )
        Text(
          text = "Live feeds",
          fontSize = 11.sp,
          color = RapidoTextMuted
        )
      }
    }

    // Alerts List
    items(alerts, key = { it.id }) { alert ->
      val isHeavy = alert.severity == TrafficSeverity.HEAVY

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("traffic_alert_card_${alert.id}")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Top Row: Number Plate & Delay Badge
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = PlateYellow,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
              ) {
                Text(
                  text = "ROUTE ${alert.routeNumber}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  fontFamily = FontFamily.Monospace,
                  color = PlateText,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = alert.city,
                fontSize = 11.sp,
                color = RapidoTextMuted,
                fontWeight = FontWeight.Medium
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isHeavy) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = null,
                  tint = if (isHeavy) TrafficRed else TrafficAmber,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "+${alert.delayMinutes}m Delay",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isHeavy) TrafficRed else TrafficAmber
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Location & Title
          Text(
            text = alert.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RapidoBlack
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "📍 ${alert.locationName}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = RapidoBlack
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = alert.description,
            fontSize = 12.sp,
            color = Color(0xFF475569),
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Footer
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFF6F7FA))
              .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Severity: ${alert.severity.label}",
              fontSize = 11.sp,
              color = if (isHeavy) TrafficRed else TrafficAmber,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Reported ${alert.reportedTimeAgo}",
              fontSize = 10.sp,
              color = RapidoTextMuted
            )
          }
        }
      }
    }
  }
}
