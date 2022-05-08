package com.example.geekout.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geekout.R
import com.example.geekout.adapters.ScoreboardAdapter
import com.example.geekout.classes.Game

class ScoreboardFragment(private val game: Game) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val mView =  inflater.inflate(R.layout.scoreboard_fragment, container, false)

        // Sets all of the view parameters

        val mScoreboardAdapter = ScoreboardAdapter(requireContext())

        val mScoreboardRecyclerView = mView.findViewById<RecyclerView>(R.id.scoreboardRecycler)
        mScoreboardRecyclerView.adapter = mScoreboardAdapter
        mScoreboardRecyclerView.layoutManager = LinearLayoutManager(context)

        mScoreboardAdapter.set(game.getPlayers())

        return mView
    }
}