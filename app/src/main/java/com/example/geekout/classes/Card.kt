package com.example.geekout.classes

class Card(private var bid : Int, private val text : String) {

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