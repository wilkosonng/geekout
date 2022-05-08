package com.example.geekout.fragments

import com.example.geekout.classes.Card
import com.example.geekout.classes.Game

import android.content.res.Resources
import java.io.File
import java.io.InputStream

class CardGenerator (var sciFiCards : Array<String>, var gameCards : Array<String>,
                     var comicCards : Array<String>,  var fantasyCards : Array<String>,
                     var miscCards : Array<String>){
    // TODO Add libraries and actual card generation
    // private var gameCards = File("app/src/main/res/raw/gamecards.txt").readLines().shuffled()

    public fun generateCard(color : Game.Roll) : Card {
        if(sciFiCards.size + gameCards.size + comicCards.size + fantasyCards.size + miscCards.size == 0) {
            return Card(-1, "Out of cards :(", Game.Roll.BLACK)
        }
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