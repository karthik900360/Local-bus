package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RapidoBlack
import com.example.ui.theme.RapidoCharcoal
import com.example.ui.theme.RapidoYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransitTopBar(
  title: String,
  selectedCity: String,
  notificationCount: Int,
  onNotificationClick: () -> Unit,
  onCityClick: () -> Unit
) {
  TopAppBar(
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Rapido-style bright yellow bus badge
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(RapidoYellow),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.DirectionsBus,
            contentDescription = "Yatri Bus Logo",
            tint = RapidoBlack,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "YATRI",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 17.sp,
              color = Color.White,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = RapidoYellow
            ) {
              Text(
                text = "BUS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = RapidoBlack,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }

          // City switcher chip
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = RapidoCharcoal,
            modifier = Modifier
              .clickable { onCityClick() }
              .testTag("topbar_city_chip")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = selectedCity,
                fontSize = 11.sp,
                color = RapidoYellow,
                fontWeight = FontWeight.Bold
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = RapidoYellow,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    },
    actions = {
      IconButton(
        onClick = onNotificationClick,
        modifier = Modifier.testTag("notification_bell_button")
      ) {
        BadgedBox(
          badge = {
            if (notificationCount > 0) {
              Badge(
                containerColor = RapidoYellow,
                contentColor = RapidoBlack
              ) {
                Text(
                  text = notificationCount.coerceAtMost(99).toString(),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(RapidoCharcoal),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (notificationCount > 0) Icons.Filled.Notifications else Icons.Outlined.Notifications,
              contentDescription = "Push Alerts History",
              tint = if (notificationCount > 0) RapidoYellow else Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = RapidoBlack,
      titleContentColor = Color.White
    )
  )
}
