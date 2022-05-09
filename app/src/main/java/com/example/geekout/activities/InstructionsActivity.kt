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
        textView?.setOnClickListener {
            Toast.makeText(
                this@InstructionsActivity,
                R.string.instructions, Toast.LENGTH_LONG
            ).show()
            val textView1 = findViewById<TextView>(R.id.text_view_id1) as TextView
            textView1?.setOnClickListener {
                Toast.makeText(
                    this@InstructionsActivity,
                    R.string.instructions, Toast.LENGTH_LONG
                ).show()
                val textView2 = findViewById<TextView>(R.id.text_view_id2) as TextView
                textView2?.setOnClickListener {
                    Toast.makeText(
                        this@InstructionsActivity,
                        R.string.instructions, Toast.LENGTH_LONG
                    ).show()
                    val textView3 = findViewById<TextView>(R.id.text_view_id3) as TextView
                    textView3?.setOnClickListener {
                        Toast.makeText(
                            this@InstructionsActivity,
                            R.string.instructions, Toast.LENGTH_LONG
                        ).show()
                        val textView4 = findViewById<TextView>(R.id.text_view_id4) as TextView
                        textView4?.setOnClickListener {
                            Toast.makeText(
                                this@InstructionsActivity,
                                R.string.instructions, Toast.LENGTH_LONG
                            ).show()
            }
        }
    }}}}
}
