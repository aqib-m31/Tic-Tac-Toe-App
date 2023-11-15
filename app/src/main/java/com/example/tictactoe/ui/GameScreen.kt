@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tictactoe.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.R
import com.example.tictactoe.data.Mark
import com.example.tictactoe.data.Player
import com.example.tictactoe.ui.theme.TicTacToeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeApp() {
    val viewModel: GameViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsState().value
    Scaffold(
        topBar = { GameAppBar() }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!uiState.gameInProgress) {
                GameStartScreen(
                    playerOneName = uiState.playerOne.name,
                    playerTwoName = uiState.playerTwo.name,
                    onPlayerOneNameChange = { viewModel.onPlayerOneNameChange(it) },
                    onPlayerTwoNameChange = { viewModel.onPlayerTwoNameChange(it) },
                    isXPlayerOneMark = uiState.playerOne.mark == Mark.X,
                    updatePlayerOneMark = { viewModel.updatePlayerMarks(it) },
                    isNameError = uiState.isNameError,
                    onStartGame = { viewModel.startGame() }
                )
            } else {
                GameBoard(
                    boardState = uiState.boardState,
                    onMarkClick = { row, col, currentMark ->
                        if (!uiState.isGameFinished) {
                            viewModel.updateMove(
                                row,
                                col,
                                currentMark
                            )
                        }
                    },
                    currentMark = uiState.currentMark,
                    currentTurn = uiState.currentTurn,
                    isGameFinished = uiState.isGameFinished,
                    winner = uiState.winner,
                    onOneMoreRound = { viewModel.restartGame() },
                    onReset = { viewModel.resetGame() }
                )
            }

        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameAppBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = modifier.size(50.dp)
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameStartScreen(
    playerOneName: String,
    playerTwoName: String,
    onPlayerOneNameChange: (name: String) -> Unit,
    onPlayerTwoNameChange: (name: String) -> Unit,
    isXPlayerOneMark: Boolean,
    updatePlayerOneMark: (Mark) -> Unit,
    isNameError: Boolean,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = playerOneName, onValueChange = {
            onPlayerOneNameChange(it)
        },
        label = {
            Text(text = stringResource(R.string.player_1_name_label))
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Person,
                contentDescription = playerOneName
            )
        },
        isError = isNameError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    OutlinedTextField(
        value = playerTwoName, onValueChange = { onPlayerTwoNameChange(it) },
        label = {
            Text(text = stringResource(R.string.player_2_name_label))
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Person,
                contentDescription = playerTwoName
            )
        },
        isError = isNameError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_large)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Card(modifier = modifier) {
            Row(modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large))) {
                MarkButton(
                    mark = Mark.O,
                    markImg = Mark.O.markImg,
                    onUpdateMark = { updatePlayerOneMark(it) },
                    elevated = !isXPlayerOneMark
                )

                Spacer(modifier = modifier.width(10.dp))
                MarkButton(
                    mark = Mark.X,
                    markImg = Mark.X.markImg,
                    onUpdateMark = { updatePlayerOneMark(it) },
                    elevated = isXPlayerOneMark
                )
            }
        }
    }
    Text(
        text = stringResource(R.string.choose_mark),
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
    )

    Button(
        onClick = onStartGame,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large))
    ) {
        Text(text = stringResource(R.string.start_game))
    }
}

@Composable
fun MarkButton(
    mark: Mark,
    @DrawableRes markImg: Int,
    onUpdateMark: (Mark) -> Unit,
    elevated: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onUpdateMark(mark) }
            .size(dimensionResource(id = R.dimen.card_size)),
        shadowElevation = if (elevated) 5.dp else 0.dp,
        tonalElevation = if (elevated) 24.dp else 0.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Image(
            painter = painterResource(id = markImg), contentDescription = null,
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Composable
fun GameBoard(
    isGameFinished: Boolean,
    winner: Player?,
    currentTurn: Player,
    @DrawableRes currentMark: Int,
    boardState: List<List<Pair<Boolean, Int>>>,
    onMarkClick: (row: Int, col: Int, currentMark: Int) -> Unit,
    onReset: () -> Unit,
    onOneMoreRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isGameFinished) {
            Text(
                text = if (winner != null) "${winner.name} WON!" else "TIE",
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                modifier = modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium))
            )
        }
        Card {
            for (row in 0..2) {
                Row(modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small))) {
                    for (col in 0..2) {
                        Surface(
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.card_size_lg))
                                .padding(dimensionResource(id = R.dimen.padding_medium)),
                            shadowElevation = dimensionResource(id = R.dimen.card_elevation),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = modifier
                                    .fillMaxSize()
                                    .clickable { onMarkClick(row, col, currentMark) },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (boardState[row][col].first) {
                                    Image(
                                        painter = painterResource(id = boardState[row][col].second),
                                        contentDescription = null,
                                        modifier = modifier
                                            .padding(dimensionResource(id = R.dimen.padding_large)),
                                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isGameFinished) {
            GameController(onReset = onReset, onOneMoreRound = onOneMoreRound)
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Text(
                    text = "Current Turn: ${currentTurn.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = modifier.padding(end = dimensionResource(id = R.dimen.padding_medium))
                )
                Image(
                    painter = painterResource(id = currentMark), contentDescription = null,
                    modifier = modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .size(dimensionResource(id = R.dimen.mark_size_sm)),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}

@Composable
fun GameController(
    onReset: () -> Unit,
    onOneMoreRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Button(onClick = onReset) {
            Text(text = "Reset")
        }
        Button(onClick = onOneMoreRound) {
            Text(text = "One More Round")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicToeAppPreview() {
    TicTacToeTheme {
        TicTacToeApp()
    }
}

@Preview(showBackground = true)
@Composable
fun GameBoardPreview() {
    TicTacToeTheme {
        GameBoard(
            currentTurn = Player(name = "ABC", mark = Mark.X),
            currentMark = Mark.X.markImg,
            boardState = List(3) { List(3) { false to Mark.X.markImg } },
            onMarkClick = { _, _, _ -> run {} },
            isGameFinished = true,
            winner = Player(name = "ABC", mark = Mark.X),
            onOneMoreRound = {},
            onReset = {}
        )
    }
}