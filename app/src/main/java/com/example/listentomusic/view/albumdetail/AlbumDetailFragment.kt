package com.example.listentomusic.view.albumdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listentomusic.R
import com.example.listentomusic.model.Music
import com.example.listentomusic.view.bottommusiccontrol.BottomMusicControlFragment
import java.util.Locale
import kotlin.collections.ArrayList

/**
 * Class AlbumDetailFragment for view list of music in album
 *
 * @param musicList: list of music in album
 * @param position: position of album in list of album
 */
class AlbumDetailFragment(private val position: Int,private val musicList: ArrayList<Music>) : Fragment(),
    AlbumDetailAdapter.OnAlbumDetailPlaylistItemClick {

    var recyclerView: RecyclerView? = null
    private lateinit var adapter: AlbumDetailAdapter
    lateinit var arrList: ArrayList<Music>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        //set title of action bar
        (activity as AppCompatActivity).supportActionBar?.title = "Songs by album list"

        recyclerView = view.findViewById(R.id.recyclerViewListMusicByAlbum)
        arrList = arrayListOf() // for handle Search View
        arrList.addAll(musicList)

        //adapter for recycler view
        adapter = AlbumDetailAdapter(arrList, this)

        recyclerViewCustom()
    }

    /**
     * fun recyclerViewCustom to customize recycler view and set adapter
     */
    private fun recyclerViewCustom() {
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
    }

    /**
     * for create option menu with Search view
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_menu, menu)
        val search = menu.findItem(R.id.searchMenu)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search song!"

        /**
         * for update adapter every change of search view
         */
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                arrList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    musicList.forEach {
                        if (it.name?.toLowerCase(Locale.getDefault())
                                ?.contains(searchText) == true
                        ) {
                            arrList.add(it)
                        }
                    }
                    recyclerView?.adapter?.notifyDataSetChanged()
                } else {
                    arrList.clear()
                    arrList.addAll(musicList)
                    recyclerView?.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
    }

    /**
     * handle recycler view item click to bottom music player appear
     */
    override fun onAlbumDetailPlaylistItemClick(position: Int, music: Music) {
        super.onAlbumDetailPlaylistItemClick(position, music)
        val bottomFragment = BottomMusicControlFragment(position,musicList)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.bottomMusicControlFragment, bottomFragment)?.commit()
    }
}