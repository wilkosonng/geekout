package com.example.geekout

class Card(private var bid : Int = -1, private val text : String = "Card Error") {

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

}