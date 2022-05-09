package com.example.geekout.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
        var indexTextView: TextView? = null
        var responseEditText: EditText? = null
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

        viewHolder.indexTextView = v.findViewById(R.id.numberTextView)
        viewHolder.responseEditText = v.findViewById(R.id.inputEditText)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = mItems[position]

        holder.indexTextView?.text = (position + 1).toString() + "."
        holder.responseEditText?.setText(spot)

        holder.responseEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s : Editable) {}

            override fun beforeTextChanged(s : CharSequence, start : Int, count : Int, after : Int) {}

            override fun onTextChanged(s : CharSequence, start : Int, before : Int, count : Int) {
                mItems[holder.bindingAdapterPosition] = holder.responseEditText!!.text.toString()
            }
        })
    }
}