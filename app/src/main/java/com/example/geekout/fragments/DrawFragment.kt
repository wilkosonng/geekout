package com.example.geekout.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Context
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
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import com.example.geekout.R
import com.example.geekout.activities.GameActivity
import com.example.geekout.classes.Game

class DrawFragment(private val game: Game,  private val mActivity: GameActivity) : Fragment() {

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
    private lateinit var mBackAnimator: AnimatorSet
    private lateinit var mFrontAnimator: AnimatorSet

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

        if (game.getCard() != null) {
            cardText.text = game.getCard()!!.getText()
            cardColor.text = when (game.getCard()!!.getColor()) {
                Game.Roll.RED -> getString(RED)
                Game.Roll.BLUE -> getString(BLUE)
                Game.Roll.GREEN -> getString(GREEN)
                Game.Roll.YELLOW -> getString(YELLOW)
                Game.Roll.BLACK -> getString(BLACK)
            }
        }

        mFrontAnimator = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_to_front) as AnimatorSet
        mBackAnimator = AnimatorInflater.loadAnimator(requireContext(), R.animator.card_to_back) as AnimatorSet

        Handler(Looper.getMainLooper()).post {
            mFrontAnimator.setTarget(cardBackView)
            mBackAnimator.setTarget(cardFrontView)

            mFrontAnimator.start()
            mBackAnimator.start()
        }

        mFrontAnimator.doOnEnd {
            mActivity.onFlipEnd()
        }

        return mView
    }
}