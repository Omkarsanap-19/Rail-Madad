package com.example.railmadad

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FirebaseMessaging", "From: ${message.notification?.title}")
        Log.d("FirebaseMessaging", "Message data: ${message.notification?.body}")

        if (message.notification != null){
            showNotification(message.notification!!.title , message.notification!!.body)
        }


    }

    fun getCustomViews(title: String? , body: String?): RemoteViews{
        val remoteView = RemoteViews("com.example.railmadad",R.layout.custom_notificaton)
        remoteView.setTextViewText(R.id.desc,body)
        remoteView.setTextViewText(R.id.title,title)

        return remoteView
    }

    private fun showNotification(title: kotlin.String?, body: kotlin.String?) {
    val intent = Intent(this, MainActivity::class.java)
        val channelId = "notification_channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )

            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(
            getCustomViews(title,body)
        )

        val notificationManager : NotificationManager? = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificatoinChannel = NotificationChannel(channelId,"Notify_app",
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager!!.createNotificationChannel(notificatoinChannel)
        }

        notificationManager!!.notify(0,builder.build())

    }

}