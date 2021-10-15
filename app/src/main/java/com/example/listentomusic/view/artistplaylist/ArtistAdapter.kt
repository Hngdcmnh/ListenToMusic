package com.example.listentomusic.view.artistplaylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import gst.tranningcourse.musicplayermockproject.model.Artist

/**
 * class ArtistAdapter
 * @param musicList for list of all music
 * @param artistList for list of artist
 */
class ArtistAdapter(
    private val musicList: ArrayList<Music>,
    private val artistList: ArrayList<Artist>,
    private val listener: OnArtistListItemClick
) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artistName = itemView.findViewById<TextView>(R.id.tvArtistName)
        val artistNumSong = itemView.findViewById<TextView>(R.id.tvArtistNumSong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.artist_item_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            artistName.text = artistList[position].artistName
            artistNumSong.text = "Songs: ${artistList[position].numSong}"

        }

        /**
         * handle item click
         */
        holder.itemView.setOnClickListener() {
            listener.onArtistListItemClick(getMusicListByArtist(musicList, artistList, position))
        }
    }

    /**
     * use to get list of music by artist
     * @param listArtist list of artist
     * @param listMusic list of all music
     * @param position position of artist you choose in list
     */
    private fun getMusicListByArtist(
        listMusic: ArrayList<Music>,
        listArtist: ArrayList<Artist>,
        position: Int
    ): ArrayList<Music> {
        val list: ArrayList<Music> = arrayListOf()

        for (music in listMusic) {
            if (music.artist == listArtist[position].artistName) {
                list.add(music)
            }
        }
        return list
    }

    override fun getItemCount(): Int {
        return artistList.size
    }

    /**
     * Interface to handle item click event
     */
    interface OnArtistListItemClick {
        fun onArtistListItemClick(list: ArrayList<Music>) {

        }
    }
}