package com.example.rosavtodorproject2.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.rosavtodorproject2.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.GregorianCalendar

class AlertsMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notification = remoteMessage.notification
        val title = notification?.title
        val message = notification?.body

        Log.d("MyFirebaseService", "Message received: $title - $message")
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

        val notificationManager = getSystemService(NotificationManager::class.java)

        val notificationBuilder = NotificationCompat.Builder(context, "notif")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build())
        Log.d("MyFirebaseService", "Message sended SUCCSESFULLY")
    }

}