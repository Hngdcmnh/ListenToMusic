package com.example.listentomusic.view.albumplaylist

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import gst.tranningcourse.musicplayermockproject.model.Album

/**
 * class Album detail adapter for Album detail recycler view
 *
 * param:
 * listAlbum: for list of album
 * listener: for handle recycler view item click event
 */
class AlbumAdapter(
    private var listAlbum: ArrayList<Album>,
    private var listMusic : ArrayList<Music>,
    private var listener: OnAlbumListOnClick
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumImg = itemView.findViewById<ImageView>(R.id.albumImg)
        val albumName = itemView.findViewById<TextView>(R.id.albumName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.album_item_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            albumName.text = listAlbum[position].albumName

            //set up Image View with Glide
            val musicImage = BitmapFactory.decodeFile(listAlbum[position].albumImg)
            Glide.with(itemView).asBitmap().load(musicImage).into(holder.albumImg)
            if(musicImage != null) {
                Glide.with(itemView).asBitmap().load(musicImage).into(holder.albumImg)
            } else {
                Glide.with(itemView).asBitmap()
                    .load(R.mipmap.ic_launcher)
                    .into(holder.albumImg)
            }
        }

        /**
         * handle item click
         */
        holder.itemView.setOnClickListener() {
            listener.onAlbumListOnClick(position,getMusicListByAlbum(listMusic,listAlbum, position))
        }
    }

    override fun getItemCount(): Int {
        return listAlbum.size
    }

    /**
     * get list of music by album
     */
    private fun getMusicListByAlbum(listMusic: ArrayList<Music>,listAlbum : ArrayList<Album>, position: Int): ArrayList<Music> {
        val list: ArrayList<Music> = arrayListOf()
        for (music in listMusic) {
            if (music.album == listAlbum[position].albumName) {
                list.add(music)
            }
        }
        return list
    }

    /**
     * Interface OnAlbumListOnClick
     * to handle album list item click
     */
    interface OnAlbumListOnClick {
        fun onAlbumListOnClick(position: Int,list: ArrayList<Music>) {

        }
    }
}