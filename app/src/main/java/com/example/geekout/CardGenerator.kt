package com.example.geekout

import com.example.geekout.classes.Game

class CardGenerator {
    // TODO Add libraries and actual card generation

    public fun generateCard(color : Game.Roll) : Card {

        return when (color) {
            Game.Roll.YELLOW -> generateYellow()
            Game.Roll.RED -> generateRed()
            Game.Roll.BLUE -> generateBlue()
            Game.Roll.GREEN -> generateGreen()
            Game.Roll.BLACK -> generateBlack()
        }
    }

    private fun generateYellow() : Card {
        // Sci-Fi
        return Card(4, "Hello, this is a yellow card", Game.Roll.YELLOW)
    }

    private fun generateRed() : Card {
        // Games
        return Card(3, "Hello, this is a red card", Game.Roll.RED)
    }

    private fun generateBlue() : Card {
        // Comics
        return Card(2, "Hello, this is a blue card", Game.Roll.BLUE)
    }

    private fun generateGreen() : Card {
        // Fantasy
        return Card(5, "Hello, this is a green card", Game.Roll.GREEN)
    }

    private fun generateBlack() : Card {
        // Misc.
        return Card(6, "Hello, this is a black card", Game.Roll.BLACK)
    }

}