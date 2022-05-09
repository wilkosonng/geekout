package com.example.geekout.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geekout.R
import com.example.geekout.classes.Player

class TaskAdapter(private val context: Context):
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "SCOREBOARD ADAPTER"
        private const val PLAYER_TYPE = R.layout.player_score
    }

    private var mItems = ArrayList<String>()

    fun clear() {
        mItems.clear()
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
        }
    }

    fun set(responseList: ArrayList<String>) {
        mItems = responseList
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
        }
    }

    fun getItem(pos: Int): Any {
        return mItems[pos]
    }

    class ViewHolder internal constructor(view: View): RecyclerView.ViewHolder(view) {
        var responseView: View = view
        var responseText: TextView? = null
    }

    override fun getItemViewType(position: Int): Int {
        return PLAYER_TYPE
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(PLAYER_TYPE, parent, false)
        val viewHolder = ViewHolder(v)

        viewHolder.responseView = v.findViewById(R.id.input_item)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = mItems[position]
//        holder.responseView.text = player
//        holder.nameTextView?.text = player.getName()
//        holder.avatarTextView?.text = player.getAvatar()
    }
}