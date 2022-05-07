package com.example.geekout

class CardGenerator {
    // TODO Add libraries and actual card generation

    public fun generateCard(color : Game.Roll) : Card {

        return when (color) {
            Game.Roll.YELLOW -> Card(4, "Hello, this is a yellow card")
            Game.Roll.RED -> Card(3, "Hello, this is a red card")
            Game.Roll.BLUE -> Card(2, "Hello, this is a blue card")
            Game.Roll.GREEN -> Card(5, "Hello, this is a green card")
            Game.Roll.BLACK -> Card(6, "Hello, this is a black card")
        }
    }

}