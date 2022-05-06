package com.example.geekout

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.*

class MenuActivity : FragmentActivity() {
    companion object {
        private const val TAG = "MENU"
        private const val HOST_KEY = "host"
        private const val CODE_KEY = "code"
        private const val UN_KEY = "username"
        private const val ID_KEY = "id"
        private val CHARSET = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        private val PATTERN = Regex("[a-zA-Z]{4}")
    }

    private lateinit var mJoinButton: Button
    private lateinit var mCreateButton: Button
    private lateinit var mInstructionsButton: Button
    private lateinit var mSettingsButton: Button
    private lateinit var mPrompt: DialogFragment
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.menu)

        // Initializes Database Reference

        mDatabase = FirebaseDatabase.getInstance().getReference("lobbies")

        // Initializes SharedPreferences and inputs Username, ID associated with client

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        mPrefs.edit()
            .putString(ID_KEY, createID())
            .putString(UN_KEY, "Player")
            .commit()

        // Initializes Views

        mJoinButton = findViewById(R.id.joinButton)
        mCreateButton = findViewById(R.id.createButton)
        mInstructionsButton = findViewById(R.id.instructionsButton)
        mSettingsButton = findViewById(R.id.settingsButton)

        // Sets onClickListeners to the buttons

        mJoinButton.setOnClickListener {
            mPrompt = CodePromptDialogFragment.newInstance()

            mPrompt.show(supportFragmentManager, "PROMPT")
        }

        mCreateButton.setOnClickListener {
            // Starts Create Lobby Activity

            startActivity(Intent(this, CreateActivity::class.java))
        }

        mInstructionsButton.setOnClickListener {
            // Starts Instructions Activity

            startActivity(Intent(this, InstructionsActivity::class.java))
        }

        mSettingsButton.setOnClickListener {
            // Starts Settings Activity

            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    fun joinLobby(code: String) {
        // Checks if lobby code matches the format (assumes get requests are expensive)

        if(PATTERN.matches(code)) {
            // If it does, checks the database for an active lobby matching the code.

            mDatabase.child(code).get().addOnSuccessListener {
                if (it.exists() && it.getValue(Game::class.java)?.isJoinable() == true) {
                    // If a lobby is found and is joinable, joins the lobby

                    // Creates intent to start game activity
                    val gameIntent = Intent(this, GameActivity::class.java)

                    // Adds dependent fields to intent

                    val playerID = mPrefs.getString(ID_KEY, "") as String
                    val playerName = mPrefs.getString(UN_KEY, "") as String

                    gameIntent.putExtra(HOST_KEY, false)
                        .putExtra(ID_KEY, playerID)
                        .putExtra(UN_KEY, playerName)
                        .putExtra(CODE_KEY, code)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    // Starts game activity

                    startActivity(gameIntent)

                    Log.i(TAG, "Joined Lobby $code")
                    Toast.makeText(this, "Joined Lobby $code", Toast.LENGTH_LONG).show()
                } else {
                    // If no lobby is found, notifies the user.

                    Log.i(TAG, "Lobby not found")
                    Toast.makeText(this, "Lobby not found/in progressz", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                // If query fails, notifies the user.

                Log.i(TAG, "Lobby search failed")
                Toast.makeText(this, "Lobby Search Failed", Toast.LENGTH_LONG).show()
            }
        } else {
            // Notifies the user if there is an invalid lobby code.

            Toast.makeText(this, "Invalid Lobby Code", Toast.LENGTH_LONG).show()
        }
    }

    private fun createID(): String {
        var id = ""

        for(i in 1..16) {
            id += CHARSET.random()
        }

        return id
    }

    // Todo: Possible more lifecycle callbacks
}