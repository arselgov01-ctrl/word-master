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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SentenceQuizState(
    val currentSentence: SentenceEntity? = null,
    val answers: List<SentenceEntity> = emptyList(),
    val selectedAnswer: SentenceEntity? = null,
    val isCorrect: Boolean? = null,
    val isLoading: Boolean = true,
    val sessionCorrect: Int = 0,
    val sessionWrong: Int = 0
)

class SentenceQuizViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as WordMasterApp
    private val repository: SentenceRepository = app.sentenceRepository
    private val settingsManager: SettingsManager = app.settingsManager

    private val _state = MutableStateFlow(SentenceQuizState())
    val state: StateFlow<SentenceQuizState> = _state.asStateFlow()

    private val answerCountFlow: StateFlow<Int> = settingsManager.settings
        .map { it.answerCount }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings.DEFAULT_ANSWER_COUNT)

    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val learnedCount: StateFlow<Int> = repository.learnedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.initializeSentences()
            loadNext()
        }
    }

    fun loadNext() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, selectedAnswer = null, isCorrect = null) }
            val sentence = repository.getNextQuizSentence()
            if (sentence != null) {
                val wrongCount = (answerCountFlow.value - 1).coerceAtLeast(1)
                val wrong = repository.getWrongAnswers(sentence, wrongCount)
                val all = (wrong + sentence).shuffled()
                _state.update {
                    it.copy(
                        currentSentence = sentence,
                        answers = all,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, currentSentence = null) }
            }
        }
    }

    fun selectAnswer(answer: SentenceEntity) {
        val current = _state.value
        if (current.selectedAnswer != null) return
        val correctSentence = current.currentSentence ?: return
        val isCorrect = answer.id == correctSentence.id

        viewModelScope.launch {
            if (isCorrect) {
                repository.onCorrectAnswer(correctSentence)
            } else {
                repository.onWrongAnswer(correctSentence)
            }
            _state.update {
                it.copy(
                    selectedAnswer = answer,
                    isCorrect = isCorrect,
                    sessionCorrect = if (isCorrect) it.sessionCorrect + 1 else it.sessionCorrect,
                    sessionWrong = if (!isCorrect) it.sessionWrong + 1 else it.sessionWrong
                )
            }
        }
    }

    fun markAsLearned() {
        val sentence = _state.value.currentSentence ?: return
        viewModelScope.launch {
            repository.markAsLearned(sentence.id)
            loadNext()
        }
    }

    fun skip() {
        loadNext()
    }
}
