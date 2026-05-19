package com.wordmaster.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wordmaster.app.WordMasterApp
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.data.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ReviewState(
    val currentWord: WordEntity? = null,
    val answers: List<WordEntity> = emptyList(),
    val selectedAnswer: WordEntity? = null,
    val isCorrect: Boolean? = null,
    val isLoading: Boolean = true,
    val reviewCorrect: Int = 0,
    val reviewWrong: Int = 0,
    val reviewTotal: Int = 0
)

class LearnedWordsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WordRepository = (application as WordMasterApp).repository

    val learnedWords: StateFlow<List<WordEntity>> = repository.learnedWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _reviewState = MutableStateFlow(ReviewState())
    val reviewState: StateFlow<ReviewState> = _reviewState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredWords: StateFlow<List<WordEntity>> = combine(learnedWords, _searchQuery) { words, query ->
        if (query.isBlank()) words
        else words.filter {
            it.english.contains(query, ignoreCase = true) ||
                    it.russian.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun markAsUnlearned(wordId: Int) {
        viewModelScope.launch {
            repository.markAsUnlearned(wordId)
        }
    }

    fun resetAllLearned() {
        viewModelScope.launch {
            repository.resetAllLearned()
        }
    }

    fun loadReviewWord() {
        viewModelScope.launch {
            _reviewState.update { it.copy(isLoading = true, selectedAnswer = null, isCorrect = null) }

            val word = repository.getRandomLearnedWord()
            if (word != null) {
                val wrongAnswers = repository.getWrongAnswersForLearned(word, 5)
                val allAnswers = (wrongAnswers + word).shuffled()

                _reviewState.update {
                    it.copy(
                        currentWord = word,
                        answers = allAnswers,
                        isLoading = false
                    )
                }
            } else {
                _reviewState.update { it.copy(isLoading = false, currentWord = null) }
            }
        }
    }

    fun selectReviewAnswer(answer: WordEntity) {
        val currentState = _reviewState.value
        if (currentState.selectedAnswer != null) return
        if (currentState.currentWord == null) return

        val isCorrect = answer.id == currentState.currentWord.id

        viewModelScope.launch {
            if (!isCorrect) {
                // Если ошибся при повторении — возвращаем слово на изучение
                repository.markAsUnlearned(currentState.currentWord.id)
                repository.onWrongAnswer(currentState.currentWord)
            }

            _reviewState.update {
                it.copy(
                    selectedAnswer = answer,
                    isCorrect = isCorrect,
                    reviewCorrect = if (isCorrect) it.reviewCorrect + 1 else it.reviewCorrect,
                    reviewWrong = if (!isCorrect) it.reviewWrong + 1 else it.reviewWrong,
                    reviewTotal = it.reviewTotal + 1
                )
            }
        }
    }
}
