package com.example.geekout

// Todo: Implement Player Class

class Player(id: String, name: String) {

    // Satisfies serializable requirement for Firebase

    constructor() : this("0000000000000000", "Invalid") {

    }

    // Initializes variables

    private var id = id
    private var name = name
    private var points = 0

    // Getter for ID

    fun getID(): String {
        return id
    }

    // Getter for Name

    fun getName(): String {
        return name
    }

    // Getter for Points

    fun getPoints(): Int {
        return points
    }

    // Setter for Name

    fun setName(newName: String): String {
        name = newName
        return name
    }

    // Adds points

    fun addPoints(diff: Int): Int {
        points += diff
        return points
    }

    // Subs points

    fun subPoints(diff: Int): Int {
        points -= diff
        return points
    }

    // Setter for points

    fun setPoints(newPoints: Int): Int {
        points = newPoints
        return points
    }

    // ToString for testing

    override fun toString(): String {
        return "$id, $name"
    }

    // Equals override for remove method dependency of ArrayList

    override fun equals(other: Any?): Boolean {
        return (other is Player) && id == other.id
    }
}