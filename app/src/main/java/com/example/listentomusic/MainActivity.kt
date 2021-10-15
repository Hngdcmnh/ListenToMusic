package com.example.listentomusic

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.listentomusic.model.Music
import com.example.listentomusic.utils.Contain.REQUEST_CODE
import com.example.listentomusic.view.albumplaylist.AlbumFragment
import com.example.listentomusic.view.artistplaylist.ArtistFragment
import com.example.listentomusic.view.home.HomeFragment
import com.example.listentomusic.view.musicplaylist.MusicFragment
import com.google.android.material.navigation.NavigationView
import gst.tranningcourse.musicplayermockproject.model.Album
import gst.tranningcourse.musicplayermockproject.model.Artist

/**
 * class Main Activity
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var navigationView: NavigationView? = null
    var drawerLayout: DrawerLayout? = null
    var toggle: ActionBarDrawerToggle? = null

    var musicList: ArrayList<Music> = arrayListOf() // for list of all music
    var albumList: ArrayList<Album> = arrayListOf() // for list of all album
    var artistList: ArrayList<Artist> = arrayListOf() // for list of all artist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        customNavView()
        permission()
        navigationView?.setNavigationItemSelectedListener(this)
    }

    /**
     * to get permission to get data from storage
     */
    private fun permission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        } else {
            musicList = getAllMusic()
            albumList = getAllAlbum(this)
            artistList = getAllArtist()
            if (musicList.size > 0) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Currently, your device does not have any songs",
                    Toast.LENGTH_LONG
                ).show()
            }
            // if permission is granted then go to home fragment
            val homeFragment = HomeFragment(musicList, albumList)
            supportFragmentManager.beginTransaction().replace(R.id.mainView, homeFragment)
                .commit()

        }
    }

    /**
     * to request permission result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val homeFragment = HomeFragment(musicList, albumList)
            supportFragmentManager.beginTransaction().replace(R.id.mainView, homeFragment)
                .commit()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * function to get all music
     * return array list of music
     */
    private fun getAllMusic(): ArrayList<Music> {
        val tempMusicList = arrayListOf<Music>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val link = cursor.getString(3)
                val artist = cursor.getString(4)
                val music = Music(title, artist, link, album)
                tempMusicList.add(music)
            }
            cursor.close()
        }
        return tempMusicList
    }

    /**
     * function to get all of album
     */
    @SuppressLint("Range")
    private fun getAllAlbum(context: Context): ArrayList<Album> {

        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val _id = MediaStore.Audio.Albums._ID
        val album_name = MediaStore.Audio.Albums.ALBUM
        val artist = MediaStore.Audio.Albums.ARTIST
        val albumart = MediaStore.Audio.Albums.ALBUM_ART
        val tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS

        val columns = arrayOf(_id, album_name, artist, albumart, tracks)
        val cursor = context.contentResolver.query(uri, columns, null, null, null)

        val albumList = ArrayList<Album>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(_id))
                val name = cursor.getString(cursor.getColumnIndex(album_name))
                val artist2 = cursor.getString(cursor.getColumnIndex(artist))
                val artPath = cursor.getString(cursor.getColumnIndex(albumart))
                val nr = cursor.getString(cursor.getColumnIndex(tracks)).toInt()
                albumList.add(Album(id, name, artist2, artPath, nr))
//                Log.e(this.javaClass.simpleName,id.toString() + name.toString()+artist2.toString()+artPath.toString()+nr.toString())
//                albumList.add(Album(1, "", "", "", 1))
            } while (cursor.moveToNext())
        }
        cursor!!.close()

        return albumList
    }

    /**
     * function to get all of artist
     */
    @SuppressLint("Range")
    private fun getAllArtist(): ArrayList<Artist> {
        val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
        val _id = MediaStore.Audio.Artists._ID
        val artistName = MediaStore.Audio.Artists.ARTIST
        val artistNumSong = MediaStore.Audio.Artists.NUMBER_OF_TRACKS

        val injection = arrayOf(_id, artistName, artistNumSong)
        val cursor = contentResolver.query(uri, injection, null, null, null)

        val artistList = ArrayList<Artist>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(_id))
                val name = cursor.getString(cursor.getColumnIndex(artistName))
                val numSong = cursor.getInt(cursor.getColumnIndex(artistNumSong))

                artistList.add(Artist(id, name, numSong))
            } while (cursor.moveToNext())
        }
        cursor!!.close()
        return artistList
    }

    /**
     * init view
     */
    private fun init() {
        navigationView = findViewById(R.id.navView)
        drawerLayout = findViewById(R.id.drawer_layout)
    }

    /**
     * custom side bar
     */
    private fun customNavView() {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout?.addDrawerListener(toggle!!)
        toggle!!.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * for replace view with fragment by choosing item in side bar
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuPlaylist -> {
                val musicFragment = MusicFragment(musicList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, musicFragment)
                    .commit()
            }
            R.id.menuAlbum -> {
                val albumFragment = AlbumFragment(albumList, musicList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, albumFragment)
                    .commit()
            }
            R.id.menuHome -> {
                val homeFragment = HomeFragment(musicList, albumList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, homeFragment)
                    .commit()
            }
            R.id.menuArtist -> {
                val artistFragment = ArtistFragment(musicList, artistList)
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.mainView, artistFragment)
                    .commit()
            }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}