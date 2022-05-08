package com.example.geekout.classes

class Card(private var bid : Int, private var text : String, private val color: Game.Roll) {

    companion object {
        private val TAG = "CARD CLASS"
    }
    // Default constructor for Firebase serialization
    constructor() : this(-1, "Invalid", Game.Roll.RED) {

    }

    fun getBid() : Int {
        return bid
    }

    fun setBid(newBid : Int) {
        bid = newBid
    }

    fun getText(): String {
        return text
    }

    fun setText(newText: String) {
        text = newText
    }

    fun cardInfo() : String {
        var bidText = when (bid) {
            2 -> "Two"
            3 -> "Three"
            4 -> "Four"
            else -> "Error:"
        }
        return "$bidText $text"
    }

    fun getColor(): Game.Roll {
        return color
    }
}