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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geekout.R
import com.example.geekout.adapters.ScoreboardAdapter
import com.example.geekout.classes.Game
import com.example.geekout.classes.Player

import com.google.firebase.database.*

class GameActivity(): Activity() {

    companion object {
        private const val TAG = "GAME"
        private const val CODE_KEY = "code"
        private const val UN_KEY = "username"
        private const val ID_KEY = "id"
        private const val HOST_KEY = "host"
    }

    private lateinit var mPlayer: Player
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mCode: String
    private var mGame = Game()
    private var isHost: Boolean = false

    private lateinit var mLobbyTextView: TextView
    private lateinit var mScoreboardRecyclerView: RecyclerView
    private lateinit var mScoreboardAdapter: ScoreboardAdapter
    private lateinit var mStartGameButton: Button
    private lateinit var mCardGenerator : CardGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Game

        setContentView(R.layout.game)

        // Initializes Views

        mLobbyTextView = findViewById(R.id.lobbyText)
        mScoreboardRecyclerView = findViewById(R.id.scoreboardRecycler)
        mStartGameButton = findViewById(R.id.startGameButton)

        // Initializes and attaches Adapters for ViewGroups

        mScoreboardAdapter = ScoreboardAdapter(this)
        mScoreboardRecyclerView.layoutManager = LinearLayoutManager(this)
        mScoreboardRecyclerView.adapter = mScoreboardAdapter

        // Initializes SharedPreferences and inputs Username, ID associated with client

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Gets information from intent

        isHost = intent.getBooleanExtra(HOST_KEY, false)
        mCode = intent.getStringExtra(CODE_KEY).toString()

        var playerID: String = intent.getStringExtra(ID_KEY).toString()
        var playerName: String = intent.getStringExtra(UN_KEY).toString()

        // Displays Default Lobby Views

        mScoreboardRecyclerView.visibility = View.VISIBLE
        mLobbyTextView.visibility = View.VISIBLE

        if (isHost) {
            mStartGameButton.visibility = View.VISIBLE
        }

        // Sets default View states for Lobby

        "Lobby Code: $mCode".also { mLobbyTextView.text = it }

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
                    mGame = currentData.getValue(Game::class.java)!!
                    drawGame(mGame)
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
                    drawGame(mGame)
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

                        drawGame(mGame)
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

        // Adds an onClick Listener to the StartGame Button

        mStartGameButton.setOnClickListener {
            if (mGame.getPlayers().size > 1) {
                startGame()
            } else {
                Log.i(TAG, "Not enough players to start.")
                Toast.makeText(this, "Not enough players", Toast.LENGTH_LONG).show()
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
                    mScoreboardAdapter.removePlayer(mPlayer)
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

    private fun startGame() {
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
                drawGame(mGame)
            }
        })
    }

    // Todo: Implement additional auxillary methods

    // Draws the game UI depending on the game state.
    // Todo: Implement UI drawing.

    private fun drawGame(game: Game) {
        when (game.getState()) {
            Game.State.LOBBY -> {
                mScoreboardAdapter.set(game.getPlayers())
            }

            Game.State.DRAW -> {
                mLobbyTextView.visibility = View.GONE

                if (isHost) {
                    mStartGameButton.visibility = View.GONE
                }

            }

            Game.State.ROLL -> {

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
}