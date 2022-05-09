package com.example.geekout.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.geekout.R
import com.example.geekout.activities.GameActivity
import com.example.geekout.classes.Game
import com.example.geekout.classes.Player

class BidFragment(private val game: Game, private val mPlayer: Player) : Fragment() {

    companion object {
        private const val RED = R.string.redCircle
        private const val BLUE = R.string.blueCircle
        private const val YELLOW = R.string.yellowCircle
        private const val GREEN = R.string.greenCircle
        private const val BLACK = R.string.blackCircle
    }

    private var bid: Int = -1
    private lateinit var cardFrontView: RelativeLayout
    private lateinit var cardText: TextView
    private lateinit var cardColor: TextView
    private lateinit var turnAvatarView: TextView
    private lateinit var turnNameView: TextView
    private lateinit var topBidTextView: TextView
    private lateinit var topBidAvatarView: TextView
    private lateinit var topBidNameView: TextView
    private lateinit var minusButton: Button
    private lateinit var plusButton: Button
    private lateinit var bidText: TextView
    private lateinit var passButton: Button
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val mView = inflater.inflate(R.layout.bid_fragment, container, false)

        // Sets view values

        cardFrontView = mView.findViewById(R.id.cardFront)
        cardText = mView.findViewById(R.id.cardInfo)
        cardColor = mView.findViewById(R.id.colorText)
        turnAvatarView = mView.findViewById(R.id.avatarTextView)
        turnNameView = mView.findViewById(R.id.nameTextView)
        topBidTextView = mView.findViewById(R.id.topBidText)
        topBidAvatarView = mView.findViewById(R.id.bidderAvatarTextView)
        topBidNameView = mView.findViewById(R.id.bidderNameTextView)
        minusButton = mView.findViewById(R.id.minusButton)
        plusButton = mView.findViewById(R.id.plusButton)
        bidText = mView.findViewById(R.id.bidText)
        passButton = mView.findViewById(R.id.passButton)
        submitButton = mView.findViewById(R.id.submitButton)

        // Sets bid to minbid + 1

        bid = game.getBid() + 1

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

        if (game.getActive() != mPlayer) {
            minusButton.visibility = View.GONE
            plusButton.visibility = View.GONE
            bidText.visibility = View.GONE
            submitButton.visibility = View.GONE
            passButton.visibility = View.GONE
        } else {
            minusButton.visibility = View.VISIBLE
            plusButton.visibility = View.VISIBLE
            bidText.visibility = View.VISIBLE
            submitButton.visibility = View.VISIBLE
            passButton.visibility = View.VISIBLE
            "Your Bid: $bid".also { bidText.text = it }
        }

        if (game.getHighestBidder() != null) {
            val player = game.getHighestBidder()

            topBidAvatarView.text = player?.getAvatar()
            topBidNameView.text = player?.getName()
            "Current Top Bid: ${game.getBid()}".also { topBidTextView.text = it }
        }

        if (game.getTurn() != null) {
            val player = game.getActive()

            turnNameView.text = player?.getName()
            turnAvatarView.text = player?.getAvatar()
        }

        minusButton.setOnClickListener {
            if (bid > game.getBid() + 1) {
                bid--
                "Your Bid: $bid".also { bidText.text = it }
            } else {
                Toast.makeText(context, "Can't decrement further", Toast.LENGTH_SHORT).show()
            }
        }

        plusButton.setOnClickListener {
            bid++
            "Your Bid: $bid".also { bidText.text = it }
        }

        submitButton.setOnClickListener {
            if (bid > game.getBid()) {
                (activity as GameActivity).bidSubmit(bid)
            }
        }

        passButton.setOnClickListener {
            (activity as GameActivity).bidPass()
        }

        return mView
    }
}