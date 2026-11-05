package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.QuizHistoryEntity
import com.example.data.model.Chapter
import com.example.data.model.ClassHistoryInfo
import com.example.data.model.Question
import com.example.data.repository.HistoryAnalytics
import com.example.data.repository.HistoryRepository
import com.example.data.repository.SampurnaFilterType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Home : AppScreen()
    object Classes : AppScreen()
    data class ChapterList(val classLevel: Int) : AppScreen()
    data class ChapterNotes(val chapterId: String) : AppScreen()
    object Sampurna500Folder : AppScreen()
    data class Quiz(
        val title: String,
        val totalQuestions: Int,
        val category: String,
        val chapterId: String? = null
    ) : AppScreen()
    object QuizResult : AppScreen()
    object Analytics : AppScreen()
    object Bookmarks : AppScreen()
}

data class QuizState(
    val title: String = "",
    val category: String = "",
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<String, Int> = emptyMap(), // QuestionId -> OptionIndex
    val isSubmitted: Boolean = false,
    val timeRemainingSeconds: Int = 0,
    val hasTimer: Boolean = true,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val skippedCount: Int = 0,
    val scorePercentage: Int = 0,
    val showInstantExplanation: Boolean = true
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistoryRepository(application)

    // Current Screen
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Navigation Stack for seamless back-handling
    private val screenStack = mutableListOf<AppScreen>()

    // Data lists
    val classesList: List<ClassHistoryInfo> = repository.getClassesList()

    private val _analytics = MutableStateFlow(HistoryAnalytics())
    val analytics: StateFlow<HistoryAnalytics> = _analytics.asStateFlow()

    private val _recentQuizzes = MutableStateFlow<List<QuizHistoryEntity>>(emptyList())
    val recentQuizzes: StateFlow<List<QuizHistoryEntity>> = _recentQuizzes.asStateFlow()

    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()

    // Sampurna 500 Folder State
    private val _sampurnaFilter = MutableStateFlow(SampurnaFilterType.ALL)
    val sampurnaFilter: StateFlow<SampurnaFilterType> = _sampurnaFilter.asStateFlow()

    private val _sampurnaQuestions = MutableStateFlow<List<Question>>(emptyList())
    val sampurnaQuestions: StateFlow<List<Question>> = _sampurnaQuestions.asStateFlow()

    // Active Quiz State
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    // Reader Mode Settings
    val readerFontSize = MutableStateFlow(17f)
    val readerBackgroundMode = MutableStateFlow("Sepia") // "Default", "Sepia", "Dark"

    init {
        loadAnalytics()
        loadSampurnaQuestions(SampurnaFilterType.ALL)

        viewModelScope.launch {
            repository.getBookmarkedQuestionIdsFlow().collectLatest { ids ->
                _bookmarkedIds.value = ids.toSet()
            }
        }

        viewModelScope.launch {
            repository.getQuizHistoryFlow().collectLatest { list ->
                _recentQuizzes.value = list
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        screenStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            return true
        }
        if (_currentScreen.value !is AppScreen.Home) {
            _currentScreen.value = AppScreen.Home
            return true
        }
        return false
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _analytics.value = repository.getAnalytics()
        }
    }

    fun setSampurnaFilter(filter: SampurnaFilterType) {
        _sampurnaFilter.value = filter
        loadSampurnaQuestions(filter)
    }

    private fun loadSampurnaQuestions(filter: SampurnaFilterType) {
        viewModelScope.launch {
            _sampurnaQuestions.value = repository.getFilteredSampurna500Questions(filter)
        }
    }

    fun getChaptersForClass(classLevel: Int): List<Chapter> {
        return repository.getChaptersForClass(classLevel)
    }

    fun getChapter(chapterId: String): Chapter? {
        return repository.getChapter(chapterId)
    }

    fun markChapterNotesRead(chapterId: String, classLevel: Int) {
        viewModelScope.launch {
            repository.markChapterNotesAsRead(chapterId, classLevel)
            loadAnalytics()
        }
    }

    // Launch Chapter / Class 80-Question Quiz
    fun startClassRevisionQuiz(classLevel: Int, chapterId: String? = null, questionCount: Int = 80, unattemptedOnly: Boolean = false) {
        viewModelScope.launch {
            val questions = repository.get80QuestionRevisionQuiz(
                classLevel = classLevel,
                chapterId = chapterId,
                count = questionCount,
                unattemptedOnly = unattemptedOnly
            )
            val chapter = chapterId?.let { repository.getChapter(it) }
            val title = if (chapter != null) {
                "${chapter.titleHindi} (${questions.size} प्रश्न टेस्ट)"
            } else {
                "कक्षा $classLevel संपूर्ण रिवीजन (${questions.size} प्रश्न टेस्ट)"
            }

            _quizState.value = QuizState(
                title = title,
                category = "Class $classLevel",
                questions = questions,
                currentQuestionIndex = 0,
                userAnswers = emptyMap(),
                isSubmitted = false,
                hasTimer = true,
                timeRemainingSeconds = questions.size * 60, // 1 min per question
                showInstantExplanation = true
            )
            navigateTo(AppScreen.Quiz(title, questions.size, "Class$classLevel", chapterId))
        }
    }

    // Launch Sampurna 500 Quiz
    fun startSampurna500Quiz(count: Int = 80, filter: SampurnaFilterType = SampurnaFilterType.ALL) {
        viewModelScope.launch {
            val allFiltered = repository.getFilteredSampurna500Questions(filter)
            val selected = if (allFiltered.size >= count) allFiltered.shuffled().take(count) else allFiltered.shuffled()

            val filterLabel = when (filter) {
                SampurnaFilterType.ALL -> "संपूर्ण इतिहास 500"
                SampurnaFilterType.UNATTEMPTED_ONLY -> "अप्रत्यक्ष/नए प्रश्न (No Repeat)"
                SampurnaFilterType.MISTAKES_ONLY -> "गलत प्रश्नों का सुधार टेस्ट"
                SampurnaFilterType.ANCIENT -> "प्राचीन भारत स्पेशल"
                SampurnaFilterType.MEDIEVAL -> "मध्यकालीन भारत स्पेशल"
                SampurnaFilterType.MODERN -> "आधुनिक भारत स्पेशल"
                SampurnaFilterType.WORLD -> "विश्व इतिहास स्पेशल"
            }

            val title = "$filterLabel (${selected.size} प्रश्न महाक्विज)"

            _quizState.value = QuizState(
                title = title,
                category = "Sampurna500",
                questions = selected,
                currentQuestionIndex = 0,
                userAnswers = emptyMap(),
                isSubmitted = false,
                hasTimer = true,
                timeRemainingSeconds = selected.size * 60,
                showInstantExplanation = true
            )
            navigateTo(AppScreen.Quiz(title, selected.size, "Sampurna500"))
        }
    }

    // Start Bookmarked Quiz
    fun startBookmarkedQuiz() {
        viewModelScope.launch {
            val questions = repository.getBookmarkedQuestions()
            if (questions.isNotEmpty()) {
                _quizState.value = QuizState(
                    title = "बुकमार्क प्रश्न रिवीजन (${questions.size} प्रश्न)",
                    category = "Bookmarks",
                    questions = questions,
                    currentQuestionIndex = 0,
                    userAnswers = emptyMap(),
                    isSubmitted = false,
                    hasTimer = false,
                    showInstantExplanation = true
                )
                navigateTo(AppScreen.Quiz("Bookmarks", questions.size, "Bookmarks"))
            }
        }
    }

    // In-Quiz Actions
    fun selectAnswer(questionId: String, optionIndex: Int) {
        val current = _quizState.value
        val updatedAnswers = current.userAnswers.toMutableMap()
        updatedAnswers[questionId] = optionIndex
        _quizState.value = current.copy(userAnswers = updatedAnswers)

        // Also record attempt in DB immediately
        val question = current.questions.find { it.id == questionId }
        if (question != null) {
            val isCorrect = optionIndex == question.correctOptionIndex
            viewModelScope.launch {
                repository.recordAttempt(
                    questionId = questionId,
                    isCorrect = isCorrect,
                    selectedOptionIndex = optionIndex,
                    era = question.era,
                    classLevel = question.classLevel
                )
            }
        }
    }

    fun nextQuestion() {
        val current = _quizState.value
        if (current.currentQuestionIndex < current.questions.size - 1) {
            _quizState.value = current.copy(currentQuestionIndex = current.currentQuestionIndex + 1)
        }
    }

    fun previousQuestion() {
        val current = _quizState.value
        if (current.currentQuestionIndex > 0) {
            _quizState.value = current.copy(currentQuestionIndex = current.currentQuestionIndex - 1)
        }
    }

    fun jumpToQuestion(index: Int) {
        val current = _quizState.value
        if (index in current.questions.indices) {
            _quizState.value = current.copy(currentQuestionIndex = index)
        }
    }

    fun toggleInstantExplanation() {
        val current = _quizState.value
        _quizState.value = current.copy(showInstantExplanation = !current.showInstantExplanation)
    }

    fun toggleBookmark(questionId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(questionId)
        }
    }

    fun submitQuiz() {
        val current = _quizState.value
        var correct = 0
        var wrong = 0
        var skipped = 0

        current.questions.forEach { q ->
            val userAns = current.userAnswers[q.id]
            when {
                userAns == null -> skipped++
                userAns == q.correctOptionIndex -> correct++
                else -> wrong++
            }
        }

        val total = current.questions.size
        val percentage = if (total > 0) (correct * 100) / total else 0

        _quizState.value = current.copy(
            isSubmitted = true,
            correctCount = correct,
            wrongCount = wrong,
            skippedCount = skipped,
            scorePercentage = percentage
        )

        viewModelScope.launch {
            repository.saveQuizResult(
                title = current.title,
                total = total,
                correct = correct,
                wrong = wrong,
                skipped = skipped,
                timeSeconds = 0,
                category = current.category
            )
            loadAnalytics()
        }

        navigateTo(AppScreen.QuizResult)
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            loadAnalytics()
            loadSampurnaQuestions(_sampurnaFilter.value)
        }
    }
}
