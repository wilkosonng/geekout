package com.example.geekout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : Activity() {
    companion object {
        const val TAG = "MAIN"
    }

    private lateinit var joinButton: Button
    private lateinit var createButton: Button
    private lateinit var instructionsButton: Button
    private lateinit var settingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.menu)

        // Initializes Views

        joinButton = findViewById(R.id.joinButton)
        createButton = findViewById(R.id.createButton)
        instructionsButton = findViewById(R.id.instructionsButton)
        settingsButton = findViewById(R.id.settingsButton)

        // Sets onClickListeners to the buttons

        joinButton.setOnClickListener {

        }

        createButton.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
        }

        instructionsButton.setOnClickListener {
            startActivity(Intent(this, InstructionsActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}