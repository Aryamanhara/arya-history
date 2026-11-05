package com.example.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AntiqueGold
import com.example.ui.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val quizState by viewModel.quizState.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    var showSubmitDialog by remember { mutableStateOf(false) }
    var showQuestionPalette by remember { mutableStateOf(false) }

    val currentQIndex = quizState.currentQuestionIndex
    val questions = quizState.questions
    val currentQuestion = questions.getOrNull(currentQIndex)

    if (currentQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val selectedOption = quizState.userAnswers[currentQuestion.id]
    val isBookmarked = currentQuestion.id in bookmarkedIds

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "प्रश्न ${currentQIndex + 1} / ${questions.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = quizState.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Bookmark toggle
                    IconButton(
                        onClick = { viewModel.toggleBookmark(currentQuestion.id) },
                        modifier = Modifier.testTag("quiz_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) AntiqueGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Question Palette button
                    IconButton(
                        onClick = { showQuestionPalette = true },
                        modifier = Modifier.testTag("quiz_grid_button")
                    ) {
                        Icon(Icons.Default.GridView, contentDescription = "All Questions")
                    }

                    // Submit button
                    FilledTonalButton(
                        onClick = { showSubmitDialog = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("quiz_submit_button")
                    ) {
                        Text("सबमिट")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.previousQuestion() },
                        enabled = currentQIndex > 0,
                        modifier = Modifier.testTag("quiz_prev_button")
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                        Text("पिछला")
                    }

                    Text(
                        text = "${quizState.userAnswers.size} / ${questions.size} उत्तरित",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Button(
                        onClick = {
                            if (currentQIndex < questions.size - 1) {
                                viewModel.nextQuestion()
                            } else {
                                showSubmitDialog = true
                            }
                        },
                        modifier = Modifier.testTag("quiz_next_button")
                    ) {
                        Text(if (currentQIndex < questions.size - 1) "अगला" else "पूर्ण करें")
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Linear Progress Bar
            item {
                val progressFraction = ((currentQIndex + 1).toFloat() / questions.size.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Question Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "प्रश्न ${currentQIndex + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            SuggestionChip(
                                onClick = {},
                                label = { Text(currentQuestion.era, style = MaterialTheme.typography.labelSmall) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentQuestion.text,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // 4 Options
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentQuestion.options.forEachIndexed { index, optionText ->
                        val isSelected = selectedOption == index
                        val isAnswered = selectedOption != null
                        val isCorrectOption = index == currentQuestion.correctOptionIndex

                        val (containerColor, borderColor, contentColor) = when {
                            !isAnswered -> Triple(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.outlineVariant,
                                MaterialTheme.colorScheme.onSurface
                            )
                            isCorrectOption -> Triple(
                                Color(0xFFE8F5E9),
                                Color(0xFF2E7D32),
                                Color(0xFF1B5E20)
                            )
                            isSelected && !isCorrectOption -> Triple(
                                Color(0xFFFFEBEE),
                                Color(0xFFC62828),
                                Color(0xFFB71C1C)
                            )
                            else -> Triple(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("option_${index}_btn")
                                .clickable {
                                    if (!isAnswered) {
                                        viewModel.selectAnswer(currentQuestion.id, index)
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = containerColor),
                            border = BorderStroke(1.5.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = if (isAnswered && isCorrectOption) Color(0xFF2E7D32)
                                    else if (isSelected) Color(0xFFC62828)
                                    else MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${'A' + index}",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAnswered && (isCorrectOption || isSelected)) Color.White
                                            else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected || (isAnswered && isCorrectOption)) FontWeight.Bold else FontWeight.Normal,
                                    color = contentColor,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isAnswered && isCorrectOption) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else if (isSelected && !isCorrectOption) {
                                    Icon(
                                        Icons.Default.Cancel,
                                        contentDescription = "Incorrect",
                                        tint = Color(0xFFC62828),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Detailed Explanation Box (Shown immediately on answering)
            if (selectedOption != null) {
                item {
                    val isUserCorrect = selectedOption == currentQuestion.correctOptionIndex
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUserCorrect) Color(0xFFF1F8E9) else Color(0xFFFFF8E1)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isUserCorrect) Color(0xFF81C784) else Color(0xFFFFD54F)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isUserCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isUserCorrect) Color(0xFF2E7D32) else Color(0xFFF57F17),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isUserCorrect) "बधाई! आपका उत्तर सही है।" else "सही उत्तर: ${currentQuestion.correctAnswerText}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUserCorrect) Color(0xFF1B5E20) else Color(0xFFE65100)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "विस्तृत व्याख्या (Detailed Explanation):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = currentQuestion.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(50.dp)) }
        }
    }

    // Question Palette Bottom Sheet
    if (showQuestionPalette) {
        ModalBottomSheet(onDismissRequest = { showQuestionPalette = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "सभी प्रश्न (${questions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 44.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 340.dp)
                ) {
                    items(questions.size) { idx ->
                        val q = questions[idx]
                        val isAnswered = quizState.userAnswers.containsKey(q.id)
                        val isCurrent = idx == currentQIndex

                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    viewModel.jumpToQuestion(idx)
                                    showQuestionPalette = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isAnswered -> Color(0xFF2E7D32)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent || isAnswered) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        val answeredCount = quizState.userAnswers.size
        val unattemptedCount = questions.size - answeredCount

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("क्विज सबमिट करें?") },
            text = {
                Text("आपने ${questions.size} में से $answeredCount प्रश्नों के उत्तर दिए हैं। ($unattemptedCount प्रश्न शेष हैं)। क्या आप परिणाम देखना चाहते हैं?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitQuiz()
                    },
                    modifier = Modifier.testTag("confirm_submit_quiz_btn")
                ) {
                    Text("हां, सबमिट करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("जारी रखें")
                }
            }
        )
    }
}
