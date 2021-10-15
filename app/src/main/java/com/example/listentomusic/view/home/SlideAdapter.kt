package com.example.listentomusic.view.home

import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.listentomusic.R
import com.example.listentomusic.databinding.ItemSliderBinding
import com.example.listentomusic.model.Music
import com.smarteist.autoimageslider.SliderViewAdapter

class SlideAdapter(
    private var mListPhoto: MutableList<Music>,
    private val listener: SlideAdapter.OnMusicPlaylistItemClick
) :
    SliderViewAdapter<SlideAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemSliderBinding) : SliderViewAdapter.ViewHolder(binding.root) {
        fun bind(music: Music) {
            Log.e(this.javaClass.simpleName,music.toString())
            val retriever = MediaMetadataRetriever()
            Log.e(this.javaClass.simpleName,music.link.toUri().toString())
            retriever.setDataSource(music.link.toUri().toString())
            val art: ByteArray? = retriever.embeddedPicture
            Glide.with(binding.imgMusic).load(art).centerCrop()
                .placeholder(R.drawable.ic_launcher_background).into(binding.imgMusic)
        }
    }

    override fun getCount(): Int {
        if (mListPhoto.size > 0)
            return mListPhoto.size
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SlideAdapter.ViewHolder {
        val binding: ItemSliderBinding =
            ItemSliderBinding.inflate(LayoutInflater.from(parent?.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: SlideAdapter.ViewHolder?, position: Int) {
        viewHolder?.bind(mListPhoto[position])
        viewHolder?.binding?.root?.setOnClickListener {
            listener.onMusicPlaylistItemClick(position, mListPhoto[position])
        }
    }

    interface OnMusicPlaylistItemClick {

        /**
         * func to handle item click
         * @param music for music is playing
         */
        fun onMusicPlaylistItemClick(position: Int, music: Music) {

        }
    }
}