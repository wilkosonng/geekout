package com.example.geekout.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geekout.R
import com.example.geekout.activities.GameActivity
import com.example.geekout.adapters.ScoreboardAdapter
import com.example.geekout.classes.Game
import com.example.geekout.classes.Player

class LobbyFragment(private val game: Game, private val code: String, private val isHost: Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val mView =  inflater.inflate(R.layout.lobby_fragment, container, false)

        // Sets all of the view parameters

        val mScoreboardAdapter = ScoreboardAdapter(requireContext())

        val mScoreboardRecyclerView = mView.findViewById<RecyclerView>(R.id.scoreboardRecycler)
        mScoreboardRecyclerView.adapter = mScoreboardAdapter
        mScoreboardRecyclerView.layoutManager = LinearLayoutManager(context)

        mScoreboardAdapter.set(game.getPlayers())

        val mLobbyCodeTextView = mView.findViewById<TextView>(R.id.lobbyText)
        mLobbyCodeTextView.text = "Lobby code: $code"

        val startGameButton = mView.findViewById<Button>(R.id.startGameButton)

        if (isHost) {
            startGameButton.visibility = View.VISIBLE
        }

        startGameButton.setOnClickListener {
            (activity as GameActivity).startGame()
        }

        return mView
    }
}