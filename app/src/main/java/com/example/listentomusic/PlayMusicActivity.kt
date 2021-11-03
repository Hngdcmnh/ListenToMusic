package com.example.listentomusic

import android.app.Notification.VISIBILITY_PUBLIC
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.media.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.listentomusic.model.Music
import java.util.*
import kotlin.collections.ArrayList
//import android.support.v4.app.NotificationCompat
//import android.support.v4.media.app.NotificationCompat as MediaNotificationCompat

class PlayMusicActivity : AppCompatActivity() {
    private var backBtn: ImageButton? = null
    private var loopBtn: ImageButton? = null
    private var seekBar: SeekBar? = null
    private var tvSeekBarStart: TextView? = null
    private var tvSeekBarEnd:TextView? = null
    private var tvName: TextView? = null
    private var tvArtist: TextView? = null
    private var imgView: ImageView? = null
    private var playBtn: ImageButton? = null
    private var prevBtn: ImageButton? = null
    private var nextBtn: ImageButton? = null
    private var pauseBtn: ImageButton? = null
    private var uri : Uri? = null

//    private var playingThreadHandler: Handler = Handler(Looper.getMainLooper())
//    private lateinit var updateSeekBar:Thread
    var timeMax :Int =0
    var timer = Timer()

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
            initSeekBar()

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)
        backBtn = findViewById(R.id.buttonBack)
        loopBtn = findViewById(R.id.playMusicButtonLoop)
        seekBar = findViewById(R.id.seekBar)
        tvSeekBarStart = findViewById(R.id.tvSeekbarStart)
        tvSeekBarEnd = findViewById(R.id.tvSeekbarEnd)
        tvName = findViewById(R.id.playMusicTextViewName)
        tvArtist = findViewById(R.id.playMusicTextViewArtist)
        imgView = findViewById(R.id.playMusicImg)
        playBtn = findViewById(R.id.playMusicButtonPlay)
        prevBtn = findViewById(R.id.playMusicButtonPrev)
        nextBtn = findViewById(R.id.playMusicButtonNext)
        pauseBtn = findViewById(R.id.playMusicButtonPause)
        mediaPlayer = MediaPlayer()
        listMusic = intent.getSerializableExtra("list music") as ArrayList<Music>
        position = intent.extras?.getInt("position")!!
        updateNewUI(position)


        /* handle button back */
        backBtn?.setOnClickListener {

        }

        /* play and pause music*/
        pauseBtn?.setOnClickListener {
            playBtn?.visibility = View.VISIBLE
            pauseBtn?.visibility = View.INVISIBLE
            mService.playMusic()
//            if((seekBar?.progress)!! /1000 <=2)
//            {
//                updateSeekBar()
//            }
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
                updateNewUI(position)
                mService.stopMusic()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mService.startMusic(listMusic[position])
                }
            }
        }

        prevBtn?.setOnClickListener {
            if(position>0) {
                position--
                updateNewUI(position)
                mService.stopMusic()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mService.startMusic(listMusic[position])
                }
            }
        }

        // Handle seekBar
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                {
                    mService.seekTo(progress)
                    Log.e(this.javaClass.simpleName,progress.toString())
                    var currentPos = mService.getCurrenPosition()!!/1000
                    var minute = currentPos!!/60
                    var second = currentPos!! - minute*60
                    tvSeekBarStart?.text = String.format("%02d",minute)+":"+String.format("%02d",second)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.e(this.javaClass.simpleName,"Start")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.e(this.javaClass.simpleName,"Stop")

            }
        })

        // Handle Loop
        loopBtn?.setOnClickListener {
            mService.changeLoopStatus()
            if (mService.isLooping) loopBtn?.setColorFilter(Color.parseColor("#FFF333"))
            else loopBtn?.setColorFilter(Color.parseColor("#FFFFFF"))
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
        timer.cancel()
        mBound = false
    }

    private fun initSeekBar() {
        seekBar?.max = mService.getDuration()!!
        var duration = mService.getDuration()!!/1000
        var minute = duration!!/60
        var second = duration!! - minute*60
        tvSeekBarEnd?.text = String.format("%02d",minute)+":"+String.format("%02d",second)
        updateSeekBar()
    }

    fun updateSeekBar()
    {

        timeMax = mService.getDuration()!!
        timer = Timer()
        var timerTask = object : TimerTask() {
            override fun run() {
                var progress = seekBar?.progress!!
                if( progress/1000 >= timeMax/1000)
                {
                    seekBar?.progress=0
                    runOnUiThread {
                        if(!mService.isLooping) {
                            playBtn?.visibility = View.INVISIBLE
                            pauseBtn?.visibility = View.VISIBLE
                            mService.isPlaying = false
                            tvSeekBarStart?.text = String.format("%02d",0)+":"+String.format("%02d",0)
                        }
                    }
                }
                else if(mService.isPlaying == true)
                {
                    seekBar?.progress = mService.getCurrenPosition()!!
                    runOnUiThread {
                        var currentPos = mService.getCurrenPosition()!!/1000
                        var minute = currentPos!!/60
                        var second = currentPos!! - minute*60
                        tvSeekBarStart?.text = String.format("%02d",minute)+":"+String.format("%02d",second)
                    }
                    Log.e(this.javaClass.simpleName,"progress: " + progress/1000 )
                    Log.e(this.javaClass.simpleName,"duration: " + timeMax/1000 )
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask,0,1000)
    }

    fun updateNewUI(position:Int)
    {
        uri = listMusic[position].link.toUri()
        metaData(uri!!)
        tvName?.text = listMusic[position].name
        tvArtist?.text = listMusic[position].artist
        pauseBtn?.isInvisible = true
        playBtn?.isVisible = true
        val animation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 12000
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        animation.interpolator = LinearInterpolator()
        imgView?.startAnimation(animation)
    }

    private fun metaData(uri: Uri) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val art: ByteArray? = retriever.embeddedPicture
        if (art != null) {
            Glide.with(this)
                .asBitmap()
                .load(art)
                .into(imgView!!)
        } else {
            Glide.with(this)
                .asBitmap()
                .load(R.mipmap.ic_launcher)
                .into(imgView!!)
        }
    }




}

