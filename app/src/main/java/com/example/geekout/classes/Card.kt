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
        return "$bid $text"
    }

    public fun getFullText() : String {
        return ""
    }

    fun getColor(): Game.Roll {
        return color
    }
}