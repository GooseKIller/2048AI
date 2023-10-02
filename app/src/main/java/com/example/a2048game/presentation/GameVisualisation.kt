package com.example.a2048game.presentation

import Game
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView

class GameVisualisation(var game:Game) {
    var gameState: Boolean = false

    fun changeGameState() {
        this.gameState = !this.gameState
    }
}