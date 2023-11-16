package com.example.tictactoe.ui

import androidx.lifecycle.ViewModel
import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

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

    fun onPlayerOneNameChange(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                playerOne = currentState.playerOne.copy(name = name)
            )
        }
    }

    fun onPlayerTwoNameChange(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                playerTwo = currentState.playerTwo.copy(name = name)
            )
        }
    }

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

    private fun checkWinner(): Mark? {
        val boardState = uiState.value.boardState
        val playerOne = uiState.value.playerOne
        val playerTwo = uiState.value.playerTwo
        for (i in lines.indices) {
            val (a, b, c) = lines[i]
            if (boardState[a] != null && boardState[b] != null && boardState[c] != null) {
                if (boardState[a] == boardState[b] && boardState[a] == boardState[c]) {
                    boardState[a]?.let { handleWinner(if (playerOne.mark == it) playerOne else playerTwo) }
                    _uiState.update { currentState ->
                        currentState.copy(
                            isGameFinished = true
                        )
                    }
                    return boardState[a]
                }
            }
        }

        for (i in boardState.indices) {
            if (boardState[i] == null) return null
        }
        _uiState.update { currentState ->
            currentState.copy(
                isGameFinished = true
            )
        }
        return null
    }

    fun handleMove(i: Int) {
        if (uiState.value.boardState[i] != null) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                boardState = currentState.boardState.mapIndexed { index, value ->
                    if (index == i) {
                        if (currentState.xIsNext) Mark.X else Mark.O
                    } else {
                        value
                    }
                },
                xIsNext = !currentState.xIsNext
            )
        }
        checkWinner()
    }

    private fun handleWinner(winner: Player) {
        _uiState.update { currentState ->
            currentState.copy(
                winner = winner,
                isGameFinished = true
            )
        }
    }

    private fun initializeGameState(
        playerOne: Player,
        playerTwo: Player,
        gameInProgress: Boolean
    ): GameUiState {
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

    fun restartGame() {
        _uiState.update { currentState ->
            initializeGameState(currentState.playerOne, currentState.playerTwo, true)
        }
    }

    fun resetGame() {
        _uiState.update {
            initializeGameState(Player("", Mark.X), Player("", Mark.O), false)
        }
    }

    init {
        resetGame()
    }

    fun startGame() {
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

    fun currentTurn(): Player {
        return if (uiState.value.xIsNext && uiState.value.playerOne.mark == Mark.X) {
            uiState.value.playerOne
        } else {
            uiState.value.playerTwo
        }
    }

    fun getWinner(): String {
        return if (uiState.value.winner == null) {
            "TIE!"
        } else {
            "${uiState.value.winner?.name} WON!"
        }
    }

    fun isXPlayerOneMark(): Boolean {
        return uiState.value.playerOne.mark == Mark.X
    }
}
