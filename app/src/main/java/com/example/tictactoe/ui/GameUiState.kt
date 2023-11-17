package com.example.tictactoe.ui

import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player

/**
 * Represents the state of the game board in the Tic Tac Toe game.
 * @property marks The marks on the game board.
 * @property currentTurn The mark of the player whose turn it is.
 * @property winner The mark of the winner of the game.
 */
data class BoardState(
    val marks: List<Mark?> = List(9) { null },
    val currentTurn: Mark = Mark.X,
    val winner: Mark? = null
) {
    /**
     * Returns the mark at the specified index on the game board.
     * @param idx The index of the square on the game board.
     * @return The mark at the specified index.
     */
    fun markFor(idx: Int): Mark? {
        return marks[idx]
    }
}

/**
 * Represents the setup for the Tic Tac Toe game.
 * @property playerOne The first player.
 * @property playerTwo The second player.
 * @property gameInProgress Whether the game is in progress.
 * @property isNameError Whether there's an error with the players' names.
 */
data class GameSetup(
    val playerOne: Player = Player("", Mark.X),
    val playerTwo: Player = Player("", Mark.O),
    val gameInProgress: Boolean = false,
    val isNameError: Boolean = false
)

/**
 * Updates the marks of the players.
 * @param playerOneMark The mark of the first player.
 * @return A copy of this `GameSetup` with the players' marks updated.
 */
fun GameSetup.updateMarks(playerOneMark: Mark) = copy(
    playerOne = playerOne.copy(mark = playerOneMark),
    playerTwo = playerTwo.copy(mark = if (playerOneMark == Mark.X) Mark.O else Mark.X)
)