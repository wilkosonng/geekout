package com.example.geekout.classes

class Game() {
    // Specifies which state the game is in.

    enum class State {
        LOBBY, DRAW, BID, TASK, REVIEW, FINISH, HOST_DC
    }

    // Specifies player actions

    enum class Action {
        BID, BID_PASS, BID_TIMEOUT, REVIEW_VETO, REVIEW_ACCEPT, REVIEW_TIMEOUT
    }

    // Specifies challenge roll colors

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
    private var answers: ArrayList<String> = ArrayList()
    private var actions: HashMap<Player, Action> = HashMap()
    private var currentBid: HashMap<Player, Int> = HashMap()
    private var gameState: State = State.LOBBY
    private var currentTurn: Player? = null
    private var activePlayer: Player? = null
    private var currentCard: Card? = null
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

    // Setter for Players

    fun setPlayers(playersList: ArrayList<Player>) {
        players = playersList
    }

    // Getter for State

    fun getState(): State {
        return gameState
    }

    // Setter for State

    fun setState(state: State) {
        gameState = state
    }

    // Getter for currentbid

    fun getBid(): HashMap<Player, Int> {
        return currentBid
    }

    // Setter for currentBid

    fun setBid(bid: HashMap<Player, Int>) {
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

    // Getter for Active

    fun getActive(): Player? {
        return activePlayer
    }

    // Setter for Active

    fun setActive(active: Player) {
        activePlayer = active
    }

    // Getter for Card

    fun getCard(): Card? {
        return currentCard
    }

    // Setter for Card

    fun setCard(card: Card) {
        currentCard = card
    }

    // Getter for Answers

    fun getAnswers(): ArrayList<String> {
        return answers
    }

    // Setter for answers

    fun setAnswers(answerList: ArrayList<String>) {
        answers = answerList
    }

    // Getter for Actions

    fun getActions(): HashMap<Player, Action> {
        return actions
    }

    // Setter for Specific Player Action

    fun submitAction(player: Player, action: Action) {
        actions[player] = action
    }

    // Clears actions

    fun clearActions() {
        actions.clear()
    }

    // Setter for Actions

    fun setActions(actionsMap: HashMap<Player, Action>) {
        actions = actionsMap
    }

    // Rolls a color to be used.

    fun rollColor(): Roll {
        return Roll.values().toList().shuffled().first()
    }

    // Public function for firebase database to determine if the lobby can be joined.

    fun isJoinable(): Boolean {
        return (players.size < 16 && gameState == State.LOBBY)
    }
}