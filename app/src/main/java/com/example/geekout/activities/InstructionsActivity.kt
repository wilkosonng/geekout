package com.example.geekout.activities

import android.app.Activity
import android.os.Bundle
import com.example.geekout.R

class InstructionsActivity: Activity() {
    // Todo: Create Instructions Screen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.instructions)
    }
}