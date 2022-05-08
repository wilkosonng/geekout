package com.example.geekout.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.geekout.R
import com.example.geekout.classes.Player

class GameAdapter(fragmentActivity: FragmentActivity):
    FragmentStateAdapter(fragmentActivity) {

    companion object {
        private const val TAG = "GAME ADAPTER"
    }
    private var mList: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mList[position]
    }

    fun addFrag(frag: Fragment) {
        mList.add(frag)
        Handler(Looper.getMainLooper()).post {
            notifyItemChanged(mList.size)
        }
    }

    fun removeFrag(frag: Fragment) {
        mList.remove(frag)
        Handler(Looper.getMainLooper()).post {
            notifyItemChanged(mList.size)
        }
    }

    fun setFrag(frag: Fragment, position: Int) {
        mList[position] = frag
        notifyItemChanged(mList.size)
    }

    // Getter for fragment list

    fun getFrags(): ArrayList<Fragment> {
        return mList
    }

    // Setter for fragment list

    fun setFrags(fragList: ArrayList<Fragment>) {
        mList = fragList
        notifyDataSetChanged()
    }

    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }
}