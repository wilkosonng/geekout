package com.example.geekout

import android.content.res.Resources
import java.io.File
import java.io.InputStream
import com.example.geekout.classes.Game

class CardGenerator (var gameCards : Array<String>){
    // TODO Add libraries and actual card generation
    // private var gameCards = File("app/src/main/res/raw/gamecards.txt").readLines().shuffled()

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
        var txt: String? = gameCards.firstOrNull() ?: return generateBlue()
        gameCards.drop(0)
        return Card(3, txt!!, Game.Roll.RED)
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