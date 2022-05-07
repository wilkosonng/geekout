package com.example.geekout

import com.example.geekout.classes.Game

class Card(private var bid : Int = -1, private val text : String = "Card Error", private val color : Game.Roll) {

    public fun getBid() : Int {
        return bid
    }

    public fun updateBid(newBid : Int) {
        bid = newBid
    }

    public fun getText() : String {
        return text
    }

    public fun getFullText() : String {
        return "$bid $text"
    }

    public fun getCategory() : Game.Roll {
        return color
    }



}