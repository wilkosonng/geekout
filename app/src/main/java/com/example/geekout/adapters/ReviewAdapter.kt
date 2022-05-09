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

class ReviewAdapter(private val context: Context) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "REVIEW ADAPTER"
        private const val REVIEW_ITEM = R.layout.review_item
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
        var reviewIndexTextView: TextView? = null
        var responseTextView: TextView? = null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(REVIEW_ITEM, parent, false)
        val viewHolder = ViewHolder(v)

        viewHolder.reviewIndexTextView = v.findViewById(R.id.reviewNumber)
        viewHolder.responseTextView = v.findViewById(R.id.reviewText)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = mItems[position]

        holder.reviewIndexTextView?.text = (position + 1).toString() + "."
        holder.responseTextView?.text = spot
    }

}