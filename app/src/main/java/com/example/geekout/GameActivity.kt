package com.example.geekout

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.database.*

class GameActivity(): Activity() {

    companion object {
        private const val TAG = "GAME"
        private const val CODE_KEY = "code"
        private const val UN_KEY = "username"
        private const val ID_KEY = "id"
        private const val HOST_KEY = "host"
    }

    private lateinit var mGame: Game
    private lateinit var mPlayer: Player
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mPrefs: SharedPreferences
    private var isHost: Boolean = false
    private var mCode: String = ""

    private lateinit var mLobbyTextView: TextView
    private lateinit var mScoreboardRecyclerView: RecyclerView
    private lateinit var mScoreboardAdapter: ScoreboardAdapter
    private lateinit var mStartGameButton: Button

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

        // Sets default View states for Lobby

        "Lobby Code: $mCode".also { mLobbyTextView.text = it }

        // Hides start game button for non-hosts.

        if (!isHost) {
            mStartGameButton.visibility = View.GONE
        }

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

        val playersListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val players = snapshot.value as ArrayList<Player>

                mGame.setPlayers(players)
                drawGame(mGame)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "Could not fetch Players")
            }
        }

        mDatabase.child("players").addValueEventListener(playersListener)

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

        mStartGameButton.setOnClickListener {
            startGame()
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
        }
    }
}