package com.example.a2048game.presentation

import Game

class GameVisualisation(var game:Game) {
    var gameState: Boolean = false

    fun changeGameState() {
        this.gameState = !this.gameState
    }
}