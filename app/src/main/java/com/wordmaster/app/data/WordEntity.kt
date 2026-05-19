package com.wordmaster.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val english: String,
    val russian: String,
    val category: String = "general",
    val correctCount: Int = 0,        // сколько раз ответил правильно
    val wrongCount: Int = 0,          // сколько раз ответил неправильно
    val isLearned: Boolean = false,   // отмечено как выученное
    val lastAnsweredAt: Long = 0,     // когда последний раз отвечал
    val streak: Int = 0,              // серия правильных ответов подряд
    val difficulty: Int = 0,          // 0-easy, 1-medium, 2-hard (авто)
    val isUserAdded: Boolean = false  // добавлено пользователем (true) или предустановлено (false)
)
