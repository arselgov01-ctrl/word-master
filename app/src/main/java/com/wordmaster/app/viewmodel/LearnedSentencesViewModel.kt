package com.wordmaster.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wordmaster.app.WordMasterApp
import com.wordmaster.app.data.SentenceEntity
import com.wordmaster.app.data.SentenceRepository
import com.wordmaster.app.settings.AppSettings
import com.wordmaster.app.settings.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SentenceReviewState(
    val currentSentence: SentenceEntity? = null,
    val answers: List<SentenceEntity> = emptyList(),
    val selectedAnswer: SentenceEntity? = null,
    val isCorrect: Boolean? = null,
    val isLoading: Boolean = true,
    val reviewCorrect: Int = 0,
    val reviewWrong: Int = 0,
    val reviewTotal: Int = 0
)

class LearnedSentencesViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as WordMasterApp
    private val repository: SentenceRepository = app.sentenceRepository
    private val settingsManager: SettingsManager = app.settingsManager

    val learnedSentences: StateFlow<List<SentenceEntity>> = repository.learnedSentences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _reviewState = MutableStateFlow(SentenceReviewState())
    val reviewState: StateFlow<SentenceReviewState> = _reviewState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredSentences: StateFlow<List<SentenceEntity>> =
        combine(learnedSentences, _searchQuery) { sentences, query ->
            if (query.isBlank()) sentences
            else sentences.filter {
                it.english.contains(query, ignoreCase = true) ||
                        it.russian.contains(query, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val answerCountFlow: StateFlow<Int> = settingsManager.settings
        .map { it.answerCount }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings.DEFAULT_ANSWER_COUNT)

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun markAsUnlearned(sentenceId: Int) {
        viewModelScope.launch {
            repository.markAsUnlearned(sentenceId)
        }
    }

    fun resetAllLearned() {
        viewModelScope.launch {
            repository.resetAllLearned()
        }
    }

    fun loadReviewSentence() {
        viewModelScope.launch {
            _reviewState.update { it.copy(isLoading = true, selectedAnswer = null, isCorrect = null) }

            val sentence = repository.getRandomLearnedSentence()
            if (sentence != null) {
                val wrongCount = (answerCountFlow.value - 1).coerceAtLeast(1)
                val wrongAnswers = repository.getWrongAnswersForLearned(sentence, wrongCount)
                val allAnswers = (wrongAnswers + sentence).shuffled()
                _reviewState.update {
                    it.copy(
                        currentSentence = sentence,
                        answers = allAnswers,
                        isLoading = false
                    )
                }
            } else {
                _reviewState.update { it.copy(isLoading = false, currentSentence = null) }
            }
        }
    }

    fun selectReviewAnswer(answer: SentenceEntity) {
        val current = _reviewState.value
        if (current.selectedAnswer != null) return
        val correctSentence = current.currentSentence ?: return

        val isCorrect = answer.id == correctSentence.id

        viewModelScope.launch {
            if (!isCorrect) {
                repository.markAsUnlearned(correctSentence.id)
                repository.onWrongAnswer(correctSentence)
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
