package com.example.listentomusic.view.artistplaylist

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import com.example.listentomusic.view.artistdetail.ArtistDetailFragment
import gst.tranningcourse.musicplayermockproject.model.Artist
import java.util.Locale
import kotlin.collections.ArrayList

/**
 * class ArtistFragment
 * @param musicList for list of all music
 * @param artistList for list of all artist
 */
class ArtistFragment(val musicList: ArrayList<Music>, val artistList: ArrayList<Artist>) :
    Fragment(), ArtistAdapter.OnArtistListItemClick {

    private var recyclerView: RecyclerView? = null
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var arrListArtist: ArrayList<Artist>
    private var tvNoSong: TextView? = null
    private var relativeLayout: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.artist_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // set title of action bar
        (activity as AppCompatActivity).supportActionBar?.title = "Artist list"

        recyclerView = view.findViewById(R.id.recyclerViewListArtist)
        tvNoSong = view.findViewById(R.id.tvNoSong)
        relativeLayout = view.findViewById(R.id.relativeLayout)
        arrListArtist = arrayListOf()// for handle search view
        arrListArtist.addAll(artistList)

        //handle UI if you have no song in storage
        if (arrListArtist.size != 0) {
            tvNoSong!!.isInvisible = true
            artistAdapter = ArtistAdapter(musicList, arrListArtist, this)
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
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = artistAdapter
    }

    /**
     * for create option menu with Search view
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_menu, menu)
        val search = menu.findItem(R.id.searchMenu)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search artist!"

        /**
         * for create option menu with Search view
         */
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                arrListArtist.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    artistList.forEach {
                        if (it.artistName.toLowerCase(Locale.getDefault())
                                .contains(searchText)
                        ) {
                            arrListArtist.add(it)
                        }
                    }
                    recyclerView?.adapter?.notifyDataSetChanged()
                } else {
                    arrListArtist.clear()
                    arrListArtist.addAll(artistList)
                }
                return false
            }
        })
    }

    /**
     * handle artist list item click event
     */
    override fun onArtistListItemClick(list: ArrayList<Music>) {
        super.onArtistListItemClick(list)
        val artistDetailFragment = ArtistDetailFragment(list)
        activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)
            ?.replace(R.id.mainView, artistDetailFragment)?.commit()
    }
}