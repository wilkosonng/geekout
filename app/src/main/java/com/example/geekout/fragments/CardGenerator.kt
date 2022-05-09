package com.example.geekout.fragments

import com.example.geekout.classes.Card
import com.example.geekout.classes.Game

import android.content.res.Resources
import java.io.File
import java.io.InputStream
import kotlin.random.Random

class CardGenerator (var sciFiCards : Array<String>, var gameCards : Array<String>,
                     var comicCards : Array<String>,  var fantasyCards : Array<String>,
                     var miscCards : Array<String>){

    public fun generateCard(color : Game.Roll) : Card {
        // Make sure there are enough cards
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
        var txt: String? = sciFiCards.firstOrNull() ?: return generateRed()
        sciFiCards.drop(1)
        return Card(generateRand(), txt!!, Game.Roll.YELLOW)
    }

    private fun generateRed() : Card {
        // Games
        var txt: String? = gameCards.firstOrNull() ?: return generateBlue()
        gameCards.drop(1)
        return Card(generateRand(), txt!!, Game.Roll.RED)
    }

    private fun generateBlue() : Card {
        // Comics
        var txt: String? = comicCards.firstOrNull() ?: return generateGreen()
        comicCards.drop(1)
        return Card(generateRand(), txt!!, Game.Roll.BLUE)
    }

    private fun generateGreen() : Card {
        // Fantasy
        var txt: String? = fantasyCards.firstOrNull() ?: return generateBlack()
        fantasyCards.drop(1)
        return Card(generateRand(), txt!!, Game.Roll.GREEN)
    }

    private fun generateBlack() : Card {
        // Misc.
        var txt: String? = miscCards.firstOrNull() ?: return generateYellow()
        miscCards.drop(1)
        return Card(generateRand(), txt!!, Game.Roll.BLACK)
    }

    private fun generateRand() : Int {
        return Random.nextInt(2, 5)
    }

}