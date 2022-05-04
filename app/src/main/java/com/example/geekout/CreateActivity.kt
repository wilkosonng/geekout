package com.example.geekout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*

class CreateActivity: Activity() {
    companion object {
        private const val TAG = "CREATE"
        private const val CODE_KEY = "code"
        private const val UN_KEY = "username"
        private const val ID_KEY = "id"
        private const val HOST_KEY = "host"

        // Charset includes alphabetical characters minus I, l, o, O

        private  val CHARSET = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"
    }

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mCreateLobby: Button
    private lateinit var mCodeTextView: TextView
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mCode: String

    // Todo: Implement Lobby Creation Logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the Create Lobby View

        setContentView(R.layout.create)

        // Gets reference to the Firebase database

        mDatabase = FirebaseDatabase.getInstance().getReference("lobbies")

        // Initializes SharedPreferences and inputs Username, ID associated with client

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Initializes Views

        mCreateLobby = findViewById(R.id.createGameButton)
        mCodeTextView = findViewById(R.id.codeText)

        // Rolls a lobby code

        rollCode()

        mCreateLobby.setOnClickListener {
            if (mCode != null) {

                val playerID = mPrefs.getString(ID_KEY, "") as String
                val playerName = mPrefs.getString(UN_KEY, "") as String

                mDatabase.child("totest").setValue(Game())

                val gameIntent = Intent(this, GameActivity::class.java)

                gameIntent.putExtra(HOST_KEY, true)
                    .putExtra(ID_KEY, playerID)
                    .putExtra(UN_KEY, playerName)
                    .putExtra(CODE_KEY, "totest")

                startActivity(gameIntent)
            } else {
                Log.i(TAG, "Lobby creation failed: Invalid Lobby Code")
                Toast.makeText(this, "Lobby Creation failed: Invalid Lobby Code", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun rollCode() {
        var code = ""

        // Rolls a random code

        for (i in 1..6) {
            code += CHARSET.random()
        }
        
        mDatabase.child(code).get().addOnSuccessListener {

            if (!it.exists()) {
                // Sets lobby code if the code is unused.

                mCode = code
                "Lobby Code: $mCode".also { mCodeTextView.text = it }
            } else {
                // If the code already exists, rolls a new code.

                rollCode()
            }
        }.addOnFailureListener {
            Log.i(TAG, "Query failed")
        }
    }
}