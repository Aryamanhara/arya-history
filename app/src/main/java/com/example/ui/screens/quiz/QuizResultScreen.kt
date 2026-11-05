package com.example.ui.screens.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.Question
import com.example.ui.theme.AntiqueGold
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.HistoryViewModel

enum class ResultFilter {
    ALL,
    INCORRECT_ONLY,
    CORRECT_ONLY,
    SKIPPED_ONLY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val quizState by viewModel.quizState.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    var activeFilter by remember { mutableStateOf(ResultFilter.ALL) }

    val filteredQuestions = remember(quizState, activeFilter) {
        when (activeFilter) {
            ResultFilter.ALL -> quizState.questions
            ResultFilter.INCORRECT_ONLY -> quizState.questions.filter {
                val ans = quizState.userAnswers[it.id]
                ans != null && ans != it.correctOptionIndex
            }
            ResultFilter.CORRECT_ONLY -> quizState.questions.filter {
                val ans = quizState.userAnswers[it.id]
                ans == it.correctOptionIndex
            }
            ResultFilter.SKIPPED_ONLY -> quizState.questions.filter {
                !quizState.userAnswers.containsKey(it.id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "क्विज परिणाम एवं समीक्षा",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Scorecard Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = AntiqueGold
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${quizState.correctCount} / ${quizState.questions.size}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "स्कोर: ${quizState.scorePercentage}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (quizState.scorePercentage >= 60) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = quizState.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${quizState.correctCount}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text("सही (Correct)", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${quizState.wrongCount}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                                Text("गलत (Wrong)", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${quizState.skippedCount}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text("छोड़े गए", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.Analytics) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_go_analytics_btn")
                    ) {
                        Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("प्रगति ट्रैकर")
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_go_home_btn")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("होम पेज")
                    }
                }
            }

            // Review Filter Chips
            item {
                Column {
                    Text(
                        text = "प्रश्नों की विस्तृत व्याख्या एवं समीक्षा:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ResultFilter.values()) { filter ->
                            val label = when (filter) {
                                ResultFilter.ALL -> "सभी प्रश्न (${quizState.questions.size})"
                                ResultFilter.INCORRECT_ONLY -> "केवल गलत (${quizState.wrongCount})"
                                ResultFilter.CORRECT_ONLY -> "केवल सही (${quizState.correctCount})"
                                ResultFilter.SKIPPED_ONLY -> "छोड़े गए (${quizState.skippedCount})"
                            }
                            val isSelected = activeFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { activeFilter = filter },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            // List of questions with answers and detailed explanations
            items(filteredQuestions) { question ->
                val userAnsIndex = quizState.userAnswers[question.id]
                val isAnswered = userAnsIndex != null
                val isCorrect = userAnsIndex == question.correctOptionIndex
                val isBookmarked = question.id in bookmarkedIds

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        if (isCorrect) Color(0xFF81C784) else if (isAnswered) Color(0xFFEF9A9A) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCorrect) Color(0xFFE8F5E9) else if (isAnswered) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.CheckCircle else if (isAnswered) Icons.Default.Cancel else Icons.Default.HelpOutline,
                                        contentDescription = null,
                                        tint = if (isCorrect) Color(0xFF2E7D32) else if (isAnswered) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isCorrect) "सही उत्तर" else if (isAnswered) "गलत उत्तर" else "अनुत्तरित",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrect) Color(0xFF2E7D32) else if (isAnswered) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.toggleBookmark(question.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) AntiqueGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = question.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Options Preview
                        question.options.forEachIndexed { idx, opt ->
                            val isCorrectOpt = idx == question.correctOptionIndex
                            val isUserSelected = idx == userAnsIndex

                            val (bg, txtColor) = when {
                                isCorrectOpt -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
                                isUserSelected && !isCorrectOpt -> Color(0xFFFFEBEE) to Color(0xFFB71C1C)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) to MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                color = bg
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${('A' + idx)}. $opt",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = txtColor,
                                        fontWeight = if (isCorrectOpt || isUserSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isCorrectOpt) {
                                        Text("✓ सही", color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    } else if (isUserSelected) {
                                        Text("✗ आपका चयन", color = Color(0xFFC62828), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Detailed Explanation
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "विस्तृत व्याख्या (Explanation):",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = question.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
