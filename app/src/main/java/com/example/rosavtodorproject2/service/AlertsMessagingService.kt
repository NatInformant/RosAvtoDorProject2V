package com.example.rosavtodorproject2.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.rosavtodorproject2.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.GregorianCalendar

class AlertsMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        val notification = remoteMessage.notification
        val title = notification?.title
        val message = notification?.body

        if (title != null && message != null) {
            sendNotification(
                title,
                message,
                applicationContext
            )
        }
    }

    private fun sendNotification(
        title: String?,
        message: String?,
        context: Context
    ) {
        val current = GregorianCalendar.getInstance()

        // Channel is a must on android 8+
        val notificationChannel = "chat_notifications"

        val notificationBuilder = NotificationCompat.Builder(context, "notif")
            .setSmallIcon(getNotificationIcon())
            .setContentTitle(title)
            .setContentText(message)
            .setWhen(current.getTimeInMillis())
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.black))
            .setChannelId(notificationChannel)

        // In case we want to setup notification piority
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH)
        } else {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
        }


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                notificationChannel,
                context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun getNotificationIcon(): Int {
        return R.mipmap.ic_launcher
    }
}