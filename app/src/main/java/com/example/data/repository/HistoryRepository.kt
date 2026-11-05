package com.example.data.repository

import android.content.Context
import com.example.data.content.Class5to8Data
import com.example.data.content.Class9to12Data
import com.example.data.content.SampurnaHistoryMasterBank
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.ChapterProgressEntity
import com.example.data.local.QuestionAttemptEntity
import com.example.data.local.QuizHistoryEntity
import com.example.data.model.Chapter
import com.example.data.model.ClassHistoryInfo
import com.example.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

enum class SampurnaFilterType {
    ALL,
    UNATTEMPTED_ONLY,
    MISTAKES_ONLY,
    ANCIENT,
    MEDIEVAL,
    MODERN,
    WORLD
}

data class HistoryAnalytics(
    val totalQuestionsInBank: Int = 500,
    val totalAttempted: Int = 0,
    val totalCorrect: Int = 0,
    val totalWrong: Int = 0,
    val accuracyPercentage: Int = 0,
    val sampurna500Attempted: Int = 0,
    val sampurna500Remaining: Int = 500,
    val classProgressMap: Map<Int, Int> = emptyMap(), // ClassLevel -> completion %
    val eraAccuracyMap: Map<String, Int> = emptyMap() // Era -> accuracy %
)

class HistoryRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.historyDao()

    // Classes List
    fun getClassesList(): List<ClassHistoryInfo> {
        return listOf(
            Class5to8Data.class5Info,
            Class5to8Data.class6Info,
            Class5to8Data.class7Info,
            Class5to8Data.class8Info,
            Class9to12Data.class9Info,
            Class9to12Data.class10Info,
            Class9to12Data.class11Info,
            Class9to12Data.class12Info
        )
    }

    // Chapters for Class
    fun getChaptersForClass(classLevel: Int): List<Chapter> {
        return when (classLevel) {
            in 5..8 -> Class5to8Data.getChaptersForClass(classLevel)
            in 9..12 -> Class9to12Data.getChaptersForClass(classLevel)
            else -> emptyList()
        }
    }

    // Get specific chapter
    fun getChapter(chapterId: String): Chapter? {
        val allClasses = listOf(5, 6, 7, 8, 9, 10, 11, 12)
        for (c in allClasses) {
            val found = getChaptersForClass(c).find { it.id == chapterId }
            if (found != null) return found
        }
        return null
    }

    // All 500 Questions Bank
    fun getAll500Questions(): List<Question> {
        return SampurnaHistoryMasterBank.all500Questions
    }

    // Get filtered list from 500 Bank (e.g. Unattempted, Mistakes, Era)
    suspend fun getFilteredSampurna500Questions(filter: SampurnaFilterType): List<Question> = withContext(Dispatchers.IO) {
        val all = getAll500Questions()
        val attemptedIds = dao.getAttemptedQuestionIds().toSet()
        val wrongIds = dao.getWrongQuestionIds().toSet()

        when (filter) {
            SampurnaFilterType.ALL -> all
            SampurnaFilterType.UNATTEMPTED_ONLY -> all.filter { it.id !in attemptedIds }
            SampurnaFilterType.MISTAKES_ONLY -> all.filter { it.id in wrongIds }
            SampurnaFilterType.ANCIENT -> all.filter { it.era == "Ancient" }
            SampurnaFilterType.MEDIEVAL -> all.filter { it.era == "Medieval" }
            SampurnaFilterType.MODERN -> all.filter { it.era == "Modern" }
            SampurnaFilterType.WORLD -> all.filter { it.era == "World" }
        }
    }

    // Generate 80-question revision quiz (or custom requested count) without duplicates
    suspend fun get80QuestionRevisionQuiz(
        classLevel: Int,
        chapterId: String? = null,
        count: Int = 80,
        unattemptedOnly: Boolean = false
    ): List<Question> = withContext(Dispatchers.IO) {
        val attemptedIds = if (unattemptedOnly) dao.getAttemptedQuestionIds().toSet() else emptySet()

        // 1. Gather chapter specific questions if chapter selected
        val pool = mutableListOf<Question>()
        if (chapterId != null) {
            val chapter = getChapter(chapterId)
            if (chapter != null) {
                pool.addAll(chapter.questions)
            }
        }

        // 2. Add class-level questions
        val classChapters = getChaptersForClass(classLevel)
        for (ch in classChapters) {
            if (ch.id != chapterId) {
                pool.addAll(ch.questions)
            }
        }

        // 3. Add relevant syllabus questions from 500 Bank matching the era
        val relevantEras = when (classLevel) {
            5, 6, 12 -> listOf("Ancient")
            7 -> listOf("Medieval")
            8, 10 -> listOf("Modern")
            9, 11 -> listOf("World")
            else -> listOf("Ancient", "Medieval", "Modern", "World")
        }

        val bankPool = getAll500Questions().filter { it.era in relevantEras }
        pool.addAll(bankPool)

        // Filter out duplicates and optionally attempted questions
        val distinctPool = pool.distinctBy { it.id }.filter { it.id !in attemptedIds }

        // If unattempted filter resulted in fewer than count, fallback to all distinct shuffled
        val finalPool = if (distinctPool.size >= count) {
            distinctPool.shuffled().take(count)
        } else {
            val fallback = pool.distinctBy { it.id }.shuffled()
            fallback.take(count.coerceAtMost(fallback.size))
        }

        finalPool
    }

    // Record question attempt
    suspend fun recordAttempt(
        questionId: String,
        isCorrect: Boolean,
        selectedOptionIndex: Int,
        era: String,
        classLevel: Int?
    ) = withContext(Dispatchers.IO) {
        val existing = dao.getAllQuestionAttempts().find { it.questionId == questionId }
        val attemptCount = (existing?.attemptCount ?: 0) + 1
        dao.recordQuestionAttempt(
            QuestionAttemptEntity(
                questionId = questionId,
                isCorrect = isCorrect,
                selectedOptionIndex = selectedOptionIndex,
                era = era,
                classLevel = classLevel,
                timestamp = System.currentTimeMillis(),
                attemptCount = attemptCount
            )
        )
    }

    // Record entire quiz result
    suspend fun saveQuizResult(
        title: String,
        total: Int,
        correct: Int,
        wrong: Int,
        skipped: Int,
        timeSeconds: Int,
        category: String
    ): Long = withContext(Dispatchers.IO) {
        val percentage = if (total > 0) (correct * 100) / total else 0
        dao.insertQuizHistory(
            QuizHistoryEntity(
                title = title,
                totalQuestions = total,
                correctCount = correct,
                wrongCount = wrong,
                skippedCount = skipped,
                scorePercentage = percentage,
                timeTakenSeconds = timeSeconds,
                category = category
            )
        )
    }

    // Bookmarks
    fun getBookmarkedQuestionIdsFlow(): Flow<List<String>> = dao.getBookmarkedQuestionIdsFlow()

    suspend fun toggleBookmark(questionId: String) = withContext(Dispatchers.IO) {
        val isBookmarked = dao.isQuestionBookmarkedFlow(questionId).first()
        if (isBookmarked) {
            dao.removeBookmark(questionId)
        } else {
            dao.addBookmark(BookmarkEntity(questionId = questionId))
        }
    }

    suspend fun getBookmarkedQuestions(): List<Question> = withContext(Dispatchers.IO) {
        val bookmarkedIds = dao.getBookmarkedQuestionIdsFlow().first().toSet()
        val allQuestions = getAll500Questions().toMutableList()
        // Also add chapter specific questions
        for (c in 5..12) {
            for (ch in getChaptersForClass(c)) {
                allQuestions.addAll(ch.questions)
            }
        }
        allQuestions.distinctBy { it.id }.filter { it.id in bookmarkedIds }
    }

    // Chapter progress
    suspend fun markChapterNotesAsRead(chapterId: String, classLevel: Int) = withContext(Dispatchers.IO) {
        val existing = dao.getChapterProgress(chapterId)
        if (existing == null) {
            dao.upsertChapterProgress(
                ChapterProgressEntity(
                    chapterId = chapterId,
                    classLevel = classLevel,
                    isNotesRead = true,
                    lastAccessedTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            dao.markChapterNotesAsRead(chapterId)
        }
    }

    fun getAllChapterProgressFlow(): Flow<List<ChapterProgressEntity>> = dao.getAllChapterProgressFlow()
    fun getQuizHistoryFlow(): Flow<List<QuizHistoryEntity>> = dao.getAllQuizHistoryFlow()

    // Analytics computation
    suspend fun getAnalytics(): HistoryAnalytics = withContext(Dispatchers.IO) {
        val attempts = dao.getAllQuestionAttempts()
        val all500 = getAll500Questions()
        val s500Ids = all500.map { it.id }.toSet()

        val totalAttempted = attempts.size
        val totalCorrect = attempts.count { it.isCorrect }
        val totalWrong = attempts.count { !it.isCorrect }
        val accuracy = if (totalAttempted > 0) (totalCorrect * 100) / totalAttempted else 0

        val s500Attempted = attempts.count { it.questionId in s500Ids }
        val s500Remaining = (500 - s500Attempted).coerceAtLeast(0)

        // Class Progress: compute based on chapters read & quiz completed
        val progressList = dao.getAllChapterProgressFlow().first()
        val classProgressMap = mutableMapOf<Int, Int>()
        for (lvl in 5..12) {
            val chapters = getChaptersForClass(lvl)
            val totalCh = chapters.size
            if (totalCh > 0) {
                val readCount = progressList.count { p -> p.classLevel == lvl && p.isNotesRead }
                val pct = (readCount * 100) / totalCh
                classProgressMap[lvl] = pct
            } else {
                classProgressMap[lvl] = 0
            }
        }

        // Era accuracy
        val eraAccuracyMap = mutableMapOf<String, Int>()
        listOf("Ancient", "Medieval", "Modern", "World").forEach { era ->
            val eraAttempts = attempts.filter { it.era == era }
            if (eraAttempts.isNotEmpty()) {
                val correct = eraAttempts.count { it.isCorrect }
                eraAccuracyMap[era] = (correct * 100) / eraAttempts.size
            } else {
                eraAccuracyMap[era] = 0
            }
        }

        HistoryAnalytics(
            totalQuestionsInBank = 500,
            totalAttempted = totalAttempted,
            totalCorrect = totalCorrect,
            totalWrong = totalWrong,
            accuracyPercentage = accuracy,
            sampurna500Attempted = s500Attempted,
            sampurna500Remaining = s500Remaining,
            classProgressMap = classProgressMap,
            eraAccuracyMap = eraAccuracyMap
        )
    }

    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        dao.resetAllAttempts()
    }
}
