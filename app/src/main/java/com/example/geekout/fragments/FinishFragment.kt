package com.example.geekout.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.geekout.R
import com.example.geekout.classes.Game

class FinishFragment(private val game: Game) : Fragment() {

    companion object {
        private const val RED = R.string.redCircle
        private const val BLUE = R.string.blueCircle
        private const val YELLOW = R.string.yellowCircle
        private const val GREEN = R.string.greenCircle
        private const val BLACK = R.string.blackCircle
    }

    private lateinit var finishText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val mView = inflater.inflate(R.layout.finish_fragment, container, false)

        // Sets view values

        finishText = mView.findViewById(R.id.finishText)
        finishText.text = "🏆 Congratulations to ${game.getActive()?.getName()} on earning your fifth and final point to win the game! 🏆"

        return mView
    }
}