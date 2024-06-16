package com.example.rosavtodorproject2.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.rosavtodorproject2.R
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AlertsMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notification = remoteMessage.notification
        val title = notification?.title
        val message = notification?.body

        Firebase.analytics.logEvent("Message_received:_$title - $message", null)
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

        notificationManager.notify(3 /* ID of notification */, notificationBuilder.build())
        Firebase.analytics.logEvent("Message_sended", null)
    }

    companion object {
        val obl_name_to_filer_value =
            mutableMapOf(
                Pair("Челябинской области", true),
                Pair("Курганской области", true)
            )
        val road_name_to_filer_value = mutableMapOf(
            Pair("Дороги М5-ПКЕ", true),
            Pair("Дороги А-310", true),
            Pair("Дороги М5-ПКЕ", true),
            Pair("Дороги А-310", true),
        )
    }
}