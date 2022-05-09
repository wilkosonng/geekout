package com.example.geekout.activities

import android.os.Bundle
import android.app.Activity
import android.content.SharedPreferences
import android.widget.Button
import com.example.geekout.R
import android.widget.EditText
import android.widget.Toast
import androidx.preference.PreferenceManager

class SettingsActivity : Activity() {
    // Todo: Create Settings Screen
    companion object {
        private const val UN_KEY = "username"
    }

    private lateinit var mPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.settings)
        val showButton = findViewById<Button>(R.id.showInput)

        //username must be under 12 characters
        val editText = findViewById<EditText>(R.id.editText)

        // Gets SharedPreferences

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        showButton.setOnClickListener {
            val text = editText.text.toString()

            if (text.length in 2..12) {
                mPrefs.edit()
                    .putString(UN_KEY, text)
                    .commit()

                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Username must be between 2 and 12 characters long",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

