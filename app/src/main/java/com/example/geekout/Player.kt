package com.example.geekout

class Player(id: String, name: String, avatar: String): Comparable<Player> {

    // Satisfies serializable requirement for Firebase

    constructor() : this("0000000000000000", "Null", "") {

    }

    /* Initializes variables
     * id: Unique ID to identify the player.
     * name: Username displayed to other players.
     * avatar: emoji avatar dispalyed to other players.
     * points: the number of points the user has.
     */

    private var id: String = id
    private var name: String = name
    private var avatar: String = avatar
    private var points: Int = 0

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

    // Getter for avatar

    fun getAvatar(): String {
        return avatar
    }

    // toString for testing

    override fun toString(): String {
        return "$id, $name"
    }

    // Equals override for remove method dependency of ArrayList

    override fun equals(other: Any?): Boolean {
        return (other is Player) && id == other.id
    }

    // CompareTo override for sorting

    override fun compareTo(other: Player): Int {
        return other.points - points
    }
}