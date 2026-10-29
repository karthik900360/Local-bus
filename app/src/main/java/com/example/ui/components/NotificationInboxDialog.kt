package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PushNotificationEvent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.RapidoBlack
import com.example.ui.theme.RapidoCharcoal
import com.example.ui.theme.RapidoTextMuted
import com.example.ui.theme.RapidoYellow
import com.example.ui.theme.TrafficAmber
import com.example.ui.theme.TrafficRed
import com.example.util.NotificationHelper

@Composable
fun NotificationInboxDialog(
  notifications: List<PushNotificationEvent>,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("notification_inbox_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(RapidoYellow),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = RapidoBlack,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Alerts & Notifications (${notifications.size})",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = RapidoBlack
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = RapidoTextMuted)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (notifications.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No alerts yet. Set a bus reminder or traffic alert to receive real-time notifications.",
              fontSize = 13.sp,
              color = RapidoTextMuted,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(notifications, key = { it.id }) { alert ->
              val isDelay = alert.type == "DELAY"
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isDelay) Color(0xFFFFEBEE) else Color(0xFFF6F7FA)
                ),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Box(
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(if (isDelay) Color(0xFFFFCDD2) else RapidoYellow),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (isDelay) Icons.Default.Warning else Icons.Default.DirectionsBus,
                      contentDescription = null,
                      tint = if (isDelay) TrafficRed else RapidoBlack,
                      modifier = Modifier.size(16.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = alert.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RapidoBlack
                      )
                      Text(
                        text = alert.timestamp,
                        fontSize = 10.sp,
                        color = RapidoTextMuted
                      )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = alert.message,
                      fontSize = 11.sp,
                      color = Color(0xFF475569),
                      lineHeight = 15.sp
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (notifications.isNotEmpty()) {
            TextButton(
              onClick = { NotificationHelper.clearAllNotifications() }
            ) {
              Icon(Icons.Default.Delete, contentDescription = null, tint = TrafficRed, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Clear All", color = TrafficRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }

          TextButton(onClick = onDismiss) {
            Text("Close", fontWeight = FontWeight.Black, color = RapidoBlack)
          }
        }
      }
    }
  }
}
