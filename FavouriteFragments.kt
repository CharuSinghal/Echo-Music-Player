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
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.player.musicplayer.R
import com.player.musicplayer.Songs
import com.player.musicplayer.adapters.FavoriteAdapter
import com.player.musicplayer.database.EchoDatabase
import com.player.musicplayer.fragments.SongPlayingFragment.Statified.favoriteContent

class FavouriteFragments : Fragment() {

    var myActivity: Activity? = null
    var noFavorites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favoriteContent : EchoDatabase? = null
    var refreshList: ArrayList<Songs>? = null
    var getListfromDatabase: ArrayList<Songs>? = null

    object Statified{
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_favourite_fragments, container, false)
        activity!!.title = "Favorites"
        noFavorites = view?.findViewById(R.id.nofavorite)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view.findViewById(R.id.songTitleFavScreen)
        playPauseButton = view.findViewById(R.id.playPausebutton)
        recyclerView = view.findViewById(R.id.favoriteRecyler)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDatabase(myActivity)
        display_favorites_by_searching()
        bottomBarSetup()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
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
                   arrayList.add(Songs(currentId, currentTitle, currentArtist, currentData, currentDate))
            }
        }
           return arrayList
    }
    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            })
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*The bottomBarClickHandler() function is used to handle the click events on the bottom bar*/
    fun bottomBarClickHandler() {

        /*We place a click listener on the bottom bar*/
        nowPlayingBottomBar?.setOnClickListener({

            /*Using the same media player object*/
            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist" , SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle" , SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path" , SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("SongID" , SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition" , SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData" , SongPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar" , "success")
            songPlayingFragment.arguments = args

            /*The below lines are now familiar
            * These are used to open a fragment*/
            fragmentManager!!.beginTransaction().replace(R.id.detail_fragment , songPlayingFragment)

                    /*The below piece of code is used to handle the back navigation
                    * This means that when you click the bottom bar and move on to the next screen
                    * on pressing back button you navigate to the screen you came from*/.addToBackStack("SongPlayingFragment").commit()
        })

        /*Apart from the click on the bottom bar we have a play/pause button in our bottom bar
        * This button is used to play or pause the media player*/
        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {

                /*If the song was already playing, we then pause it and save the it's position
                * and then change the button to play button*/
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }
        fun display_favorites_by_searching() {
            if (favoriteContent?.checkSize() as Int > 0) {
                refreshList = ArrayList<Songs>()
                getListfromDatabase = favoriteContent?.queryDBList()
                val fetchListfromDevice = getSongsFromPhone()
                if (fetchListfromDevice != null) {

                    /*Then we check all the songs in the phone*/
                    for (i in 0..fetchListfromDevice?.size - 1) {

                        /*We iterate through every song in database*/
                        for (j in 0..getListfromDatabase?.size as Int - 1) {

                            /*While iterating through all the songs we check for the songs which are in both the lists
                            * i.e. the favorites songs*/
                            if (getListfromDatabase?.get(j)?.songId === fetchListfromDevice?.get(i)?.songId) {

                                /*on getting the favorite songs we add them to the refresh list*/
                                refreshList?.add((getListfromDatabase as ArrayList<Songs>)[j])
                            }
                        }
                    }
                } else {
                }

                /*If refresh list is null we display that there are no favorites*/
                if (refreshList == null) {
                    recyclerView?.visibility = View.INVISIBLE
                    noFavorites?.visibility = View.VISIBLE
                } else {

                    /*Else we setup our recycler view for displaying the favorite songs*/
                    val favoriteAdapter = FavoriteAdapter(refreshList as ArrayList<Songs> , myActivity as Context)
                    val mLayoutManager = LinearLayoutManager(activity)
                    recyclerView?.layoutManager = mLayoutManager
                    recyclerView?.itemAnimator = DefaultItemAnimator()
                    recyclerView?.adapter = favoriteAdapter
                    recyclerView?.setHasFixedSize(true)
                }
            } else {

                /*If initially the checkSize() function returned 0 then also we display the no favorites present message*/
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            }

        }

    }


