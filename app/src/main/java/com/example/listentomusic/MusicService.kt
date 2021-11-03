package com.example.listentomusic

import android.app.Notification.VISIBILITY_PUBLIC
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.media.app.NotificationCompat
import com.example.listentomusic.model.Music
import android.R
import android.app.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.listentomusic.utils.*
import java.time.temporal.TemporalAdjusters.next


class MusicService:Service() {
    private val binder = MusicBinder()
    private lateinit var mService:MusicService
    private var mediaPlayer: MediaPlayer? = null

    public var isPlaying = false
    public var liveDataIsPlaying = MutableLiveData<Boolean>()

    public var liveDataMusicIsPlaying = MutableLiveData<Boolean>()
    public lateinit var musicIsPlaying: Music
    public var isLooping = false

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        Log.e(this.javaClass.simpleName,"create service")
        liveDataIsPlaying.value= isPlaying
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(this.javaClass.simpleName,intent?.getIntExtra("action",0).toString())

        var action = intent?.getIntExtra("action",0)
        when(action)
        {
            ACTION_NEXT -> {

            }
            ACTION_PREV->{

            }
            ACTION_PAUSE->{
                pauseMusic()
                Log.e(this.javaClass.simpleName,liveDataIsPlaying.value.toString())
            }
            ACTION_PLAY->{
                playMusic()
                Log.e(this.javaClass.simpleName,liveDataIsPlaying.value.toString())
            }
        }
        return START_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        Log.e(this.javaClass.simpleName,"unbind")
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mediaPlayer !=null)
        {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        Log.e(this.javaClass.simpleName,"destroy servicce")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    public fun startMusic(music: Music)
    {
        sendNotification(music, true)
        if(isPlaying == false)
        {
            mediaPlayer = null
            mediaPlayer?.setDataSource(music.link)
            mediaPlayer?.start()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                        AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                )
                setDataSource(music.link)
                prepare() // might take long! (for buffering, etc)
                start()
            }
            musicIsPlaying = music
            isPlaying = true
            liveDataIsPlaying.value = isPlaying

        }
        else if(isPlaying == true)
        {
            if(music.link != musicIsPlaying.link)
            {
                mediaPlayer?.stop()
                mediaPlayer = null
                mediaPlayer?.setDataSource(music.link)
                mediaPlayer?.start()
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                            AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    )
                    setDataSource(music.link)
                    prepare() // might take long! (for buffering, etc)
                    start()
                }
                musicIsPlaying = music
                isPlaying = true
                liveDataIsPlaying.value = isPlaying
            }
        }

    }

    public fun pauseMusic()
    {
        if(isPlaying == true)
        {
            mediaPlayer?.pause()
            isPlaying = false
            liveDataIsPlaying.value = isPlaying
            sendNotification(musicIsPlaying, false)
        }
    }

    public fun playMusic()
    {
        if(isPlaying == false)
        {
            mediaPlayer?.start()
            Toast.makeText(this,"play music",Toast.LENGTH_SHORT).show()
            isPlaying = true
            liveDataIsPlaying.value = isPlaying
            sendNotification(musicIsPlaying, true)
        }
    }

    public fun getCurrenPosition(): Int? {
        return mediaPlayer?.currentPosition
    }

    public fun getDuration(): Int? {
        return mediaPlayer?.duration
    }

    public fun seekTo(progress:Int)
    {
        mediaPlayer?.seekTo(progress)
    }

    public fun changeLoopStatus()
    {
        if(mediaPlayer?.isLooping == true) {
            mediaPlayer?.isLooping = false
            isLooping = false
        }
        else {
            mediaPlayer?.isLooping = true
            isLooping = true
        }

    }

    public fun stopMusic()
    {
        mediaPlayer?.stop()
        isPlaying = false
        Toast.makeText(this,"stop music",Toast.LENGTH_SHORT).show()

    }

    inner class MusicBinder:Binder(){
        fun getService():MusicService = this@MusicService

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(music:Music,isplaying:Boolean)
    {
        val notificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(music,isplaying,this)


    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel_1"
            val descriptionText = "This is channel 1"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel("CHANNEL 1", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

        }
    }

}
