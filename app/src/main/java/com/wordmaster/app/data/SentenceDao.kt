package com.wordmaster.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SentenceDao {

    @Query("SELECT * FROM sentences ORDER BY english ASC")
    fun getAllSentences(): Flow<List<SentenceEntity>>

    @Query("SELECT * FROM sentences WHERE isLearned = 1 ORDER BY english ASC")
    fun getLearnedSentences(): Flow<List<SentenceEntity>>

    @Query("SELECT * FROM sentences WHERE isLearned = 0 ORDER BY english ASC")
    fun getUnlearnedSentences(): Flow<List<SentenceEntity>>

    @Query("SELECT COUNT(*) FROM sentences")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM sentences WHERE isLearned = 1")
    fun getLearnedCount(): Flow<Int>

    @Query("SELECT SUM(correctCount) FROM sentences")
    fun getTotalCorrectAnswers(): Flow<Int?>

    @Query("SELECT SUM(wrongCount) FROM sentences")
    fun getTotalWrongAnswers(): Flow<Int?>

    @Update
    suspend fun updateSentence(sentence: SentenceEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSentences(sentences: List<SentenceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentence(sentence: SentenceEntity): Long

    @Delete
    suspend fun deleteSentence(sentence: SentenceEntity)

    @Query("SELECT * FROM sentences WHERE id = :id LIMIT 1")
    suspend fun getSentenceById(id: Int): SentenceEntity?

    @Query("SELECT COUNT(*) FROM sentences")
    suspend fun getCount(): Int

    @Query("UPDATE sentences SET isLearned = 1 WHERE id = :sentenceId")
    suspend fun markAsLearned(sentenceId: Int)

    @Query("UPDATE sentences SET isLearned = 0 WHERE id = :sentenceId")
    suspend fun markAsUnlearned(sentenceId: Int)

    @Query("UPDATE sentences SET isLearned = 0, correctCount = 0, wrongCount = 0, streak = 0 WHERE isLearned = 1")
    suspend fun resetAllLearned()

    @Query(
        """
        SELECT * FROM sentences WHERE isLearned = 0
        ORDER BY
            CASE WHEN correctCount = 0 AND wrongCount = 0 THEN 0 ELSE 1 END,
            streak ASC,
            wrongCount DESC,
            RANDOM()
        LIMIT 1
    """
    )
    suspend fun getNextQuizSentence(): SentenceEntity?

    @Query("SELECT * FROM sentences WHERE id != :excludeId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomSentencesExcluding(excludeId: Int, count: Int): List<SentenceEntity>

    @Query("SELECT * FROM sentences WHERE isLearned = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomLearnedSentence(): SentenceEntity?

    @Query("SELECT * FROM sentences WHERE isLearned = 1 AND id != :excludeId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomLearnedSentencesExcluding(excludeId: Int, count: Int): List<SentenceEntity>
}
