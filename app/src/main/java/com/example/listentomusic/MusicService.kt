package com.example.listentomusic

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.listentomusic.model.Music

class MusicService:Service() {
    private val binder = MusicBinder()
    private lateinit var mService:MusicService
    private var mediaPlayer: MediaPlayer? = null
    public var isPlaying = false
    public var musicIsPlaying: String =""
    public var isLooping = false

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        Log.e(this.javaClass.simpleName,"create service")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(this.javaClass.simpleName,"start command service")
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public fun startMusic(music: Music)
    {
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
            musicIsPlaying = music.link
            isPlaying = true
        }
        else if(isPlaying == true)
        {
            if(music.link != musicIsPlaying)
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
                musicIsPlaying = music.link
                isPlaying = true
            }
        }

    }

    public fun pauseMusic()
    {
        if(isPlaying == true)
        {
            mediaPlayer?.pause()
            isPlaying = false
        }
    }

    public fun playMusic()
    {
        if(isPlaying == false)
        {
            mediaPlayer?.start()
            Toast.makeText(this,"play music",Toast.LENGTH_SHORT).show()
            isPlaying = true
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

}