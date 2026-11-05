package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_attempts")
data class QuestionAttemptEntity(
    @PrimaryKey val questionId: String,
    val isCorrect: Boolean,
    val selectedOptionIndex: Int,
    val era: String,
    val classLevel: Int?,
    val timestamp: Long = System.currentTimeMillis(),
    val attemptCount: Int = 1
)

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val scorePercentage: Int,
    val timeTakenSeconds: Int,
    val category: String, // "Class5", "Class10", "Sampurna500", etc.
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val questionId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null
)

@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey val chapterId: String,
    val classLevel: Int,
    val isNotesRead: Boolean = false,
    val quizHighScore: Int = 0,
    val quizCompleted: Boolean = false,
    val lastAccessedTimestamp: Long = System.currentTimeMillis()
)
