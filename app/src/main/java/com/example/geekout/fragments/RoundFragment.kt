package com.example.geekout.fragments

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.example.geekout.R
import com.example.geekout.activities.GameActivity
import com.example.geekout.classes.Game

class RoundFragment(private val game: Game) : Fragment() {

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
    private lateinit var notifView: TextView
    private lateinit var mBackAnimator: AnimatorSet
    private lateinit var mFrontAnimator: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val mView = inflater.inflate(R.layout.round_fragment, container, false)

        // Sets view values

        cardFrontView = mView.findViewById(R.id.cardFront)
        cardBackView = mView.findViewById(R.id.cardBack)
        cardText = mView.findViewById(R.id.cardInfo)
        cardColor = mView.findViewById(R.id.colorText)
        notifView = mView.findViewById(R.id.notifText)

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

        var vetoTally = 0
        var approveTally = 0

        for (action in game.getActions()) {
            when (action) {
                Game.Action.REVIEW_VETO -> {
                    vetoTally++
                }

                Game.Action.REVIEW_ACCEPT -> {
                    approveTally++
                }

                else -> {

                }
            }
        }

        notifView.text = if (vetoTally < approveTally) "${game.getActive()?.getName()} passed the challenge and earned themselves a point!" else
            "${game.getActive()?.getName()} failed the challenge and lost themselves a point!"

        return mView
    }
}