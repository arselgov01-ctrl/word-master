package com.wordmaster.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wordmaster.app.WordMasterApp
import com.wordmaster.app.data.SentenceEntity
import com.wordmaster.app.data.SentenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SentencesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SentenceRepository = (application as WordMasterApp).sentenceRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyUserAdded = MutableStateFlow(false)
    val showOnlyUserAdded: StateFlow<Boolean> = _showOnlyUserAdded.asStateFlow()

    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val learnedCount: StateFlow<Int> = repository.learnedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCorrect: StateFlow<Int> = repository.totalCorrect.map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalWrong: StateFlow<Int> = repository.totalWrong.map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val sentences: StateFlow<List<SentenceEntity>> = combine(
        repository.allSentences,
        _searchQuery,
        _showOnlyUserAdded
    ) { all, query, onlyUser ->
        all.asSequence()
            .filter { !onlyUser || it.isUserAdded }
            .filter {
                query.isBlank() ||
                    it.english.contains(query, ignoreCase = true) ||
                    it.russian.contains(query, ignoreCase = true)
            }
            .toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeSentences()
        }
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun toggleOnlyUserAdded() {
        _showOnlyUserAdded.value = !_showOnlyUserAdded.value
    }

    fun addSentence(english: String, russian: String, category: String) {
        if (english.isBlank() || russian.isBlank()) return
        viewModelScope.launch {
            repository.addUserSentence(english, russian, category)
        }
    }

    fun updateSentence(sentence: SentenceEntity, english: String, russian: String, category: String) {
        if (english.isBlank() || russian.isBlank()) return
        viewModelScope.launch {
            repository.updateUserSentence(
                sentence.copy(
                    english = english.trim(),
                    russian = russian.trim(),
                    category = category.ifBlank { "general" }
                )
            )
        }
    }

    fun deleteSentence(sentence: SentenceEntity) {
        viewModelScope.launch {
            repository.deleteSentence(sentence)
        }
    }

    fun markAsUnlearned(id: Int) {
        viewModelScope.launch {
            repository.markAsUnlearned(id)
        }
    }
}
