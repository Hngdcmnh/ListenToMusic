package com.example.listentomusic

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.listentomusic.model.Music

class PlayMusicActivity : AppCompatActivity() {
    private var tvName: TextView? = null
    private var tvArtist: TextView? = null
    private var imgView: ImageView? = null
    private var playBtn: ImageButton? = null
    private var prevBtn: ImageButton? = null
    private var nextBtn: ImageButton? = null
    private var pauseBtn: ImageButton? = null
    private var uri : Uri? = null

    /* handel music */
    private var mBound:Boolean = false
    private lateinit var mService:MusicService
    private lateinit var mediaPlayer: MediaPlayer
    var listMusic:ArrayList<Music> = ArrayList()
    var position:Int =0

    private val connection = object : ServiceConnection {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)
        tvArtist = findViewById(R.id.playMusicTextViewArtist)
        imgView = findViewById(R.id.playMusicImg)
        playBtn = findViewById(R.id.playMusicButtonPlay)
        prevBtn = findViewById(R.id.playMusicButtonPrev)
        nextBtn = findViewById(R.id.playMusicButtonNext)
        pauseBtn = findViewById(R.id.playMusicButtonPause)
        mediaPlayer = MediaPlayer()
        listMusic = intent.getSerializableExtra("list music") as ArrayList<Music>
        position = intent.extras?.getInt("position")!!

        pauseBtn?.setOnClickListener {
            playBtn?.visibility = View.VISIBLE
            pauseBtn?.visibility = View.INVISIBLE
            mService.playMusic()
        }

        playBtn?.setOnClickListener {
            playBtn?.visibility = View.INVISIBLE
            pauseBtn?.visibility = View.VISIBLE
            mService.pauseMusic()

        }

        /* button next/prev */
        nextBtn?.setOnClickListener {
            if(position<listMusic.size-1) {
                position++
//                updateNewUI(position)
                mService.stopMusic()
                mService.startMusic(listMusic[position])
            }
        }

        prevBtn?.setOnClickListener {
            if(position>0) {
                position--
//                updateNewUI(position)
                mService.stopMusic()
                mService.startMusic(listMusic[position])
            }
        }
    }

    override fun onStart() {
        super.onStart()

        var intent = Intent(this, MusicService::class.java)
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        Log.e(this.javaClass.simpleName,"stop fragment")
        this.unbindService(connection)
        mBound = false
    }
}