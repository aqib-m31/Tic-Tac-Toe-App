package com.example.tictactoe.data

import com.example.tictactoe.R

enum class Mark(val markImg: Int) {
    X(R.drawable.x_mark),
    O(R.drawable.o_mark)
}

data class Player(
    val name: String,
    val mark: Mark,
)