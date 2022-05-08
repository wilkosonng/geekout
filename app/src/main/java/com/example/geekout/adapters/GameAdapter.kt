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

    // Solution to overwrite fragments adapted from here:
    // https://stackoverflow.com/questions/57938930/remove-fragment-in-viewpager2-use-fragmentstateadapter-but-still-display

    private var mIDs = mList.map { it.hashCode().toLong() }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mList[position]
    }

    // Adds a fragment to the ViewPager

    fun addFrag(frag: Fragment) {
        mList.add(frag)
        notifyItemChanged(mList.size)
    }

    // Removes a fragment from the ViewPager

    fun removeFrag(frag: Fragment) {
        mList.remove(frag)
        notifyItemChanged(mList.size)
    }

    // Sets a fragment in the ViewPager

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

    // Clears the fragment list

    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }

    // Overrides to allow for deletion of fragments

    override fun getItemId(position: Int): Long {
        return mList[position].hashCode().toLong()
    }

    override fun containsItem(itemID: Long): Boolean {
        return mIDs.contains(itemID)
    }
}