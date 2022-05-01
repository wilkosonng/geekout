package com.example.geekout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.*

class MainActivity : FragmentActivity() {
    companion object {
        const val TAG = "MAIN"
    }

    private lateinit var mJoinButton: Button
    private lateinit var mCreateButton: Button
    private lateinit var mInstructionsButton: Button
    private lateinit var mSettingsButton: Button
    private lateinit var mPrompt: DialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.menu)

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
}