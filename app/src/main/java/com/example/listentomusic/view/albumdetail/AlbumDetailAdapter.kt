package com.example.listentomusic.view.albumdetail

import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listentomusic.R
import com.example.listentomusic.model.Music

/**
 * class Album detail adapter for Album detail recycler view
 *
 * @param musicList for list of music in album
 * @param listener: for handle recycler view item click event
 */
class AlbumDetailAdapter(
    private val musicList: ArrayList<Music>,
    private val listener: OnAlbumDetailPlaylistItemClick
) : RecyclerView.Adapter<AlbumDetailAdapter.ViewHolder>() {

    /**
     * Class ViewHolder
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvNameMusicItem)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtistMusicItem)
        val img: ImageView = itemView.findViewById(R.id.imgMusicItem)
    }

    /**
     * create ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_music_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            tvName.text = musicList[position].name
            tvArtist.text = musicList[position].artist
            val musicImg = getImg(musicList[position].link)

            /**
             * set up image view with Glide
             */
            if (musicImg != null) {
                Glide.with(itemView).asBitmap()
                    .load(musicImg)
                    .into(holder.img)
            } else {
                Glide.with(itemView).asBitmap()
                    .load(R.mipmap.ic_launcher)
                    .into(holder.img)
            }
            /**
             * handle item click event
             */
            holder.itemView.setOnClickListener {
                listener.onAlbumDetailPlaylistItemClick(position, musicList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    /**
     * use to get Image inn byte array
     */
    private fun getImg(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art: ByteArray? = retriever.embeddedPicture
        retriever.release()
        return art
    }

    /**
     * Interface for handle item click event
     */
    interface OnAlbumDetailPlaylistItemClick {
        fun onAlbumDetailPlaylistItemClick(position: Int, music: Music) {

        }
    }
}