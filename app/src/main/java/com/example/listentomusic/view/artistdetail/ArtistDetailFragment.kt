package com.example.listentomusic.view.artistdetail

import android.os.Bundle
import android.view.*
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
 * class ArtistDetailFragment
 * @param musicList for list of music by artist
 */
class ArtistDetailFragment(private val musicList: ArrayList<Music>) : Fragment(),
    ArtistDetailAdapter.OnArtistDetailPlaylistItemClick {

    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: ArtistDetailAdapter
    private lateinit var arrList: ArrayList<Music>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.artist_detail_fragment, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        //set title of action bar
        (activity as AppCompatActivity).supportActionBar?.title = "Songs by artist list"

        recyclerView = view.findViewById(R.id.recyclerViewListMusicByArtist)
        arrList = arrayListOf() // for handle Search View
        arrList.addAll(musicList)
        adapter = ArtistDetailAdapter(arrList, this)

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
     * func to handle list item click then play music and bottom fragment appear
     */
    override fun onArtistDetailPlaylistItemClick(position: Int, list: ArrayList<Music>) {
        super.onArtistDetailPlaylistItemClick(position, list)
        val bottomFragment = BottomMusicControlFragment(position,musicList)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.bottomMusicControlFragment, bottomFragment)?.commit()
    }
}