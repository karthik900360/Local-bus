package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.PushNotificationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationHelper {
  private const val CHANNEL_ID = "bus_transit_channel"
  private const val CHANNEL_NAME = "Yatri Bus Transit & Delay Alerts"

  private val _notificationsHistory = MutableStateFlow<List<PushNotificationEvent>>(emptyList())
  val notificationsHistory: StateFlow<List<PushNotificationEvent>> = _notificationsHistory.asStateFlow()

  fun init(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Live notifications for bus arrival, departure, and traffic delays"
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 250, 150, 250)
      }
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      manager?.createNotificationChannel(channel)
    }
  }

  fun triggerPushNotification(
    context: Context,
    title: String,
    message: String,
    type: String = "ARRIVAL"
  ) {
    val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    val event = PushNotificationEvent(
      id = System.currentTimeMillis().toString(),
      title = title,
      message = message,
      timestamp = timeStr,
      type = type
    )

    // Store in history
    _notificationsHistory.value = listOf(event) + _notificationsHistory.value

    // Post to Android system tray if permission permitted
    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }

    if (hasPermission) {
      try {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
          .setSmallIcon(R.mipmap.ic_launcher)
          .setContentTitle(title)
          .setContentText(message)
          .setStyle(NotificationCompat.BigTextStyle().bigText(message))
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setAutoCancel(true)
          .setVibrate(longArrayOf(0, 250, 150, 250))
          .build()

        manager?.notify(System.currentTimeMillis().toInt(), notification)
      } catch (e: Exception) {
        // Fallback gracefully
      }
    }
  }

  fun clearAllNotifications() {
    _notificationsHistory.value = emptyList()
  }
}
