package com.example.data.model

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val classLevel: Int? = null,
    val chapterId: String? = null,
    val era: String = "Ancient", // Ancient, Medieval, Modern, World
    val topic: String = "General"
) {
    val correctAnswerText: String
        get() = options.getOrElse(correctOptionIndex) { "" }
}

enum class HistoryEra(val displayNameHindi: String, val displayNameEnglish: String) {
    ANCIENT("प्राचीन भारत", "Ancient India"),
    MEDIEVAL("मध्यकालीन भारत", "Medieval India"),
    MODERN("आधुनिक भारत एवं स्वतंत्रता संग्राम", "Modern India & Freedom Struggle"),
    WORLD("विश्व इतिहास", "World History")
}

data class Chapter(
    val id: String,
    val classLevel: Int,
    val chapterNumber: Int,
    val titleHindi: String,
    val titleEnglish: String,
    val era: HistoryEra,
    val overview: String,
    val keyPoints: List<String>,
    val timeline: List<TimelineEvent>,
    val fullNotes: String,
    val questions: List<Question>
)

data class TimelineEvent(
    val period: String,
    val eventHindi: String,
    val eventEnglish: String = ""
)

data class ClassHistoryInfo(
    val classLevel: Int,
    val bookTitleHindi: String,
    val bookTitleEnglish: String,
    val description: String,
    val totalChapters: Int,
    val badgeIconName: String = "ic_history_scroll"
)
