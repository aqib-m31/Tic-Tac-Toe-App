package com.example.tictactoe.ui

import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player

/**
 * Represents the UI state of the game.
 * @property playerOne The first player.
 * @property playerTwo The second player.
 * @property winner The player who won the game, or null if there's no winner yet.
 * @property boardState The current state of the game board.
 * @property xIsNext Whether it's X's turn.
 * @property gameInProgress Whether the game is in progress.
 * @property isNameError Whether there's an error with the player names.
 * @property isGameFinished Whether the game is finished.
 */
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