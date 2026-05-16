package com.wordmaster.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentences")
data class SentenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val english: String,
    val russian: String,
    val category: String = "general",
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val isLearned: Boolean = false,
    val lastAnsweredAt: Long = 0,
    val streak: Int = 0,
    val isUserAdded: Boolean = false
)
