package com.example.tictactoe.ui

import androidx.lifecycle.ViewModel
import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * GameViewModel is a ViewModel that manages and stores UI-related data for the game.
 * It exposes both the game setup and board state for observers to react to changes.
 */
class GameViewModel : ViewModel() {
    // Mutable state flow for game setup and board state, private to avoid external modification
    private val _gameSetup = MutableStateFlow(GameSetup())
    private val _boardState = MutableStateFlow(BoardState())

    // Public state flow for game setup and board state, exposes the state for observers
    val gameSetup: StateFlow<GameSetup> = _gameSetup.asStateFlow()
    val boardState: StateFlow<BoardState> = _boardState.asStateFlow()

    // List of winning lines in the game. Each line is represented by a list of indices.
    private val lines: List<List<Int>> = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    /**
     * Updates the name of Player One.
     * @param name The new name for Player One.
     */
    fun onPlayerOneNameChange(name: String) {
        _gameSetup.update { currentState ->
            currentState.copy(
                playerOne = currentState.playerOne.copy(name = name)
            )
        }
    }

    /**
     * Updates the name of Player Two.
     * @param name The new name for Player Two.
     */
    fun onPlayerTwoNameChange(name: String) {
        _gameSetup.update { currentState ->
            currentState.copy(
                playerTwo = currentState.playerTwo.copy(name = name)
            )
        }
    }

    /**
     * Updates the marks of the players.
     * @param playerOneMark The mark for Player One. Player Two's mark is automatically set to the other mark.
     */
    fun updatePlayerMarks(playerOneMark: Mark) {
        _gameSetup.update { currentState ->
            currentState.updateMarks(playerOneMark)
        }
    }

    /**
     * Checks if there's a winner in the current game state.
     * @return The mark of the winner, or null if there's no winner yet.
     */
    private fun checkWinner(): Mark? {
        val marks = _boardState.value.marks

        // Iterate over all winning lines
        for (i in lines.indices) {
            val (a, b, c) = lines[i]

            // If all cells in a line have the same mark, we have a winner
            if (marks[a] != null && marks[b] != null && marks[c] != null) {
                if (marks[a] == marks[b] && marks[a] == marks[c]) {
                    // Handle the winner and return the winning mark
                    marks[a]?.let { handleWinner(it) }
                    return marks[a]
                }
            }
        }
        // If no winner is found, return null
        return null
    }

    /**
     * Handles a move made by a player.
     * @param i The index of the cell where the move is made.
     */
    fun handleMove(i: Int) {
        // If the cell is already marked or the game is finished, ignore the move
        if (_boardState.value.markFor(i) != null || isGameFinished()) {
            return
        }
        _boardState.update { currentState ->
            // Update the board state with the new move
            currentState.copy(
                marks = currentState.marks.mapIndexed { index, mark ->
                    if (index == i) {
                        // If the index matches the move, set the mark to the current player's mark
                        currentState.currentTurn
                    } else {
                        // Otherwise, keep the existing mark
                        mark
                    }
                },
                currentTurn = if (currentState.currentTurn == Mark.X) Mark.O else Mark.X
            )
        }
        checkWinner()
    }

    /**
     * Handles the situation when a winner is found.
     * @param mark The mark of the winner.
     */
    private fun handleWinner(mark: Mark) {
        _boardState.update { currentState ->
            currentState.copy(
                winner = mark
            )
        }
    }

    /**
     * Restarts the game by resetting the board state.
     */
    fun restartGame() {
        _boardState.update { currentState ->
            currentState.copy(
                marks = List(9) { null },
                currentTurn = Mark.X,
                winner = null
            )
        }
    }

    /**
     * Resets the game by resetting both the game setup and the board state.
     */
    fun resetGame() {
        restartGame()
        _gameSetup.update { currentState ->
            currentState.copy(
                playerOne = Player("Player 1", Mark.X),
                playerTwo = Player("Player 2", Mark.O),
                gameInProgress = false
            )
        }
    }

    /**
     * Initializes the ViewModel by resetting the game.
     */
    init {
        resetGame()
    }

    /**
     * Starts the game if both player names are valid.
     */
    fun startGame() {
        _gameSetup.update { currentState ->
            val nameOneLength = currentState.playerOne.name.length
            val nameTwoLength = currentState.playerTwo.name.length
            if (nameOneLength in 3..20 && nameTwoLength in 3..20) {
                currentState.copy(
                    gameInProgress = true,
                    isNameError = false
                )
            } else {
                currentState.copy(
                    isNameError = true
                )
            }
        }
    }

    /**
     * Returns the player whose turn it is.
     * @return The current player.
     */
    fun currentTurn(): Player {
        return if (_boardState.value.currentTurn == _gameSetup.value.playerOne.mark) _gameSetup.value.playerOne else gameSetup.value.playerTwo
    }

    /**
     * Returns the winner of the game, or "TIE!" if the game is a tie.
     * @return The winner of the game.
     */
    fun getWinner(): String {
        return if (_boardState.value.winner == null) {
            "TIE!"
        } else {
            "${
                if (_boardState.value.winner == _gameSetup.value.playerOne.mark) {
                    _gameSetup.value.playerOne.name
                } else _gameSetup.value.playerTwo.name
            } WON!"
        }
    }

    /**
     * Checks if Player One's mark is X.
     * @return True if Player One's mark is X, false otherwise.
     */
    fun isXPlayerOneMark(): Boolean {
        return _gameSetup.value.playerOne.mark == Mark.X
    }

    /**
     * Checks if the game is finished.
     * @return True if the game is finished, false otherwise.
     */
    fun isGameFinished(): Boolean {
        for (mark in _boardState.value.marks) {
            // If there's an empty cell and no winner, the game is not finished
            if (mark == null && _boardState.value.winner == null) return false
        }
        // If all cells are marked or there's a winner, the game is finished
        return true
    }
}