package com.example.geekout

import android.app.Activity
import android.os.Bundle
import com.google.firebase.database.*

class GameActivity(isHost: Boolean): Activity() {
    // Todo: Implement Game


    private lateinit var mGame: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the default view: Game
        setContentView(R.layout.game)
    }
}