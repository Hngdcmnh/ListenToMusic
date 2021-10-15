package com.example.listentomusic.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listentomusic.R
import com.example.listentomusic.databinding.FragmentHomeBinding
import com.example.listentomusic.model.Music
import com.example.listentomusic.view.albumdetail.AlbumDetailFragment
import com.example.listentomusic.view.bottommusiccontrol.BottomMusicControlFragment
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import gst.tranningcourse.musicplayermockproject.model.Album


/**
 * class HomeFragment
 * @param musicList for list of all music
 * @param albumList for List of all album
 */
class HomeFragment(
    private val musicList: ArrayList<Music>,
    private val albumList: ArrayList<Album>
) : Fragment(),
    HomeMusicAdapter.OnMusicHomePlaylistItemClick, SlideAdapter.OnMusicPlaylistItemClick,
    HomeAlbumAdapter.OnHomeAlbumListClick {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: SlideAdapter

    private lateinit var musicAdapter: HomeMusicAdapter

    private lateinit var albumAdapter: HomeAlbumAdapter

    private var mListPhoto: MutableList<Music> = mutableListOf()

    private var mListMusic: ArrayList<Music> = arrayListOf()

    private var mListAlbum: ArrayList<Album> = arrayListOf()

    private lateinit var listMusic: ArrayList<Music>

    private lateinit var listAlbum: ArrayList<Album>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListMusic.addAll(musicList.shuffled()) // shuffle music list
        mListAlbum.addAll(albumList.shuffled()) // shuffle album list
        mListPhoto.addAll(musicList.shuffled())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Home"

        setSlideImage()
        setListMusic()
        setListAlbum()
    }

    /**
     * get the album to recycler view with adapter
     */
    private fun setListAlbum() {
        listAlbum = arrayListOf()
        val size = mListAlbum.size
        if (size > 0){
            if (size < 7){
                for (i in 0 until size) {
                    listAlbum.add(mListAlbum[i])
                }
            }else{
                for (i in 0 until 7) {
                    listAlbum.add(mListAlbum[i])
                }
            }
            albumAdapter = HomeAlbumAdapter(musicList, listAlbum, this)
            binding.rvList2.adapter = albumAdapter
        }
        binding.rvList2.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    /**
     * get the music to recycler view with adapter
     */
    private fun setListMusic() {
        listMusic = arrayListOf()
        val size = mListMusic.size
        if (size > 0){
            if (size < 7){
                for (i in 0 until size) {
                    listMusic.add(mListMusic[i])
                }
            }else{
                for (i in 0 until 7) {
                    listMusic.add(mListMusic[i])
                }
            }
            musicAdapter = HomeMusicAdapter(listMusic, this)
            binding.rvList1.adapter = musicAdapter
        }
        binding.rvList1.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setSlideImage() {
        val size = mListPhoto.size
        if (size >= 6) {
            adapter = SlideAdapter(mListPhoto.subList(0, 6), this)
            binding.svMusic.setSliderAdapter(adapter)
        } else if (size in 1..5) {
            adapter = SlideAdapter(mListPhoto.subList(0, size), this)
            binding.svMusic.setSliderAdapter(adapter)
        }
        binding.svMusic.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.svMusic.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
        binding.svMusic.startAutoCycle()
    }

    /**
     * on list suggest music item play handle event
     */
    override fun onMusicHomePlaylistItemClick(position: Int, music: Music) {
        super.onMusicPlaylistItemClick(position, music)
        val bottomFragment = BottomMusicControlFragment(position, listMusic)
        activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)
            ?.replace(R.id.bottomMusicControlFragment, bottomFragment)?.commit()
    }

    /**
     * on list suggest album item play handle event
     */
    override fun onHomeAlbumListClick(position: Int, list: ArrayList<Music>) {
        super.onHomeAlbumListClick(position, list)
        val albumDetailFragment = AlbumDetailFragment(position, list)
        activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)
            ?.replace(R.id.mainView, albumDetailFragment)?.commit()
    }
}