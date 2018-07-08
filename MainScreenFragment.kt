package com.player.musicplayer.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.player.musicplayer.R
import com.player.musicplayer.Songs
import com.player.musicplayer.adapters.MainScreenAdapter
import com.player.musicplayer.fragments.SongPlayingFragment.Statified.myActivity
import java.util.*

class MainScreenFragment : Fragment() {

    var getSongsList: ArrayList<Songs>?=null
    var nowPlayingButton:RelativeLayout? = null
    var playPausebutton:ImageButton? = null
    var visibleLayout: RelativeLayout?=null
    var songTitle:TextView? = null
    var noSongs: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    var myActivity: Activity? = null
    var _mainScreenAdaptor: MainScreenAdapter? = null
    var trackPosition = 0


    object Statified{
        var mMediaPlayer: MediaPlayer?= null
    }

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_main_screen , container , false)
        setHasOptionsMenu(true)
        activity!!.title = "All Songs"
        visibleLayout = view?.findViewById<RelativeLayout>(R.id.visibleLayout)
        noSongs = view?.findViewById<RelativeLayout>(R.id.noSongs)
        nowPlayingButton = view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        songTitle = view?.findViewById<TextView>(R.id.SongTitleMainScreen)
        playPausebutton = view?.findViewById<ImageButton>(R.id.playPausebutton)
        recyclerView = view?.findViewById<RecyclerView>(R.id.contentMain)


        return view


    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getSongsList = getSongsFromPhone()
        val prefs = activity!!.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs.getString("action_sort_recent", "false")
        if (getSongsList == null) {
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        }
        /*If there are songs in the device, we display the list*/
        else {
            _mainScreenAdaptor = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdaptor
        }
        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                _mainScreenAdaptor?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainScreenAdaptor?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu? , inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {
            /*Whichever action item is selected, we save the preferences and perform the operation of comparison*/
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
            }
            _mainScreenAdaptor?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {
            val editortwo = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editortwo?.putString("action_sort_recent", "true")
            editortwo?.putString("action_sort_ascending", "false")
            editortwo?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
            }
            _mainScreenAdaptor?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }


    fun getSongsFromPhone(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)

                arrayList.add(Songs(currentId , currentTitle , currentArtist , currentData , currentDate))
            }
        }
        return arrayList
    }

}
