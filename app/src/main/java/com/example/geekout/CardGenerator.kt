package com.example.geekout

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
        return Card(4, "Hello, this is a yellow card")
    }

    private fun generateRed() : Card {
        return Card(3, "Hello, this is a red card")
    }

    private fun generateBlue() : Card {
        return Card(2, "Hello, this is a blue card")
    }

    private fun generateGreen() : Card {
        return Card(5, "Hello, this is a green card")
    }

    private fun generateBlack() : Card {
        return Card(6, "Hello, this is a black card")
    }

}