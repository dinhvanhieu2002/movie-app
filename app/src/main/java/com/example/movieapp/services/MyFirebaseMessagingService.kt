package com.example.movieapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.movieapp.R
import com.example.movieapp.ui.activities.MainActivity
import com.example.movieapp.utils.ApiHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("Token", "Refresh Token $newToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        sendNotification(message.data)
    }
    val FRAGMENT_TO_LOAD = "FRAGMENT_TO_LOAD"

    private fun sendNotification(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("mediaId", data["mediaId"])
        intent.putExtra("mediaType", data["mediaType"])
        intent.putExtra("isScroll", true)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        Log.e("notification message", data["message"].toString())
        Log.e("notification title", data["title"].toString())
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = getString(R.string.default_notification_channel_id)

        //collapse
        val notificationLayout = RemoteViews(packageName, R.layout.layout_custom_notification)
        notificationLayout.setTextViewText(R.id.notificationTitle, data["title"].toString())
        notificationLayout.setTextViewText(R.id.notificationMessage, data["message"].toString())

        //expanded
        val notificationExpandedLayout = RemoteViews(packageName,
            R.layout.layout_custom_notification_expanded
        )
        Glide.with(this)
            .asBitmap()
            .load(ApiHelper.getPosterPath(data["poster"].toString()))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Log.e("Bitmap Uri", ApiHelper.getPosterPath(data["poster"].toString()))
                    notificationLayout.setImageViewBitmap(R.id.notificationImage, resource)
                    notificationExpandedLayout.setImageViewBitmap(R.id.notificationImageExpanded, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    notificationLayout.setImageViewResource(R.id.notificationImage, R.drawable.ic_launcher_foreground)
                    notificationExpandedLayout.setImageViewResource(R.id.notificationImageExpanded, R.drawable.ic_launcher_foreground)
                }
            })

        notificationExpandedLayout.setTextViewText(R.id.notificationTitleExpanded, data["title"])
        notificationExpandedLayout.setTextViewText(R.id.notificationMessageExpanded, data["message"])


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your notification icon drawable
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationExpandedLayout)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

//        Glide.with(this)
//            .asBitmap()
//            .load(ApiHelper.getPosterPath(data["poster"].toString()))
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    notificationBuilder.setLargeIcon(resource)
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//
//                }
//            })

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since Android Oreo, a notification channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel title", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}