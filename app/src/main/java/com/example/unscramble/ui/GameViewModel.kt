package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel(
    private val availableWords: Set<String>
): ViewModel() {
    // Game UI state
    private val _uiState = MutableStateFlow(GameUiState())
    // Expose a non-mutable StateFlow as a public variable
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var usedWords: MutableSet<String> = mutableSetOf<String>()

    private var currentWord: String = getUnusedWord()
        set(value){
            usedWords.add(value)
            _uiState.update { currentState ->
                currentState.copy(
                    scrambledWord = shuffleWord(value),
                    wordCount = currentState.wordCount + 1
                )
            }
            field = value
        }

    init {
        resetGame()
    }

    private fun getUnusedWord(): String {
        val unusedWords = availableWords subtract usedWords
        if (unusedWords.isEmpty()) {
            throw NoSuchElementException("No more unused words available")
        }
        return unusedWords.random()
    }

    private fun shuffleWord(word: String): String {
        val tempWord = word.toCharArray()
        while (String(tempWord).equals(word, false)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun resetGame() {
        _uiState.update { GameUiState() }
        usedWords.clear()
        currentWord = getUnusedWord()
    }

    var userGuess by mutableStateOf("")
        private set

    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    fun checkUserGuess() {
        if (userGuess.isBlank()) return

        val isGuessCorrect: Boolean = userGuess.equals(currentWord, ignoreCase = true)
        if (isGuessCorrect) {
            currentWord = getUnusedWord()
            userGuess = ""
        }

        _uiState.update { currentState ->
            currentState.copy(
                isGuessedWordWrong = !isGuessCorrect,
                guessCount = currentState.guessCount + 1,
                score = if(isGuessCorrect) currentState.score + SCORE_INCREASE else currentState.score,
                isGameOver = currentState.wordCount > MAX_NO_OF_WORDS
            )
        }
    }

    fun skipWord() {
        currentWord = getUnusedWord()
        _uiState.update { currentState ->
            currentState.copy(
                isGuessedWordWrong = false,
                isGameOver = currentState.wordCount > MAX_NO_OF_WORDS
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Here you can pull dependencies.
                // For now, we use the 'allWords' data directly.
                GameViewModel(availableWords = allWords)
            }
        }
    }
}
