package com.example.tictactoe.ui

import androidx.lifecycle.ViewModel
import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Game. It holds the state of the UI and provides methods to update the state.
 */
class GameViewModel : ViewModel() {
    /**
     * MutableStateFlow for the UI state of the game.
     */
    private val _uiState = MutableStateFlow(GameUiState())

    /**
     * StateFlow for the UI state of the game.
     */
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /**
     * List of winning lines in the game.
     */
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
        _uiState.update { currentState ->
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
        _uiState.update { currentState ->
            currentState.copy(
                playerTwo = currentState.playerTwo.copy(name = name)
            )
        }
    }

    /**
     * Updates the marks of the players based on the mark of Player One.
     * @param playerOneMark The mark of Player One.
     */
    fun updatePlayerMarks(playerOneMark: Mark) {
        _uiState.update { currentState ->
            if (playerOneMark == Mark.X) {
                currentState.copy(
                    playerOne = currentState.playerOne.copy(mark = Mark.X),
                    playerTwo = currentState.playerTwo.copy(mark = Mark.O),
                )
            } else {
                currentState.copy(
                    playerOne = currentState.playerOne.copy(mark = Mark.O),
                    playerTwo = currentState.playerTwo.copy(mark = Mark.X),
                )
            }
        }
    }


    /**
     * Checks if there's a winner in the game.
     * @return The mark of the winner, or null if there's no winner yet.
     */
    private fun checkWinner(): Mark? {
        // Get the current state of the board and the players
        val boardState = uiState.value.boardState
        val playerOne = uiState.value.playerOne
        val playerTwo = uiState.value.playerTwo

        // Iterate over all possible winning lines
        for (i in lines.indices) {
            val (a, b, c) = lines[i]

            // If all three positions in the line are filled
            if (boardState[a] != null && boardState[b] != null && boardState[c] != null) {
                // If all three positions have the same mark
                if (boardState[a] == boardState[b] && boardState[a] == boardState[c]) {
                    // Handle the winner and update the UI state
                    boardState[a]?.let { handleWinner(if (playerOne.mark == it) playerOne else playerTwo) }
                    _uiState.update { currentState ->
                        currentState.copy(
                            isGameFinished = true
                        )
                    }
                    // Return the winning mark
                    return boardState[a]
                }
            }
        }

        // If there are no empty positions left on the board, the game is a draw
        for (i in boardState.indices) {
            if (boardState[i] == null) return null
        }
        _uiState.update { currentState ->
            currentState.copy(
                isGameFinished = true
            )
        }
        // Return null to indicate a draw
        return null
    }

    /**
     * Handles a move by a player.
     * @param i The index of the move.
     */
    fun handleMove(i: Int) {
        // If the selected position is already filled or game is finished, ignore the move
        if (uiState.value.boardState[i] != null || uiState.value.isGameFinished) {
            return
        }

        // Update the UI state with the new move
        _uiState.update { currentState ->
            currentState.copy(
                // Update the board state with the new move
                boardState = currentState.boardState.mapIndexed { index, value ->
                    if (index == i) {
                        // If it's X's turn, place an X, otherwise place an O
                        if (currentState.xIsNext) Mark.X else Mark.O
                    } else {
                        // Keep the existing value for other positions
                        value
                    }
                },
                // Switch the turn to the other player
                xIsNext = !currentState.xIsNext
            )
        }

        // Check if the move resulted in a win
        checkWinner()
    }

    /**
     * Handles the winner of the game.
     * @param winner The player who won the game.
     */
    private fun handleWinner(winner: Player) {
        // Update the UI state to indicate the winner and that the game is finished
        _uiState.update { currentState ->
            currentState.copy(
                winner = winner,
                isGameFinished = true
            )
        }
    }

    /**
     * Initializes the game state.
     * @param playerOne The first player.
     * @param playerTwo The second player.
     * @param gameInProgress Whether the game is in progress.
     * @return The initialized game state.
     */
    private fun initializeGameState(
        playerOne: Player,
        playerTwo: Player,
        gameInProgress: Boolean
    ): GameUiState {
        // Return a new game state with the provided players and game progress,
        // and with default values for the other properties
        return GameUiState(
            playerOne = playerOne,
            playerTwo = playerTwo,
            gameInProgress = gameInProgress,
            xIsNext = true,
            isNameError = false,
            boardState = List(9) { null },
            winner = null,
            isGameFinished = false
        )
    }

    /**
     * Restarts the game.
     */
    fun restartGame() {
        // Update the UI state to restart the game with the current players
        _uiState.update { currentState ->
            initializeGameState(currentState.playerOne, currentState.playerTwo, true)
        }
    }

    /**
     * Resets the game.
     */
    fun resetGame() {
        // Update the UI state to reset the game with new players
        _uiState.update {
            initializeGameState(Player("", Mark.X), Player("", Mark.O), false)
        }
    }

    /**
     * Initializes the game state when the ViewModel is created.
     */
    init {
        resetGame()
    }

    /**
     * Starts the game.
     */
    fun startGame() {
        // Update the UI state to start the game if the player names are valid
        _uiState.update { currentState ->
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
     * @return The player whose turn it is.
     */
    fun currentTurn(): Player {
        return if ((uiState.value.xIsNext && uiState.value.playerOne.mark == Mark.X) ||
            (!uiState.value.xIsNext && uiState.value.playerOne.mark == Mark.O)
        ) {
            uiState.value.playerOne
        } else {
            uiState.value.playerTwo
        }
    }

    /**
     * Returns a string indicating the winner of the game.
     * @return A string indicating the winner of the game.
     */
    fun getWinner(): String {
        // If there's no winner, return "TIE!", otherwise return the name of the winner
        return if (uiState.value.winner == null) {
            "TIE!"
        } else {
            "${uiState.value.winner?.name} WON!"
        }
    }

    /**
     * Returns whether player one's mark is X.
     * @return True if player one's mark is X, false otherwise.
     */
    fun isXPlayerOneMark(): Boolean {
        // Return true if player one's mark is X, false otherwise
        return uiState.value.playerOne.mark == Mark.X
    }
}