package com.example.listentomusic.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.listentomusic.MainActivity
import com.example.listentomusic.MusicReceiver
import com.example.listentomusic.R
import com.example.listentomusic.model.Music


    private val NOTIFICATION_ID =0

    val ACTION_PREV :Int=0
    val ACTION_PAUSE:Int=1
    val ACTION_NEXT :Int=2
    val ACTION_PLAY :Int=3


fun NotificationManager.sendNotification( music: Music, isplaying:Boolean, applicationContext: Context) {

    var mediaSessionCompat = MediaSessionCompat(applicationContext,"tag")

    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val musicImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.disc
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(musicImage)
        .bigLargeIcon(null)

    val prevIntent = Intent(applicationContext, MusicReceiver::class.java)
    prevIntent.putExtra("action", 0)
    val prevPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        ACTION_PREV,
        prevIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)

    val pauseIntent = Intent(applicationContext, MusicReceiver::class.java)
    pauseIntent.putExtra("action", 1)
    val pausePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        ACTION_PAUSE,
        pauseIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)

    val playIntent = Intent(applicationContext, MusicReceiver::class.java)
    playIntent.putExtra("action", 3)
    val playPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        ACTION_PLAY,
        playIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)

    val nextIntent = Intent(applicationContext, MusicReceiver::class.java)
    nextIntent.putExtra("action", 2)
    val nextPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        ACTION_NEXT,
        nextIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)



    val builder = NotificationCompat.Builder(
        applicationContext,
        "CHANNEL 1"
    )

        .setSmallIcon(R.drawable.disc) // chua co icon
        .setContentTitle("notification")
        .setContentText(music.name)

        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)


        .setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
//                .setMediaSession(mediaSessionCompat.sessionToken)
        )

        .setLargeIcon(musicImage)

        .setPriority(NotificationCompat.PRIORITY_LOW)

    if(isplaying)
    {
        builder.addAction(
            R.drawable.prev,
            "prev",
            prevPendingIntent
        )
        .addAction(
            R.drawable.pause,
            "pause",
            pausePendingIntent
        )
        .addAction(
            R.drawable.next,
            "next",
            nextPendingIntent
        )
    }
    else
    {
        builder.addAction(
            R.drawable.prev,
            "prev",
            prevPendingIntent
        )
        .addAction(
            R.drawable.play,
            "play",
            playPendingIntent

        )
        .addAction(
            R.drawable.next,
            "next",
            nextPendingIntent
        )
    }


    notify(NOTIFICATION_ID, builder.build())
}


