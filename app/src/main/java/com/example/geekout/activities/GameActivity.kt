package com.example.geekout.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.geekout.R
import com.example.geekout.adapters.GameAdapter
import com.example.geekout.classes.Card
import com.example.geekout.classes.Game
import com.example.geekout.classes.Player
import com.example.geekout.fragments.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameActivity() : FragmentActivity() {

    companion object {
        private const val TAG = "GAME"
        private const val CODE_KEY = "code"
        private const val UN_KEY = "username"
        private const val ID_KEY = "id"
        private const val HOST_KEY = "host"

        private const val LEADERBOARD_ICON = R.drawable.leaderboard_vector
        private const val GAME_ICON = R.drawable.game_vector
    }

    private lateinit var mPlayer: Player
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mCode: String
    private var mGame = Game()
    private var isHost: Boolean = false
    private var mFrags: ArrayList<Fragment> = ArrayList()
    private var mTurnCounter: Int = 0
    private lateinit var mCardGenerator: CardGenerator

    private lateinit var mGameAdapter: GameAdapter
    private lateinit var mGamePager: ViewPager2
    private lateinit var mTabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Game

        setContentView(R.layout.game)

        // Initializes and attaches Adapters for ViewGroups

        mGamePager = findViewById(R.id.gamePager)
        mGameAdapter = GameAdapter(this)
        mTabLayout = findViewById(R.id.tabLayout)

        mGamePager.adapter = mGameAdapter

        TabLayoutMediator(mTabLayout, mGamePager) { tab, position ->
            tab.icon = if (position == 0) getDrawable(GAME_ICON) else getDrawable(LEADERBOARD_ICON)
        }.attach()

        // Initializes SharedPreferences and inputs Username, ID associated with client

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Gets information from intent

        isHost = intent.getBooleanExtra(HOST_KEY, false)
        mCode = intent.getStringExtra(CODE_KEY).toString()

        var playerID: String = intent.getStringExtra(ID_KEY).toString()
        var playerName: String = intent.getStringExtra(UN_KEY).toString()

        // Initializes Database Reference

        mDatabase = FirebaseDatabase.getInstance().getReference("lobbies").child(mCode)

        // Initializes card generator

        mCardGenerator = CardGenerator(
            getTextFromRaw(R.raw.scificards), getTextFromRaw(R.raw.gamecards),
            getTextFromRaw(R.raw.comiccards), getTextFromRaw(R.raw.fantasycards),
            getTextFromRaw(R.raw.misccards)
        )

        // Adds the player to the lobby using a transaction to ensure read/write safety.

        mDatabase.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                val avatars = p.getAvatars()
                val avatar = avatars.shuffled()[0]

                mPlayer = Player(playerID, playerName, avatar)

                p.addPlayer(mPlayer)

                p.removeAvatar(avatar)

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (currentData != null) {

                }
            }
        })

        // If the player isn't the host, adds listeners for data to sync game state.
        // If the player is the host, then their game will be the one others sync to.

        // Todo: Implement Listeners

        mDatabase.child("players").addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // When the player list is updated (player joins or leaves), update local game.

                        val type = object : GenericTypeIndicator<ArrayList<Player>>() {}
                        val players = snapshot.getValue(type) as ArrayList<Player>

                        mGame.setPlayers(players)

                        // If a player leaves and re-indexes the players ArrayList, decrements the turn counter.

                        if (isHost) {
                            if (mGame.getTurn() != null && players[mTurnCounter % players.size] != mGame.getTurn()) {
                                mTurnCounter--
                            }
                        }

                        mFrags = if (mFrags.size == 2) {
                            arrayListOf(mFrags[0], ScoreboardFragment(mGame))
                        } else {
                            arrayListOf(LobbyFragment(mGame, mCode, isHost))
                        }

                        mGameAdapter.setFrags(mFrags)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "Could not fetch Players")
                }
            })

        if (isHost) {
            val actionsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Records player responses.

                        val type = object : GenericTypeIndicator<ArrayList<Game.Action>>() {}
                        val actions = snapshot.getValue(type) as ArrayList<Game.Action>

                        mGame.setActions(actions)

                        if (mGame.getState() == Game.State.BID) {
                            val action = actions[mGame.getPlayers().indexOf(mGame.getActive())]

                            if (action == Game.Action.BID || action == Game.Action.BID_PASS) {
                                setNewBidder()
                            } else {
                                return
                            }
                        } else {
                            var approvals = 0
                            var vetos = 0
                            var total = 0
                            var points = 0

                            for (action in actions) {
                                if (action == Game.Action.REVIEW_ACCEPT) {
                                    approvals++
                                    total++
                                } else if (action == Game.Action.REVIEW_VETO) {
                                    vetos++
                                    total++
                                }
                            }

                            if (mGame.getPlayers().size != 0 && total / mGame.getPlayers().size > 0.8) {
                                points = if (approvals > vetos) 1 else -1

                                mDatabase.runTransaction(object : Transaction.Handler {
                                    override fun doTransaction(data: MutableData): Transaction.Result {
                                        val p = data.getValue(Game::class.java)
                                            ?: return Transaction.success(data)

                                        // Adds player avatar back into list of available avatars.

                                        val player =
                                            p.getPlayers()[p.getPlayers().indexOf(p.getActive())]
                                        player.addPoints(points)

                                        if (player.getPoints() == 5) {
                                            Log.i(TAG, "INTO STATE FINISH")
                                            p.setState(Game.State.FINISH)
                                        } else {
                                            Log.i(TAG, "INTO STATE ROUND")
                                            p.setState(Game.State.ROUND)
                                        }

                                        data.value = p
                                        return Transaction.success(data)
                                    }

                                    override fun onComplete(
                                        databaseError: DatabaseError?,
                                        committed: Boolean,
                                        currentData: DataSnapshot?
                                    ) {
                                        mGame = currentData?.getValue(Game::class.java)!!

                                        drawGame()
                                    }
                                })
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "Could not fetch Actions")
                }
            }

            mDatabase.child("actions").addValueEventListener(actionsListener)

            val answersListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Records player responses.

                        val type = object : GenericTypeIndicator<ArrayList<String>>() {}
                        val answers = snapshot.getValue(type) as ArrayList<String>

                        mGame.setAnswers(answers)

                        mDatabase.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(data: MutableData): Transaction.Result {
                                val p =
                                    data.getValue(Game::class.java) ?: return Transaction.success(
                                        data
                                    )
                                if (p.getState() == Game.State.TASK) {
                                    p.setState(Game.State.REVIEW)
                                }

                                data.value = p
                                return Transaction.success(data)
                            }

                            override fun onComplete(
                                databaseError: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?
                            ) {
                                Log.i(TAG, "Next Stage")
                                mGame = currentData?.getValue(Game::class.java)!!

                                drawGame()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "Could not fetch Answers")
                }
            }

            mDatabase.child("answers").addValueEventListener(answersListener)
        } else {
            val stateListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // When the game state is updated, updates UI
                        mDatabase.get().addOnSuccessListener {
                            if (it.exists()) {
                                mGame = it.getValue(Game::class.java)!!

                                drawGame()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "Could not fetch State")
                }
            }

            mDatabase.child("state").addValueEventListener(stateListener)

            val cardListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val card = snapshot.getValue(Card::class.java) as Card

                        mGame.setCard(card)
                        drawGame()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "Could not fetch Card")
                }
            }

            mDatabase.child("card").addValueEventListener(cardListener)

            val currPlayerListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.i(TAG, "Updating active")
                        val curr = snapshot.getValue(Player::class.java)

                        if (curr != null) {
                            mGame.setActive(curr)
                        }
                        drawGame()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "Could not fetch Active")
                }
            }

            mDatabase.child("active").addValueEventListener(currPlayerListener)

            val currTurnListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.i(TAG, "Updating turn")
                        val curr = snapshot.getValue(Player::class.java)

                        if (curr != null) {
                            mGame.setTurn(curr)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

            mDatabase.child("turn").addValueEventListener(currTurnListener)

            val bidListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.i(TAG, "Updating bid")
                        val bid = snapshot.getValue(Int::class.java) as Int

                        if (bid != null) {
                            mGame.setBid(bid)
                        }

                        mDatabase.child("highestBidder").get().addOnSuccessListener {
                            if (it.exists()) {
                                mGame.setHighestBidder(it.getValue(Player::class.java)!!)
                                drawGame()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

            mDatabase.child("bid").addValueEventListener(bidListener)
        }
    }

    // Todo: Implement more rigorous method for disconnections

    override fun onDestroy() {
        super.onDestroy()

        if (isHost) {
            // Removes game from database if host leaves.

            mDatabase.removeValue()
        } else {
            // Removes player from database if player leaves after a short timeout.

            mDatabase.runTransaction(object : Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                    // Adds player avatar back into list of available avatars.

                    p.removeAction(p.getPlayers().indexOf(mPlayer))

                    p.removePlayer(mPlayer)
                    p.addAvatar(mPlayer.getAvatar())

                    data.value = p
                    return Transaction.success(data)
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    Log.i(TAG, "Completed Deletion")
                }
            })
        }
    }

    // Starts the game and notifies clients.

    fun startGame() {
//        if (mGame.getPlayers().size >= 2) {
        if (mGame.getPlayers().size >= 0) {
            mDatabase.runTransaction(object : Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                    // Sets the game state to DRAW

                    Log.i(TAG, "STARTING GAME")
                    p.setState(Game.State.DRAW)
                    p.setTurn(mGame.getPlayers()[mTurnCounter % mGame.getPlayers().size])

                    data.value = p
                    return Transaction.success(data)
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    mGame = currentData?.getValue(Game::class.java)!!
                    setCard()
                }
            })
        } else {
            Log.i(TAG, "NOT ENOUGH PLAYERS")
            Toast.makeText(this, "Need at least 2 Players", Toast.LENGTH_SHORT).show()
        }
    }

    // Todo: Implement additional auxillary methods

    private fun setCard() {
        mDatabase.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                // Sets the game state to DRAW

                val card = mCardGenerator.generateCard(mGame.rollColor())
                p.setCard(card)

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                mGame = currentData?.getValue(Game::class.java)!!

                drawGame()
            }
        })
    }

    fun onFlipEnd() {
        if (isHost) {
            mDatabase.runTransaction(object : Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                    // Sets the game state to DRAW

                    p.setState(Game.State.BID)
                    p.setHighestBidder(p.getPlayers()[mTurnCounter % p.getPlayers().size])
                    p.setActive(p.getPlayers()[mTurnCounter % p.getPlayers().size])
                    p.getCard()?.getBid()?.let { p.setBid(it) }

                    data.value = p
                    return Transaction.success(data)
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    mGame = currentData?.getValue(Game::class.java)!!

                    drawGame()
                }
            })
        }
    }

    fun bidSubmit(bidAmount: Int) {
        mDatabase.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                // Sets the action to bid pass.
                p.submitAction(p.getPlayers().indexOf(mPlayer), Game.Action.BID)
                p.setBid(bidAmount)
                p.setHighestBidder(mPlayer)

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                mGame = currentData?.getValue(Game::class.java)!!
                drawGame()
            }
        })
    }

    fun bidPass() {
        mDatabase.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                // Sets the action to bid pass.
                p.submitAction(p.getPlayers().indexOf(mPlayer), Game.Action.BID_PASS)

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                mGame = currentData?.getValue(Game::class.java)!!
                drawGame()
            }
        })
    }

    fun approveAnswers() {
        mDatabase.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                // Sets the action to bid pass.
                p.submitAction(p.getPlayers().indexOf(mPlayer), Game.Action.REVIEW_ACCEPT)

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                mGame = currentData?.getValue(Game::class.java)!!
                drawGame()
            }
        })
    }

    fun rejectAnswers() {
        mDatabase.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                // Sets the action to bid pass.
                p.submitAction(p.getPlayers().indexOf(mPlayer), Game.Action.REVIEW_VETO)

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                mGame = currentData?.getValue(Game::class.java)!!
                drawGame()
            }
        })
    }

    private fun setNewBidder() {
        Log.i(TAG, "SETTING NEW BIDDER")
        mDatabase.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                // Sets the new bidder

                Log.i(TAG, p.getPlayers().size.toString())
                Log.i(TAG, (p.getPlayers().indexOf(p.getActive())).toString())
                Log.i(TAG, (p.getPlayers().indexOf(p.getActive()) + 1).toString())
                Log.i(
                    TAG,
                    p.getPlayers()[(p.getPlayers()
                        .indexOf(p.getActive()) + 1) % p.getPlayers().size].toString()
                )

                p.submitAction(p.getPlayers().indexOf(p.getActive()), Game.Action.NONE)
                p.setActive(
                    p.getPlayers()[(p.getPlayers()
                        .indexOf(p.getActive()) + 1) % p.getPlayers().size]
                )

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                mGame = currentData?.getValue(Game::class.java)!!
                drawGame()
                if (mGame.getActive() == mGame.getHighestBidder()) {
                    mDatabase.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(data: MutableData): Transaction.Result {
                            val p =
                                data.getValue(Game::class.java) ?: return Transaction.success(data)

                            // Transitions to TASK State
                            // Todo: Complete delegateTask
                            p.setState(Game.State.TASK)
                            data.value = p

                            return Transaction.success(data)
                        }

                        override fun onComplete(
                            databaseError: DatabaseError?,
                            committed: Boolean,
                            currentData: DataSnapshot?
                        ) {
                            mGame = currentData?.getValue(Game::class.java)!!

                            drawGame()
                        }
                    })
                }
            }

        })
    }

    // Draws the game UI depending on the game state.
    // Todo: Implement UI drawing.

    private fun drawGame() {
        when (mGame.getState()) {
            Game.State.LOBBY -> {
                mFrags = arrayListOf(LobbyFragment(mGame, mCode, isHost))
                mGameAdapter.setFrags(mFrags)
            }

            Game.State.DRAW -> {
                mFrags = arrayListOf(DrawFragment(mGame, this), ScoreboardFragment(mGame))
                mGameAdapter.setFrags(mFrags)
            }

            Game.State.BID -> {
                mFrags = arrayListOf(BidFragment(mGame, mPlayer), ScoreboardFragment(mGame))
                mGameAdapter.setFrags(mFrags)
            }

            Game.State.TASK -> {
                mFrags = arrayListOf(TaskFragment(mGame, mPlayer), ScoreboardFragment(mGame))
                mGameAdapter.setFrags(mFrags)
            }

            Game.State.REVIEW -> {
                mFrags = arrayListOf(ReviewFragment(mGame, mPlayer), ScoreboardFragment(mGame))
                mGameAdapter.setFrags(mFrags)
            }

            Game.State.ROUND -> {
                mFrags = arrayListOf(RoundFragment(mGame), ScoreboardFragment(mGame))
                mGameAdapter.setFrags(mFrags)
            }

            Game.State.FINISH -> {
                mFrags = arrayListOf(FinishFragment(mGame), ScoreboardFragment(mGame))
                mGameAdapter.setFrags(mFrags)
            }
        }
    }

    fun submitAnswers(answers: ArrayList<String>) {
        mDatabase.runTransaction(object : Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java) ?: return Transaction.success(data)

                // Sets the action to bid pass.
                p.setAnswers(answers)

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                mGame = currentData?.getValue(Game::class.java)!!
                drawGame()
            }
        })
    }

    private fun getTextFromRaw(myID: Int): Array<String> {
        var rawText = resources.openRawResource(myID).bufferedReader().use { it.readText() }
        return rawText.split("[\r\n]+".toRegex()).shuffled().toTypedArray()
    }
}