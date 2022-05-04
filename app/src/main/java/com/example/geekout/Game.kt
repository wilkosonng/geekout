package com.example.geekout

import android.util.Log
import java.io.Serializable

class Game() {
    // Todo: Define Game Class for Database and sync use.

    // Specifies which state the game is in.

    enum class State {
        LOBBY, DRAW, ROLL, BID, TASK, FINISH
    }

    // Initializes variables used for game.

    private var players: ArrayList<Player> = ArrayList()
    private var gameState: Game.State = State.LOBBY

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

    // Public function for firebase database to determine if the lobby can be joined.

    fun isJoinable(): Boolean {
        return (players.size < 16 && gameState == State.LOBBY)
    }
}