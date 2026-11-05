package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    // Question Attempts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordQuestionAttempt(attempt: QuestionAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordQuestionAttempts(attempts: List<QuestionAttemptEntity>)

    @Query("SELECT * FROM question_attempts")
    fun getAllQuestionAttemptsFlow(): Flow<List<QuestionAttemptEntity>>

    @Query("SELECT * FROM question_attempts")
    suspend fun getAllQuestionAttempts(): List<QuestionAttemptEntity>

    @Query("SELECT questionId FROM question_attempts")
    suspend fun getAttemptedQuestionIds(): List<String>

    @Query("SELECT questionId FROM question_attempts WHERE isCorrect = 0")
    suspend fun getWrongQuestionIds(): List<String>

    @Query("SELECT COUNT(*) FROM question_attempts")
    fun getTotalAttemptedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM question_attempts WHERE isCorrect = 1")
    fun getTotalCorrectCountFlow(): Flow<Int>

    // Quiz History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizHistory(quiz: QuizHistoryEntity): Long

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getAllQuizHistoryFlow(): Flow<List<QuizHistoryEntity>>

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentQuizHistoryFlow(): Flow<List<QuizHistoryEntity>>

    // Bookmarks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE questionId = :questionId")
    suspend fun removeBookmark(questionId: String)

    @Query("SELECT questionId FROM bookmarks")
    fun getBookmarkedQuestionIdsFlow(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE questionId = :questionId)")
    fun isQuestionBookmarkedFlow(questionId: String): Flow<Boolean>

    // Chapter Progress
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChapterProgress(progress: ChapterProgressEntity)

    @Query("SELECT * FROM chapter_progress")
    fun getAllChapterProgressFlow(): Flow<List<ChapterProgressEntity>>

    @Query("SELECT * FROM chapter_progress WHERE chapterId = :chapterId")
    suspend fun getChapterProgress(chapterId: String): ChapterProgressEntity?

    @Query("UPDATE chapter_progress SET isNotesRead = 1, lastAccessedTimestamp = :timestamp WHERE chapterId = :chapterId")
    suspend fun markChapterNotesAsRead(chapterId: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM question_attempts")
    suspend fun resetAllAttempts()
}
