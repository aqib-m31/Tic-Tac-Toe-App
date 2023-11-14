package com.example.tictactoe.ui

import androidx.lifecycle.ViewModel
import com.example.tictactoe.R
import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private val winningConditions: List<List<Pair<Int, Int>>> = listOf(
        listOf(0 to 0, 0 to 1, 0 to 2),
        listOf(1 to 0, 1 to 1, 1 to 2),
        listOf(2 to 0, 2 to 1, 2 to 2),
        listOf(0 to 0, 1 to 0, 2 to 0),
        listOf(0 to 1, 1 to 1, 2 to 1),
        listOf(0 to 2, 1 to 2, 2 to 2),
        listOf(0 to 0, 1 to 1, 2 to 2),
        listOf(0 to 2, 1 to 1, 2 to 0),
    )

    fun onPlayerOneNameChange(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                playerOne = currentState.playerOne.copy(name = name),
            )
        }
    }

    fun onPlayerTwoNameChange(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                playerTwo = currentState.playerTwo.copy(name = name),
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

    fun startGame() {
        _uiState.update { currentState ->
            if (currentState.playerOne.name.length in 3..20 && currentState.playerTwo.name.length in 3..20) {
                currentState.copy(
                    currentTurn = if (currentState.currentTurn == currentState.playerOne) currentState.playerTwo else currentState.playerOne,
                    isNameError = false,
                    gameInProgress = true
                )
            } else {
                currentState.copy(
                    isNameError = true
                )
            }
        }
    }

    fun updateMove(row: Int, col: Int, currentMark: Int) {
        if (_uiState.value.boardState[row][col].first) return
        _uiState.update { currentState ->
            val updatedBoardState: List<List<Pair<Boolean, Int>>> =
                currentState.boardState.mapIndexed { rowIndex, rowList ->
                    rowList.mapIndexed { colIndex, pair ->
                        if (row == rowIndex && col == colIndex) {
                            true to currentMark
                        } else {
                            pair
                        }
                    }
                }
            currentState.copy(
                currentMark = if (currentState.isXCurrent) R.drawable.o_mark else R.drawable.x_mark,
                isXCurrent = !currentState.isXCurrent,
                currentTurn = if (currentState.currentTurn == currentState.playerOne) currentState.playerTwo else currentState.playerOne,
                boardState = updatedBoardState
            )
        }
        checkWinner()
    }

    private fun checkWinner() {
        val boardState = uiState.value.boardState
        winningConditions.forEach { row ->
            val pairOne = row[0]
            val pairTwo = row[1]
            val pairThree = row[2]

            if (boardState[pairOne.first][pairOne.second].first && boardState[pairTwo.first][pairTwo.second].first && boardState[pairThree.first][pairThree.second].first) {
                if (
                    boardState[pairOne.first][pairOne.second] == boardState[pairTwo.first][pairTwo.second] &&
                    boardState[pairOne.first][pairOne.second] == boardState[pairThree.first][pairThree.second]
                ) {
                    handleGameWinner()
                }
            }
        }
        if (!uiState.value.isGameFinished) {
            for (i in boardState.indices) {
                for (j in 0 until boardState[i].size) {
                    if (!boardState[i][j].first) {
                        return
                    }
                }
            }
            _uiState.update { currentState ->
                currentState.copy(
                    isGameFinished = true,
                    winner = null,
                )
            }
        }
    }

    private fun handleGameWinner() {
        _uiState.update { currentState ->
            currentState.copy(
                isGameFinished = true,
                winner = if (uiState.value.currentTurn == currentState.playerOne) currentState.playerTwo else currentState.playerOne
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
            isNameError = false,
            isXCurrent = true,
            isGameFinished = false,
            winner = null,
            gameInProgress = gameInProgress,
            currentMark = Mark.X.markImg,
            boardState = List(3) { List(3) { false to Mark.X.markImg } },
            currentTurn = if (playerOne.mark == Mark.X) playerOne else playerTwo
        )
    }

    fun restartGame() {
        _uiState.update { currentState ->
            initializeGameState(currentState.playerOne, currentState.playerTwo, true)
        }
    }

    fun resetGame() {
        _uiState.update {
            initializeGameState(
                Player(name = "", mark = Mark.X),
                Player(name = "", mark = Mark.O),
                false
            )
        }
    }

    init {
        resetGame()
    }

}
