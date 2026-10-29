package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusOperatorType
import com.example.data.model.BusRoute
import com.example.data.model.LiveBus
import com.example.data.model.OccupancyStatus
import com.example.data.model.TrafficSeverity
import com.example.ui.theme.*

@Composable
fun LiveMapScreen(
  route: BusRoute,
  liveBus: LiveBus?,
  isTrackingPaused: Boolean,
  showTrafficOverlay: Boolean,
  onToggleTracking: () -> Unit,
  onToggleTrafficOverlay: () -> Unit,
  onTriggerNotificationAlert: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1.7f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_scale"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.7f,
    targetValue = 0.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_alpha"
  )

  val isGovt = route.operatorType == BusOperatorType.GOVERNMENT
  val isCoimbatore = route.city.contains("Coimbatore")

  Box(modifier = modifier.fillMaxSize().background(Color(0xFFE2E7EE))) {
    // Custom Interactive Coimbatore-Based Map Canvas
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .testTag("interactive_bus_map_canvas")
    ) {
      val canvasWidth = size.width
      val canvasHeight = size.height

      // Draw subtle city transit grid roads
      val gridSpacing = 52.dp.toPx()
      for (x in 0..(canvasWidth / gridSpacing).toInt()) {
        drawLine(
          color = Color(0xFFD0D7E2),
          start = Offset(x * gridSpacing, 0f),
          end = Offset(x * gridSpacing, canvasHeight),
          strokeWidth = 1.dp.toPx()
        )
      }
      for (y in 0..(canvasHeight / gridSpacing).toInt()) {
        drawLine(
          color = Color(0xFFD0D7E2),
          start = Offset(0f, y * gridSpacing),
          end = Offset(canvasWidth, y * gridSpacing),
          strokeWidth = 1.dp.toPx()
        )
      }

      // Draw Coimbatore Geographic Features if Coimbatore route
      if (isCoimbatore) {
        // 1. Ukkadam Periyakulam / Valankulam Lake blue polygon
        drawOval(
          color = Color(0xFFB3E5FC),
          topLeft = Offset(canvasWidth * 0.15f, canvasHeight * 0.52f),
          size = Size(canvasWidth * 0.28f, canvasHeight * 0.12f)
        )
        // 2. Western Ghats / Marudhamalai foothills hill shade on left
        val mountainPath = Path().apply {
          moveTo(0f, canvasHeight * 0.15f)
          lineTo(canvasWidth * 0.18f, canvasHeight * 0.22f)
          lineTo(canvasWidth * 0.10f, canvasHeight * 0.32f)
          lineTo(0f, canvasHeight * 0.38f)
          close()
        }
        drawPath(mountainPath, color = Color(0xFFC8E6C9).copy(alpha = 0.6f))

        // 3. Avinashi Road Elevated Corridor strip
        drawLine(
          color = Color(0xFFCBD5E1),
          start = Offset(canvasWidth * 0.35f, canvasHeight * 0.28f),
          end = Offset(canvasWidth * 0.95f, canvasHeight * 0.35f),
          strokeWidth = 6.dp.toPx()
        )
      }

      // Compute coordinate bounds to project route onto canvas
      val stops = route.stops
      if (stops.size >= 2) {
        val minLat = stops.minOf { it.latitude }
        val maxLat = stops.maxOf { it.latitude }
        val minLng = stops.minOf { it.longitude }
        val maxLng = stops.maxOf { it.longitude }

        val latSpan = (maxLat - minLat).coerceAtLeast(0.0008)
        val lngSpan = (maxLng - minLng).coerceAtLeast(0.0008)

        val paddingX = 60.dp.toPx()
        val paddingTop = 120.dp.toPx()
        val paddingBottom = 280.dp.toPx()
        val usableWidth = canvasWidth - (paddingX * 2)
        val usableHeight = canvasHeight - (paddingTop + paddingBottom)

        fun projectPoint(lat: Double, lng: Double): Offset {
          val normalizedX = ((lng - minLng) / lngSpan).toFloat()
          val normalizedY = (1.0f - ((lat - minLat) / latSpan).toFloat())
          return Offset(
            x = paddingX + (normalizedX * usableWidth),
            y = paddingTop + (normalizedY * usableHeight)
          )
        }

        // Draw Route Polyline
        val routePoints = stops.map { projectPoint(it.latitude, it.longitude) }

        val basePath = Path().apply {
          moveTo(routePoints.first().x, routePoints.first().y)
          for (i in 1 until routePoints.size) {
            val prev = routePoints[i - 1]
            val curr = routePoints[i]
            val midX = (prev.x + curr.x) / 2
            val midY = (prev.y + curr.y) / 2
            quadraticTo(prev.x, prev.y, midX, midY)
          }
          lineTo(routePoints.last().x, routePoints.last().y)
        }

        // Dark road casing (Rapido dark contrast)
        drawPath(
          path = basePath,
          color = Color(0xFF1E222A),
          style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Road surface
        drawPath(
          path = basePath,
          color = Color(0xFFFFFFFF),
          style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Traffic overlay
        if (showTrafficOverlay) {
          for (i in 0 until routePoints.size - 1) {
            val stop = stops[i]
            val segmentColor = when (stop.trafficCondition) {
              TrafficSeverity.NORMAL -> TrafficGreen
              TrafficSeverity.MODERATE -> TrafficAmber
              TrafficSeverity.HEAVY -> TrafficRed
            }
            drawLine(
              color = segmentColor.copy(alpha = 0.95f),
              start = routePoints[i],
              end = routePoints[i + 1],
              strokeWidth = 5.dp.toPx(),
              cap = StrokeCap.Round
            )
          }
        }

        // Draw stop pins with stop numbers (#1, #2... #N)
        stops.forEachIndexed { index, stop ->
          val point = routePoints[index]
          val isOrigin = index == 0
          val isDest = index == stops.lastIndex
          val isCurrentPassed = liveBus != null && index <= liveBus.currentStopIndex

          drawCircle(
            color = Color.White,
            radius = if (isOrigin || isDest) 11.dp.toPx() else 7.dp.toPx(),
            center = point
          )
          drawCircle(
            color = when {
              isOrigin -> TrafficGreen
              isDest -> TrafficRed
              isCurrentPassed -> RapidoYellowDark
              else -> RapidoBlack
            },
            radius = if (isOrigin || isDest) 8.dp.toPx() else 5.dp.toPx(),
            center = point
          )
        }

        // Draw Live Bus GPS Pin with Rapido Yellow beacon & Direction Arrow
        if (liveBus != null) {
          val busPoint = projectPoint(liveBus.latitude, liveBus.longitude)

          // Glowing Rapido Yellow Radar wave
          drawCircle(
            color = RapidoYellow.copy(alpha = pulseAlpha),
            radius = 30.dp.toPx() * pulseScale,
            center = busPoint
          )

          // Black contrast ring
          drawCircle(
            color = RapidoBlack,
            radius = 18.dp.toPx(),
            center = busPoint
          )
          // Solid Rapido Yellow core
          drawCircle(
            color = RapidoYellow,
            radius = 14.dp.toPx(),
            center = busPoint
          )
          // Inner black dot
          drawCircle(
            color = RapidoBlack,
            radius = 5.dp.toPx(),
            center = busPoint
          )
        }
      }
    }

    // Top Floating Navigation Bar (Rapido style with Operator & Stop Counter)
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = RapidoBlack),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .align(Alignment.TopCenter)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = PlateYellow,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
          ) {
            Text(
              text = route.routeNumber,
              color = PlateText,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (isGovt) "🏛️ TNSTC Govt" else "🚌 Private Bus",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RapidoYellow
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• ${route.stops.size} Stops",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.8f)
              )
            }
            Text(
              text = "${route.origin.take(14)} ➔ ${route.destination.take(14)}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Traffic toggle
          IconButton(
            onClick = onToggleTrafficOverlay,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (showTrafficOverlay) RapidoYellow else RapidoCharcoal)
              .testTag("toggle_traffic_overlay")
          ) {
            Icon(
              imageVector = Icons.Default.Traffic,
              contentDescription = "Traffic overlay",
              tint = if (showTrafficOverlay) RapidoBlack else Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          // Play / Pause simulation
          IconButton(
            onClick = onToggleTracking,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (isTrackingPaused) TrafficRed else RapidoCharcoal)
              .testTag("toggle_tracking_play_pause")
          ) {
            Icon(
              imageVector = if (isTrackingPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
              contentDescription = "Play or Pause",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // Rapido-Style Floating Ride Bottom Sheet with Real-Time Stop Counters & Operator
    Card(
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .testTag("live_bus_telemetry_card")
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        // Drag handle indicator
        Box(
          modifier = Modifier
            .width(36.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFFE0E0E0))
            .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Vehicle Plate, Operator Name & ETA
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Yellow transport license plate
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = PlateYellow,
              border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
            ) {
              Text(
                text = liveBus?.vehicleNumber ?: "TN-38-N-2419",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = PlateText,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (isGovt) Color(0xFFE3F2FD) else Color(0xFFFFF3E0)
            ) {
              Text(
                text = if (isGovt) "TNSTC Govt" else (liveBus?.operatorName ?: "Private Bus"),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGovt) Color(0xFF0D47A1) else Color(0xFFE65100),
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }

          // Live ETA Pill
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = RapidoYellow
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${liveBus?.etaNextStopMin ?: 2} MIN",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = RapidoBlack
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text("ETA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = RapidoBlack)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Approaching Stop Info & Remaining Stops Counter
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TrafficGreen))
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "APPROACHING STOP (WHERE BUS IS COMING)",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = TrafficGreen,
              letterSpacing = 0.5.sp
            )
            Text(
              text = liveBus?.nextStopName ?: "Locating next stop...",
              fontSize = 15.sp,
              fontWeight = FontWeight.ExtraBold,
              color = RapidoBlack,
              maxLines = 1
            )
          }

          // Stops Remaining Counter
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF1F5F9)
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalAlignment = Alignment.End
            ) {
              Text(
                text = "${liveBus?.remainingStopsCount ?: (route.stops.size - 1)} STOPS LEFT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = RapidoBlack
              )
              Text(
                text = "of ${route.stops.size} Total Stops",
                fontSize = 9.sp,
                color = Color(0xFF64748B)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Telemetry Details: Speed, Occupancy, Delay & Ticket Cost
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF6F7FA))
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Speed
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Speed, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${liveBus?.speedKmph ?: 32} km/h",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = RapidoBlack
            )
          }

          // Occupancy Status
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(liveBus?.occupancy?.colorHex ?: OccupancyStatus.SEATS_AVAILABLE.colorHex).copy(alpha = 0.15f)
          ) {
            Text(
              text = liveBus?.occupancy?.label ?: "Seats Available",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(liveBus?.occupancy?.colorHex ?: OccupancyStatus.SEATS_AVAILABLE.colorHex),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          // Ticket Cost Info
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = RapidoYellowLight
          ) {
            Text(
              text = if (route.isWomenFreeTravel) "Ticket: ₹5 (Women ₹0)" else "Ticket: ₹6 - ₹15",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = RapidoBlack,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Signature Rapido Yellow Action Button (Push Notification Alert)
        ElevatedButton(
          onClick = onTriggerNotificationAlert,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = RapidoYellow,
            contentColor = RapidoBlack
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("push_notification_alert_button")
        ) {
          Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = RapidoBlack, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Notify Me on Arrival (Push Notification)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}
