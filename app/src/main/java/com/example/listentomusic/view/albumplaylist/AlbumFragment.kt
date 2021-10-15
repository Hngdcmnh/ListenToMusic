package com.example.listentomusic.view.albumplaylist

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import com.example.listentomusic.view.albumdetail.AlbumDetailFragment
import gst.tranningcourse.musicplayermockproject.model.Album
import java.util.Locale
import kotlin.collections.ArrayList

/**
 * class AlbumFragment for view
 * param: albumList for list of album
 */
class AlbumFragment(val albumList: ArrayList<Album>, val musicList: ArrayList<Music>) : Fragment(),
    AlbumAdapter.OnAlbumListOnClick {
    private var recyclerView: RecyclerView? = null
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var arrListAlbum: ArrayList<Album>
    private var tvNoSong: TextView? = null
    private var relativeLayout: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.title = "Albums list"

        recyclerView = view.findViewById(R.id.recyclerViewListAlbum)
        tvNoSong = view.findViewById(R.id.tvNoSong)
        relativeLayout = view.findViewById(R.id.relativeLayout)
        arrListAlbum = arrayListOf()// for handle search view
        arrListAlbum.addAll(albumList)

        // if you have no song in storage then show text
        if (arrListAlbum.size != 0) {
            tvNoSong!!.isInvisible = true
            albumAdapter = AlbumAdapter(arrListAlbum, musicList, this)
            recyclerViewCustom()
        } else {
            tvNoSong!!.isVisible = true
            tvNoSong!!.isSelected = true;
            relativeLayout!!.setBackgroundColor(Color.WHITE)
        }

    }

    /**
     * fun recyclerViewCustom to customize recycler view and set adapter
     */
    private fun recyclerViewCustom() {
        recyclerView?.layoutManager = GridLayoutManager(context, 2)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = albumAdapter
    }

    /**
     * for create option menu with Search view
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_menu, menu)
        val search = menu.findItem(R.id.searchMenu)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search album!"

        /**
         * for create option menu with Search view
         */
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                arrListAlbum.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    albumList.forEach {
                        if (it.albumName.lowercase(Locale.getDefault())
                                .contains(searchText)
                        ) {
                            arrListAlbum.add(it)
                        }
                    }
                    recyclerView?.adapter?.notifyDataSetChanged()
                } else {
                    arrListAlbum.clear()
                    arrListAlbum.addAll(albumList)
                }
                return false
            }
        })
    }

    /**
     * to send list of music in album to Album Detail Fragment and get fragment appear
     */
    override fun onAlbumListOnClick(position: Int, list: ArrayList<Music>) {
        super.onAlbumListOnClick(position, list)

        val albumDetailFragment = AlbumDetailFragment(position, list)
        activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)
            ?.replace(R.id.mainView, albumDetailFragment)?.commit()
    }
}