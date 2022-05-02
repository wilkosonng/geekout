package com.example.geekout

import android.app.Activity
import android.os.Bundle
import com.google.firebase.database.*

class CreateActivity: Activity() {
    // Todo: Implement Lobby Creation Logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Menu
        setContentView(R.layout.settings)
    }
}