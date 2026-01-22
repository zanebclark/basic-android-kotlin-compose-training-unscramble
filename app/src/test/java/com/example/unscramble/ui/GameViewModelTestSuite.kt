package com.example.unscramble.ui

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertFalse
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
        val unScrambledWord = gameState.word
        assertNotEquals("", gameState.scrambledWord)

        // Assert that current word is scrambled.
        assertNotEquals(unScrambledWord, gameState.scrambledWord)
        // Assert that current word count is set to 1.
        assertTrue(gameState.wordCount == 1)
        // Assert that initially the score is 0.
        assertTrue(gameState.score == 0)
        // Assert that the wrong word guessed is false.
        assertFalse(gameState.isGuessedWordWrong)
        // Assert that game is not over.
        assertFalse(gameState.isGameOver)

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
    fun gameViewModel_InsufficientWords_ThrowsException() {
        val availableWords: Set<String> = setOf("at")
        assertThrows(IllegalArgumentException::class.java) {
            GameViewModel(availableWords = availableWords)
        }
    }

    @Test
    fun gameViewModel_NoWords_ThrowsException() {
        val availableWords: Set<String> = setOf()
        assertThrows(NoSuchElementException::class.java) {
            GameViewModel(availableWords = availableWords)
        }
    }

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset()  {
        val availableWords: Set<String> = setOf("at", "sea",)
        val viewModel = GameViewModel(availableWords = availableWords)

        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = currentGameUiState.word
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        // Assert that checkUserGuess() method updates isGuessedWordWrong is updated correctly.
        assertFalse(currentGameUiState.isGuessedWordWrong)
        // Assert that the score is updated correctly.
        assertEquals(20, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet()  {
        val availableWords: Set<String> = setOf("at", "sea",)
        val viewModel = GameViewModel(availableWords = availableWords)

        viewModel.updateUserGuess("incorrect")
        viewModel.checkUserGuess()
        val currentGameUiState = viewModel.uiState.value

        // Assert that checkUserGuess() method updates isGuessedWordWrong is updated correctly.
        assertTrue(currentGameUiState.isGuessedWordWrong)
        // Assert that the score is updated correctly.
        assertEquals(0, currentGameUiState.score)
    }


    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        val viewModel = GameViewModel(availableWords = allWords)
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        repeat(MAX_NO_OF_WORDS) {
            val correctPlayerWord = currentGameUiState.word
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            // Assert that after each correct answer, score is updated correctly.
            assertEquals(expectedScore, currentGameUiState.score)
        }
        // Assert that after all questions are answered, the current word count is up-to-date.
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.wordCount -1)
        // Assert that after 10 questions are answered, the game is over.
        assertTrue(currentGameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        val viewModel = GameViewModel(availableWords = allWords)
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = currentGameUiState.word
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        val lastWordCount = currentGameUiState.wordCount
        viewModel.skipWord()
        currentGameUiState = viewModel.uiState.value
        // Assert that score remains unchanged after word is skipped.
        assertEquals(20, currentGameUiState.score)
        // Assert that word count is increased by 1 after word is skipped.
        assertEquals(lastWordCount + 1, currentGameUiState.wordCount)
    }
}
