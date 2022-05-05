package com.example.geekout

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.widget.TextView

import com.google.firebase.database.*

class GameActivity(): Activity() {
    // Todo: Implement Game

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Game

        setContentView(R.layout.game)

        // Initializes Views

        mLobbyTextView = findViewById(R.id.lobbyText)

        // Initializes SharedPreferences and inputs Username, ID associated with client

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Gets information from intent

        isHost = intent.getBooleanExtra(HOST_KEY, false)
        mCode = intent.getStringExtra(CODE_KEY).toString()

        var playerID: String = intent.getStringExtra(ID_KEY).toString()
        var playerName: String = intent.getStringExtra(UN_KEY).toString()

        // Sets TextView to display LobbyCode while in Lobby State

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
                }
            }
        })

        // If the player isn't the host, adds listeners for data to sync game state.
        // If the player is the host, then their game will be the one others sync to.

        if (!isHost) {
            // Todo: Implement Listeners
            val playersListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            }

            val stateListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "Destroying")
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
}