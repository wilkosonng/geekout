package com.example.geekout.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geekout.R
import com.example.geekout.activities.GameActivity
import com.example.geekout.adapters.TaskAdapter
import com.example.geekout.classes.Game

class ReviewFragment(private val game: Game) : Fragment() {

    companion object {
        private const val RED = R.string.redCircle
        private const val BLUE = R.string.blueCircle
        private const val YELLOW = R.string.yellowCircle
        private const val GREEN = R.string.greenCircle
        private const val BLACK = R.string.blackCircle
    }

    private lateinit var cardFrontView: RelativeLayout
    private lateinit var cardBackView: ImageView
    private lateinit var cardText: TextView
    private lateinit var cardColor: TextView
    private lateinit var approveAnswers: Button
    private lateinit var rejectAnswers: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val mView = inflater.inflate(R.layout.draw_fragment, container, false)

        // Sets view values

        cardFrontView = mView.findViewById(R.id.cardFront)
        cardBackView = mView.findViewById(R.id.cardBack)
        cardText = mView.findViewById(R.id.cardInfo)
        cardColor = mView.findViewById(R.id.colorText)
        approveAnswers = mView.findViewById(R.id.submitButton)
        rejectAnswers = mView.findViewById(R.id.passButton)

        if (game.getCard() != null) {
            cardText.text = game.getCard()!!.cardInfo()
            cardColor.text = when (game.getCard()!!.getColor()) {
                Game.Roll.RED -> getString(RED)
                Game.Roll.BLUE -> getString(BLUE)
                Game.Roll.GREEN -> getString(GREEN)
                Game.Roll.YELLOW -> getString(YELLOW)
                Game.Roll.BLACK -> getString(BLACK)
            }
        }

        val mTaskAdapter = TaskAdapter(requireContext())

        val mTaskRecyclerView = mView.findViewById<RecyclerView>(R.id.scoreboardRecycler)
        mTaskRecyclerView.adapter = mTaskAdapter
        mTaskRecyclerView.layoutManager = LinearLayoutManager(context)

        mTaskAdapter.set(ArrayList<String>(game.getBid()))

        approveAnswers.setOnClickListener {
            (activity as GameActivity).approveAnswers()
        }

        rejectAnswers.setOnClickListener {
            (activity as GameActivity).rejectAnswers()
        }

        return mView
    }
}