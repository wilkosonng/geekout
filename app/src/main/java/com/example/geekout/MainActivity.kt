package com.example.geekout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.*

class MainActivity : FragmentActivity() {
    companion object {
        const val TAG = "MAIN"
        val PATTERN = Regex("[a-zA-Z]{6}")
    }

    private lateinit var mJoinButton: Button
    private lateinit var mCreateButton: Button
    private lateinit var mInstructionsButton: Button
    private lateinit var mSettingsButton: Button
    private lateinit var mPrompt: DialogFragment
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.menu)

        // Initializes Database Reference

        mDatabase = FirebaseDatabase.getInstance().getReference("lobbies")

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
            startActivity(Intent(this, CreateActivity::class.java))
        }

        mInstructionsButton.setOnClickListener {
            startActivity(Intent(this, InstructionsActivity::class.java))
        }

        mSettingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    fun joinLobby(code: String) {
        // Checks if lobby code matches the format

        if(PATTERN.matches(code)) {
            // If it does, checks the database for an active lobby matching the code.

            mDatabase.child("lobbies").child(code).get().addOnSuccessListener {
                // If a match is found, joins the lobby
                // Todo: Add Lobby joining code

                Log.i(TAG, "Joined Lobby $code")
                Toast.makeText(this, "Joined Lobby $code", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                // If no match is found, notifies the user.

                Log.i(TAG, "Lobby not found")
                Toast.makeText(this, "Lobby Not Found", Toast.LENGTH_LONG).show()
            }
        } else {
            // Notifies the user if there is an invalid lobby code.

            Toast.makeText(this, "Invalid Lobby Code", Toast.LENGTH_LONG).show()
        }
    }

    // Todo: Possible more lifecycle callbacks
}