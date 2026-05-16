package com.wordmaster.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words ORDER BY english ASC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isLearned = 0 ORDER BY streak ASC, lastAnsweredAt ASC LIMIT :limit")
    suspend fun getWordsForQuiz(limit: Int = 20): List<WordEntity>

    @Query("SELECT * FROM words WHERE isLearned = 0 ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomUnlearnedWords(count: Int): List<WordEntity>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomWords(count: Int): List<WordEntity>

    @Query("SELECT * FROM words WHERE isLearned = 1 ORDER BY english ASC")
    fun getLearnedWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isLearned = 0 ORDER BY english ASC")
    fun getUnlearnedWords(): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE isLearned = 1")
    fun getLearnedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE correctCount > 0")
    fun getAttemptedCount(): Flow<Int>

    @Query("SELECT SUM(correctCount) FROM words")
    fun getTotalCorrectAnswers(): Flow<Int?>

    @Query("SELECT SUM(wrongCount) FROM words")
    fun getTotalWrongAnswers(): Flow<Int?>

    @Update
    suspend fun updateWord(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWords(words: List<WordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: Int): WordEntity?

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getCount(): Int

    @Query("UPDATE words SET isLearned = 1 WHERE id = :wordId")
    suspend fun markAsLearned(wordId: Int)

    @Query("UPDATE words SET isLearned = 0 WHERE id = :wordId")
    suspend fun markAsUnlearned(wordId: Int)

    @Query("UPDATE words SET isLearned = 0, correctCount = 0, wrongCount = 0, streak = 0 WHERE isLearned = 1")
    suspend fun resetAllLearned()

    @Query(
        """
        SELECT * FROM words WHERE isLearned = 0 
        ORDER BY 
            CASE WHEN correctCount = 0 AND wrongCount = 0 THEN 0 ELSE 1 END,
            streak ASC, 
            wrongCount DESC,
            RANDOM() 
        LIMIT 1
    """
    )
    suspend fun getNextQuizWord(): WordEntity?

    @Query("SELECT * FROM words WHERE id != :excludeId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomWordsExcluding(excludeId: Int, count: Int): List<WordEntity>

    @Query("SELECT * FROM words WHERE isLearned = 1 ORDER BY RANDOM()")
    fun getLearnedWordsShuffled(): Flow<List<WordEntity>>

    @Query(
        """
        SELECT * FROM words WHERE isLearned = 1 
        ORDER BY RANDOM() 
        LIMIT 1
    """
    )
    suspend fun getRandomLearnedWord(): WordEntity?

    @Query("SELECT * FROM words WHERE isLearned = 1 AND id != :excludeId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomLearnedWordsExcluding(excludeId: Int, count: Int): List<WordEntity>
}
