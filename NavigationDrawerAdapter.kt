package com.player.musicplayer.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.player.musicplayer.*
import com.player.musicplayer.activities.MainActivity
import com.player.musicplayer.fragments.AboutUsFragments
import com.player.musicplayer.fragments.FavouriteFragments
import com.player.musicplayer.fragments.MainScreenFragment
import com.player.musicplayer.fragments.SettingFragments


class NavigationDrawerAdapter(_contentList: ArrayList<String> , _getImages: IntArray , _context: Context )
    : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {

    var contentList: ArrayList<String>? = null
    var getImages: IntArray? = null
    var mContext: Context? = null


    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mContext = _context
    }

    override fun onBindViewHolder(holder: NavViewHolder , position: Int) {
        holder?.icon_get?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_get?.setText(contentList?.get(position))
        holder?.contentHolder?.setOnClickListener({

            if (position == 0) {
                val mainScreenFragment = MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.detail_fragment , mainScreenFragment).commit()
            } else if (position == 1) {
                val favouriteFragment = FavouriteFragments()
                (mContext as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.detail_fragment , favouriteFragment).commit()
            } else if (position == 2) {
                val settingsFragment = SettingFragments()
                (mContext as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.detail_fragment , settingsFragment).commit()
            } else   {
                val aboutUsFragment = AboutUsFragments()
                (mContext as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.detail_fragment , aboutUsFragment).commit()
            }

            MainActivity.Statified.drawerLayout?.closeDrawers()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): NavViewHolder {
        var itemView = LayoutInflater.from(parent?.context).inflate(R.layout.row_custom_navigationdrawer , parent , false)
        val returnThis = NavViewHolder(itemView)
        return returnThis
    }

    override fun getItemCount(): Int {
        return (contentList as ArrayList).size
    }





    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){

        var icon_get : ImageView?=null
        var text_get : TextView?=null
        var contentHolder : RelativeLayout?=null
        init {
            icon_get = itemView?.findViewById(R.id.icon_navdrawer)
            text_get = itemView?.findViewById(R.id.text_navdrawer)

            contentHolder= itemView?.findViewById(R.id.navdrwer_item_content_holder)


        }
    }
}