package com.wordmaster.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wordmaster.app.WordMasterApp
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.data.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WordRepository = (application as WordMasterApp).repository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyUserAdded = MutableStateFlow(false)
    val showOnlyUserAdded: StateFlow<Boolean> = _showOnlyUserAdded.asStateFlow()

    val words: StateFlow<List<WordEntity>> = combine(
        repository.allWords,
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

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun toggleOnlyUserAdded() {
        _showOnlyUserAdded.value = !_showOnlyUserAdded.value
    }

    fun addWord(english: String, russian: String, category: String) {
        if (english.isBlank() || russian.isBlank()) return
        viewModelScope.launch {
            repository.addUserWord(english, russian, category)
        }
    }

    fun updateWord(word: WordEntity, english: String, russian: String, category: String) {
        if (english.isBlank() || russian.isBlank()) return
        viewModelScope.launch {
            repository.updateUserWord(
                word.copy(
                    english = english.trim(),
                    russian = russian.trim(),
                    category = category.ifBlank { "general" }
                )
            )
        }
    }

    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            repository.deleteWord(word)
        }
    }
}
