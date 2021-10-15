package com.example.listentomusic.view.musicplaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
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
 * Class MusicFragment for view list of music
 *
 * @param musicList: list of music
 */
class MusicFragment(private val musicList: ArrayList<Music>) : Fragment(),
    MusicAdapter.OnMusicPlaylistItemClick {
    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: MusicAdapter
    private lateinit var arrList: ArrayList<Music>
    private var tvNoSongList: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_music_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.title = "Songs list"

        recyclerView = view.findViewById(R.id.recyclerViewListMusic)
        tvNoSongList = view.findViewById(R.id.tvNoSongList)
        arrList = arrayListOf()// for handle search view
        arrList.addAll(musicList)
        if (arrList.size != 0){
            tvNoSongList!!.isInvisible = true
            adapter = MusicAdapter(arrList, this)
            recyclerViewCustom()
        }else{
            tvNoSongList!!.isVisible = true
            tvNoSongList!!.isSelected = true

        }
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
                TODO("Not yet implemented")
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
                }
                return false
            }
        })
    }

    /**
     * handle music playlist item click to start music
     */
    override fun onMusicPlaylistItemClick(position: Int, music: Music) {
        super.onMusicPlaylistItemClick(position, music)
        val bottomFragment = BottomMusicControlFragment(position, arrList)
        activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)
            ?.replace(R.id.bottomMusicControlFragment, bottomFragment)?.commit()
    }
}