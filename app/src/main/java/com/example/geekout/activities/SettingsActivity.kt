package com.example.geekout.activities

import android.os.Bundle
import android.app.Activity
import com.example.geekout.R

class SettingsActivity: Activity() {
    // Todo: Create Settings Screen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.settings)
    }
}