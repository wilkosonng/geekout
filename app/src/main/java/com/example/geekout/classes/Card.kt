package com.example.geekout.classes

class Card(private var bid : Int, private val text : String, private val color: Game.Roll) {

    // Default constructor for Firebase serialization
    constructor() : this(-1, "Invalid", Game.Roll.RED) {

    }

    public fun getBid() : Int {
        return bid
    }

    public fun updateBid(newBid : Int) {
        bid = newBid
    }

    public fun getText() : String {
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