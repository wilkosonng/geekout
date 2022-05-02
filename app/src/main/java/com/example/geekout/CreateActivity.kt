package com.example.geekout

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.*

class CreateActivity: Activity() {
    companion object {
        const val TAG = "CREATE"

        // Charset includes alphabetical characters minus I, l, o, O

        const val CHARSET = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"
    }

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mCreateLobby: Button
    private lateinit var mCodeTextView: TextView
    private lateinit var mCode: String

    // Todo: Implement Lobby Creation Logic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets the Content View to the Create Lobby View

        setContentView(R.layout.create)

        // Gets reference to the Firebase database

        mDatabase = FirebaseDatabase.getInstance().getReference("lobbies")

        // Sets lobby code

        mCode = getCode()

        // Initializes Views

        mCreateLobby = findViewById(R.id.createGameButton)
        mCodeTextView = findViewById(R.id.codeText)

        // Sets code text view to new code

        "Lobby Code: $mCode".also { mCodeTextView.text = it }

        mCreateLobby.setOnClickListener {

            mDatabase.child(mCode).setValue("TEST")
        }
    }

    private fun getCode(): String {
        var code = ""
        var flag = true

        // Rolls a random code

        for (i in 1..6) {
            code += CHARSET.random()
        }

        mDatabase.child(code).get().addOnSuccessListener {
            // Rolls code again if the lobby code already exists

            flag = false
        }

        return if (flag) code else getCode()
    }
}