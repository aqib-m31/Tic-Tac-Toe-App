package com.example.tictactoe.ui

import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player

data class GameUiState(
    val playerOne: Player = Player("", Mark.X),
    val playerTwo: Player = Player("", Mark.O),
    val winner: Player? = null,
    val boardState: List<Mark?> = List(9) { null },
    val xIsNext: Boolean = true,
    val gameInProgress: Boolean = false,
    val isNameError: Boolean = false,
    val isGameFinished: Boolean = false
)