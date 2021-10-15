package com.example.listentomusic.view.home

import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listentomusic.R
import com.example.listentomusic.databinding.ItemMusicHomeBinding
import com.example.listentomusic.model.Music

/**
 * class HomeMusicAdapter
 * @param listMusic for list of music
 */
class HomeMusicAdapter(
    private val listMusic: ArrayList<Music>,
    private var listener: OnMusicHomePlaylistItemClick
) :
    RecyclerView.Adapter<HomeMusicAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMusicHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(music: Music) {
            binding.musicName.text = music.name

            //handle image view wth Glide
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(music.link.toUri().toString())
            val art: ByteArray? = retriever.embeddedPicture
            if(art != null) {
                Glide.with(binding.musicImg).asBitmap()
                    .load(art)
                    .into(binding.musicImg)
            } else {
                Glide.with(binding.musicImg).asBitmap()
                    .load(R.mipmap.ic_launcher)
                    .into(binding.musicImg)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMusicAdapter.ViewHolder {
        val binding: ItemMusicHomeBinding =
            ItemMusicHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeMusicAdapter.ViewHolder, position: Int) {
        holder.bind(listMusic[position])

        holder.binding.root.setOnClickListener {
            listener.onMusicHomePlaylistItemClick(position, listMusic[position])
        }
    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

    interface OnMusicHomePlaylistItemClick {
        /**
         * func to handle item click
         * @param music for music is playing
         */
        fun onMusicHomePlaylistItemClick(position: Int, music: Music) {

        }
    }
}