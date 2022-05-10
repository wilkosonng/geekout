package com.example.geekout.activities

import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Button
import com.example.geekout.R
import android.widget.EditText
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.io.BufferedWriter
import java.io.FileNotFoundException
import java.io.OutputStreamWriter

class SettingsActivity : Activity() {
    // Todo: Create Settings Screen
    companion object {
        private const val UN_KEY = "username"
        private const val UN_FILE = "UN_File.txt"

        private const val TAG = "SETTINGS"
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
                if (!getFileStreamPath(UN_FILE).exists()) {
                    try {
                        val fos = openFileOutput(UN_FILE, Context.MODE_PRIVATE)
                        val mWriter = BufferedWriter(OutputStreamWriter(fos))

                        mWriter.write(text)
                        mWriter.close()
                    } catch (e: FileNotFoundException) {
                        Log.i(TAG, "Error writing to file")
                    }
                }

                mPrefs.edit()
                    .putString(UN_KEY, text)
                    .commit()

                Toast.makeText(this, "Username set to $text", Toast.LENGTH_SHORT).show()
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

