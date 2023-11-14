package com.example.tictactoe.ui

import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player

data class GameUiState(
    val playerOne: Player = Player(name = "", mark = Mark.X),
    val playerTwo: Player = Player(name = "", mark = Mark.O),
    val isNameError: Boolean = false,
    val isXCurrent: Boolean = true,
    val isGameFinished: Boolean = false,
    val winner: Player? = null,
    val gameInProgress: Boolean = true,
    val currentMark: Int = Mark.X.markImg,
    val boardState: List<List<Pair<Boolean, Int>>> = List(3) { List(3) { false to Mark.X.markImg } },
    val currentTurn: Player = if (playerOne.mark == Mark.X) playerOne else playerTwo
)