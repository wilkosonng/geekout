package com.example.geekout.activities

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.geekout.R
import com.example.geekout.adapters.GameAdapter
import com.example.geekout.fragments.CardGenerator
import com.example.geekout.adapters.ScoreboardAdapter
import com.example.geekout.classes.Game
import com.example.geekout.classes.Player
import com.example.geekout.fragments.LobbyFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.google.firebase.database.*

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
    private var mCardGenerator: CardGenerator = CardGenerator()

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

        TabLayoutMediator(mTabLayout, mGamePager, true, false) { tab, position ->
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
                    drawGame()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "Could not fetch Players")
            }
        })

        if (isHost) {
            val actionsListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

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

            val cardListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

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
        if (mGame.getPlayers().size >= 2) {
            mDatabase.runTransaction(object: Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val p = data.getValue(Game::class.java)?: return Transaction.success(data)

                    // Sets the game state to DRAW

                    p.setState(Game.State.DRAW)

                    data.value = p
                    return Transaction.success(data)
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    Log.i(TAG, "Game Started")
                    mGame = currentData?.getValue(Game::class.java)!!
                    drawGame()
                }
            })
        } else {
            Log.i(TAG, "NOT ENOUGH PLAYERS")
            Toast.makeText(this, "Need at least 2 Players", Toast.LENGTH_SHORT).show()
        }
    }

    // Todo: Implement additional auxillary methods

    // Draws the game UI depending on the game state.
    // Todo: Implement UI drawing.

    private fun drawGame() {
        when (mGame.getState()) {
            Game.State.LOBBY -> {
                mGameAdapter.setFrags(arrayListOf(LobbyFragment(mGame, mCode, isHost)))
            }

            Game.State.DRAW -> {

            }

            Game.State.BID -> {

            }

            Game.State.TASK -> {

            }

            Game.State.REVIEW -> {

            }

            Game.State.FINISH -> {

            }

            Game.State.HOST_DC -> {

            }
        }
    }

    fun getGame(): Game {
        return mGame
    }
}