package com.example.geekout.activities

import android.os.Bundle
import android.app.Activity
import android.widget.Button
import com.example.geekout.R
import android.widget.EditText
import android.widget.Toast

class SettingsActivity: Activity() {
    // Todo: Create Settings Screen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.settings)
        val showButton = findViewById<Button>(R.id.showInput)


        val editText = findViewById<EditText>(R.id.editText)


        showButton.setOnClickListener {


            val text = editText.text


            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
}

