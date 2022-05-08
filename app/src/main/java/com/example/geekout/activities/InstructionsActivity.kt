package com.example.geekout.activities

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.geekout.R

class InstructionsActivity: Activity() {
    // Todo: Create Instructions Screen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.instructions)
        val textView = findViewById<TextView>(R.id.text_view_id) as TextView
        textView?.setOnClickListener{ Toast.makeText(this@InstructionsActivity,
            R.string.instructions, Toast.LENGTH_LONG).show() }
    }

}
