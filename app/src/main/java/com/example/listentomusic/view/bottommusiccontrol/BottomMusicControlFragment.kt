package com.example.listentomusic.view.bottommusiccontrol

import android.content.*
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.listentomusic.MusicService
import com.example.listentomusic.PlayMusicActivity
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import java.io.Serializable

/**
 * Class BottomMusicControlFragment for bottom music control
 * param: music for the music is playing
 */
class BottomMusicControlFragment(private var position : Int,var listMusic: ArrayList<Music>) : Fragment() {
    private var bottomTvName: TextView? = null
    private var bottomTvArtist: TextView? = null
    private var bottomImgView: ImageView? = null
    private var bottomPlayBtn: ImageButton? = null
    private var bottomPrevBtn: ImageButton? = null
    private var bottomNextBtn: ImageButton? = null
    private var bottomPauseBtn: ImageButton? = null
    private var uri : Uri? = null

    /* handel music */
    private var mBound:Boolean = false
    private lateinit var mService:MusicService
    private lateinit var mediaPlayer:MediaPlayer

    private val connection = object :ServiceConnection{
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            mService = binder.getService()
            mBound = true
            Log.e(this.javaClass.simpleName,mService.isPlaying.toString())
            mService.startMusic(listMusic[position])
            bottomPlayBtn?.visibility = View.VISIBLE
            bottomPauseBtn?.visibility = View.INVISIBLE
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_music_player_control, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        Log.e(this.javaClass.simpleName,"start fragment")
        var intent = Intent(this.context, MusicService::class.java)
        this.context?.startService(intent)
        this.context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        Log.e(this.javaClass.simpleName,"stop fragment")
        this.context?.unbindService(connection)
//        Toast.makeText(this.context,"Stop",Toast.LENGTH_SHORT).show()
        mBound = false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomTvName = view.findViewById(R.id.tvNameBottom)
        bottomTvArtist = view.findViewById(R.id.tvArtistBottom)
        bottomImgView = view.findViewById(R.id.bottomImgView)
        bottomPlayBtn = view.findViewById(R.id.bottomPlayButton)
        bottomPrevBtn = view.findViewById(R.id.bottomPrevButton)
        bottomNextBtn = view.findViewById(R.id.bottomNextButton)
        bottomPauseBtn = view.findViewById(R.id.bottomPauseButton)
        mediaPlayer = MediaPlayer()

        init()

        /**
         * handle bottom music control click event to view PlayMusicActivity screen
         */
        view.setOnClickListener {
            sendingIntent()
        }

        /* button pause/play */
        bottomPauseBtn?.setOnClickListener {
            bottomPlayBtn?.visibility = View.VISIBLE
            bottomPauseBtn?.visibility = View.INVISIBLE
//            mediaPlayer.start()
            mService.playMusic()
        }

        bottomPlayBtn?.setOnClickListener {
            bottomPlayBtn?.visibility = View.INVISIBLE
            bottomPauseBtn?.visibility = View.VISIBLE
//            mediaPlayer.pause()
            mService.pauseMusic()

        }

        /* button next/prev */
        bottomNextBtn?.setOnClickListener {
            if(position<listMusic.size-1) {
                position++
                updateNewUI(position)
                mService.stopMusic()
                mService.startMusic(listMusic[position])
            }
        }

        bottomPrevBtn?.setOnClickListener {
            if(position>0) {
                position--
                updateNewUI(position)
                mService.stopMusic()
                mService.startMusic(listMusic[position])
            }
        }
    }

    fun updateNewUI(position:Int)
    {
        uri = listMusic[position].link.toUri()
        metaData(uri!!)
        bottomTvName?.text = listMusic[position].name
        bottomTvArtist?.text = listMusic[position].artist
        bottomPauseBtn?.isInvisible = true
        bottomPlayBtn?.isVisible = true
        val animation = RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        ).apply {
            duration = 6000
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        animation.interpolator = LinearInterpolator()
        bottomImgView?.startAnimation(animation)
    }



    /**
     * initialize the bottom fragment's view
     */
    private fun init() {
        uri = listMusic[position].link.toUri()
        metaData(uri!!)
        bottomTvName?.text = listMusic[position].name
        bottomTvArtist?.text = listMusic[position].artist
        bottomPauseBtn?.isInvisible = true
        bottomPlayBtn?.isVisible = true
        val animation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 6000
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
        }
        animation.interpolator = LinearInterpolator()
        bottomImgView?.startAnimation(animation)
    }

    /**
     * This func used to assign image to bottomImgView
     */
    private fun metaData(uri: Uri) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val art: ByteArray? = retriever.embeddedPicture
        if (art != null) {
            Glide.with(this)
                .asBitmap()
                .load(art)
                .into(bottomImgView!!)
        } else {
            Glide.with(this)
                .asBitmap()
                .load(R.mipmap.ic_launcher)
                .into(bottomImgView!!)
        }
    }

    /**
     * for sending intent with music is playing to start Playing music activity
     */

    private fun sendingIntent() {
        val intent = Intent(context, PlayMusicActivity::class.java)

        intent.putExtra("list music", listMusic as Serializable )
        intent.putExtra("position",position)
        startActivity(intent)
    }

}