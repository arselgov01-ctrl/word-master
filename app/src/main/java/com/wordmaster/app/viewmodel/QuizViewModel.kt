package com.wordmaster.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wordmaster.app.WordMasterApp
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.data.WordRepository
import com.wordmaster.app.settings.AppSettings
import com.wordmaster.app.settings.SettingsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class QuizState(
    val currentWord: WordEntity? = null,
    val answers: List<WordEntity> = emptyList(),
    val selectedAnswer: WordEntity? = null,
    val isCorrect: Boolean? = null,
    val isLoading: Boolean = true,
    val wordsLearned: Int = 0,
    val totalWords: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val sessionCorrect: Int = 0,
    val sessionWrong: Int = 0,
    val showCelebration: Boolean = false,
    val celebrationMessage: String = ""
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as WordMasterApp
    private val repository: WordRepository = app.repository
    private val settingsManager: SettingsManager = app.settingsManager

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    private val answerCountFlow: StateFlow<Int> = settingsManager.settings
        .map { it.answerCount }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings.DEFAULT_ANSWER_COUNT)

    val learnedCount: StateFlow<Int> = repository.learnedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCorrect: StateFlow<Int> = repository.totalCorrect
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalWrong: StateFlow<Int> = repository.totalWrong
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.initializeWords()
            loadNextWord()
        }
    }

    fun loadNextWord() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, selectedAnswer = null, isCorrect = null, showCelebration = false) }

            val word = repository.getNextQuizWord()
            if (word != null) {
                val wrongCount = (answerCountFlow.value - 1).coerceAtLeast(1)
                val wrongAnswers = repository.getWrongAnswers(word, wrongCount)
                val allAnswers = (wrongAnswers + word).shuffled()

                _state.update {
                    it.copy(
                        currentWord = word,
                        answers = allAnswers,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, currentWord = null) }
            }
        }
    }

    fun selectAnswer(answer: WordEntity) {
        val currentState = _state.value
        if (currentState.selectedAnswer != null) return // Уже выбран ответ
        if (currentState.currentWord == null) return

        val isCorrect = answer.id == currentState.currentWord.id

        viewModelScope.launch {
            if (isCorrect) {
                repository.onCorrectAnswer(currentState.currentWord)
            } else {
                repository.onWrongAnswer(currentState.currentWord)
            }

            val newStreak = if (isCorrect) currentState.currentStreak + 1 else 0
            val bestStreak = maxOf(currentState.bestStreak, newStreak)

            // Проверяем достижения
            var showCelebration = false
            var celebrationMessage = ""

            if (isCorrect && (currentState.currentWord.streak + 1) >= 5) {
                showCelebration = true
                celebrationMessage = "🎉 Слово «${currentState.currentWord.english}» выучено!"
            } else if (newStreak > 0 && newStreak % 10 == 0) {
                showCelebration = true
                celebrationMessage = "🔥 Серия из $newStreak правильных ответов!"
            }

            _state.update {
                it.copy(
                    selectedAnswer = answer,
                    isCorrect = isCorrect,
                    currentStreak = newStreak,
                    bestStreak = bestStreak,
                    sessionCorrect = if (isCorrect) it.sessionCorrect + 1 else it.sessionCorrect,
                    sessionWrong = if (!isCorrect) it.sessionWrong + 1 else it.sessionWrong,
                    showCelebration = showCelebration,
                    celebrationMessage = celebrationMessage
                )
            }
        }
    }

    fun markAsLearned() {
        val currentWord = _state.value.currentWord ?: return
        viewModelScope.launch {
            repository.markAsLearned(currentWord.id)
            loadNextWord()
        }
    }

    fun skipWord() {
        loadNextWord()
    }
}
