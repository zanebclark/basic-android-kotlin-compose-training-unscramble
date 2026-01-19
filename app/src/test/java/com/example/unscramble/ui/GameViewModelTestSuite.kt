package com.example.unscramble.ui

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {
    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val availableWords: Set<String> =
            setOf("at", "sea",)
        val viewModel = GameViewModel(availableWords = availableWords)
        val gameState = viewModel.uiState.value
        // Ensure that the scrambled word is not empty upon initialization
        assertNotNull(gameState.scrambledWord)
        assertNotEquals("", gameState.scrambledWord)

        // Sort the scrambled word
        val scrambledSorted = gameState.scrambledWord.toCharArray().sortedArray().joinToString("")

        // Check if any of the available words, when sorted, match the scrambled word
        val isMatchFound = availableWords.any { word ->
            word.toCharArray().sortedArray().joinToString("") == scrambledSorted
        }

        assertTrue("The scrambled word should be a permutation of one of the available words", isMatchFound)
    }

    @Test
    fun gameViewModel_ResetGame_StateIsReset() {
        val availableWords: Set<String> = setOf("at", "sea",)
        val viewModel = GameViewModel(availableWords = availableWords)
        // Capture initial scrambled word
        val initialScrambledWord = viewModel.uiState.value.scrambledWord
        
        // Reset the game
        viewModel.resetGame()
        
        val currentScrambledWord = viewModel.uiState.value.scrambledWord
        
        // Note: In a real test, you might want to mock the word provider to 
        // guarantee the word changes, but here we check it's at least valid.
        assertNotNull(currentScrambledWord)
        assertNotEquals(initialScrambledWord, currentScrambledWord)
    }

    @Test
    fun gameViewModel_NoMoreWords_ThrowsException() {
        val availableWords: Set<String> =
            setOf()
        assertThrows(NoSuchElementException::class.java) {
            GameViewModel(availableWords = availableWords)
        }
    }
}
