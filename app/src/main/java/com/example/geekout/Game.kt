package com.example.geekout

import android.util.Log

class Game() {
    // Todo: Define Game Class for Database and sync use.

    // Specifies which state the game is in.

    enum class State {
        LOBBY, DRAW, ROLL, BID, TASK, REVIEW, FINISH
    }

    enum class Roll {
        RED, BLUE, YELLOW, GREEN, BLACK
    }

    /* Initializes variables used for game.
     * players: A List of all players in the game.
     * gameState: the state the game is in.
     * currentTurn: The player whose turn it is (starts bidding)
     * activePlayer: the player who is currently taking the action (ex. Bidding)
     * currentCard: the card that is being played
     * currentBid: the current highest bid for the bidding phase
     * avatars: the list of currently-available avatars for players
    */

    private var players: ArrayList<Player> = ArrayList()
    private var gameState: State = State.LOBBY
    private var currentTurn: Player? = null
    private var activePlayer: Player? = null
    private var currentCard: Card? = null
    private var currentBid: Int = -1
    private var avatars: ArrayList<String> = arrayListOf(
        "🐍", "🐒", "🐕", "🐖", "🐇", "🐪", "🐘", "🦒", "🐀", "🦜",
        "🐢", "🦖", "🐬", "🦈", "🐅", "🐎", "🦥", "🦘", "🐋")

    // Adds a player

    fun addPlayer(player: Player) {
        players.add(player)
    }

    // Removes a player

    fun removePlayer(player: Player) {
        players.remove(player)
    }

    // Getter for Players

    fun getPlayers(): ArrayList<Player> {
        return players
    }

    // Getter for State

    fun getState(): Game.State {
        return gameState
    }

    // Setter for State

    fun setState(state: Game.State) {
        gameState = state
    }

    // Getter for currentbid

    fun getBid(): Int {
        return currentBid
    }

    // Setter for currentBid

    fun setBid(bid: Int) {
        currentBid = bid
    }

    // Getter for currentTurn

    fun getTurn(): Player? {
        return currentTurn
    }

    // Setter for currentTurn

    fun setTurn(player: Player) {
        currentTurn = player
    }

    // Getter for Avatars

    fun getAvatars(): ArrayList<String> {
        return avatars
    }

    // Add Avatars

    fun addAvatar(avatar: String) {
        avatars.add(avatar)
    }

    // Remove Avatars

    fun removeAvatar(avatar: String) {
        avatars.remove(avatar)
    }

    // Setter for Avatars

    fun setAvatars(avatarList: ArrayList<String>) {
        avatars = avatarList
    }

    fun rollColor(): Game.Roll {
        return Roll.values().toList().shuffled().first()
    }

    // Public function for firebase database to determine if the lobby can be joined.

    fun isJoinable(): Boolean {
        return (players.size < 16 && gameState == State.LOBBY)
    }
}