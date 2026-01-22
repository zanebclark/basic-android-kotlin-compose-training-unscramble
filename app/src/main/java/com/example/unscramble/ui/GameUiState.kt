package com.example.unscramble.ui


data class GameUiState(
    val word: String = "",
    val scrambledWord: String = "",
    val wordCount: Int = 0,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false,
    val guessCount: Int = 0,
)
