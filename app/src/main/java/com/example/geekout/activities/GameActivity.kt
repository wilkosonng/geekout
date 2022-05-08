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

class GameActivity(): FragmentActivity() {

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
    private var mActiveCounter: Int = 0
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
            tab.icon = if(position == 0) getDrawable(GAME_ICON) else getDrawable(LEADERBOARD_ICON)
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

        mCardGenerator = CardGenerator(getTextFromRaw(R.raw.scificards), getTextFromRaw(R.raw.gamecards),
                                        getTextFromRaw(R.raw.comiccards), getTextFromRaw(R.raw.fantasycards),
                                        getTextFromRaw(R.raw.misccards))

        // Adds the player to the lobby using a transaction to ensure read/write safety.

        mDatabase.runTransaction(object: Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java)?: return Transaction.success(data)

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
            object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // When the player list is updated (player joins or leaves), update local game.

                    val type = object: GenericTypeIndicator<ArrayList<Player>>() {}
                    val players = snapshot.getValue(type) as ArrayList<Player>

                    mGame.setPlayers(players)

                    // If a player leaves and re-indexes the players ArrayList, decrements the turn counter.

                    if (isHost) {
                        if (mGame.getTurn() != null && players[mTurnCounter % players.size] != mGame.getTurn()) {
                            mTurnCounter--
                        }

                        if (mGame.getActive() != null && players[mActiveCounter % players.size] != mGame.getActive()) {
                            mActiveCounter--
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
            val actionsListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Records player responses.

                        val type = object: GenericTypeIndicator<ArrayList<Game.Action>>() {}
                        val actions = snapshot.getValue(type) as ArrayList<Game.Action>

                        mGame.setActions(actions)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

            mDatabase.child("actions").addValueEventListener(actionsListener)
        } else {
            val stateListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // When the game state is updated, updates UI

                        val state = snapshot.getValue(Game.State::class.java) as Game.State
                        mGame.setState(state)

                        drawGame()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

            mDatabase.child("state").addValueEventListener(stateListener)

            val cardListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val card = snapshot.getValue(Card::class.java) as Card

                        mGame.setCard(card)
                        drawGame()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

            mDatabase.child("card").addValueEventListener(cardListener)

            val currPlayerListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

            val currTurnListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

            val bidListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        }
    }

    // Todo: Implement more rigorous method for disconnections

    override fun onDestroy() {
        super.onDestroy()

        if(isHost) {
            // Removes game from database if host leaves.

            mDatabase.removeValue()
        } else {
            // Removes player from database if player leaves after a short timeout.

            mDatabase.runTransaction(object: Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val p = data.getValue(Game::class.java)?: return Transaction.success(data)

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
            mDatabase.runTransaction(object: Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val p = data.getValue(Game::class.java)?: return Transaction.success(data)

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

    private fun setState(state: Game.State, drawAfterwards: Boolean) {
        mDatabase.runTransaction(object: Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java)?: return Transaction.success(data)

                // Sets the game state to State

                p.setState(state)

                data.value = p
                return Transaction.success(data)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                mGame = currentData?.getValue(Game::class.java)!!
                Log.i(TAG, "Set to ${mGame.getState()}")

                if (drawAfterwards) {
                    drawGame()
                }
            }
        })
    }

    private fun setCard() {
        mDatabase.runTransaction(object: Transaction.Handler {
            override fun doTransaction(data: MutableData): Transaction.Result {
                val p = data.getValue(Game::class.java)?: return Transaction.success(data)

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
        if(isHost) {
            setState(Game.State.BID, true)
        }
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
                mFrags = arrayListOf(BidFragment(mGame), ScoreboardFragment(mGame))
                mGameAdapter.setFrags(mFrags)
            }

            Game.State.TASK -> {

            }

            Game.State.REVIEW -> {

            }

            Game.State.ROUND -> {

            }

            Game.State.FINISH -> {

            }

            Game.State.HOST_DC -> {

            }
        }
    }

    private fun getTextFromRaw(myID : Int) : Array<String> {
        var rawText = resources.openRawResource(myID).bufferedReader().use { it.readText() }
        return rawText.split("[\r\n]+".toRegex()).shuffled().toTypedArray()
    }
}