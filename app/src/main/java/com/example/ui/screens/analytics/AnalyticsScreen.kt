package com.example.ui.screens.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.analytics.collectAsState()
    val recentQuizzes by viewModel.recentQuizzes.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAnalytics()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "प्रदर्शन विश्लेषण एवं प्रगति ट्रैकर",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "आपकी तैयारी का संपूर्ण सांख्यिकी रिकॉर्ड",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("reset_progress_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Progress")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // 1. Overall Accuracy Gauge Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(76.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${analytics.accuracyPercentage}%",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "सटीकता",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        Column {
                            Text(
                                text = "कुल हल किए प्रश्न: ${analytics.totalAttempted}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "सही: ${analytics.totalCorrect} प्रश्न",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "गलत: ${analytics.totalWrong} प्रश्न",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 2. 500 Questions Bank Progress
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.FolderSpecial,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "संपूर्ण इतिहास 500 प्रश्न बैंक",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${analytics.sampurna500Attempted} / 500 हल",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val frac = (analytics.sampurna500Attempted.toFloat() / 500f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { frac },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = AntiqueGold,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "शेष अनसुलझे प्रश्न: ${analytics.sampurna500Remaining} प्रश्न (Zero Repeat Mode में अभ्यास करें)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3. Era-wise Performance
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "कालखंड अनुसार सटीकता (Era-wise Mastery):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val eras = listOf(
                            Triple("Ancient", "प्राचीन भारत (Ancient India)", Color(0xFF1B365D)),
                            Triple("Medieval", "मध्यकालीन भारत (Medieval India)", Color(0xFFC59B27)),
                            Triple("Modern", "आधुनिक भारत (Modern India)", Color(0xFFBC472A)),
                            Triple("World", "विश्व इतिहास (World History)", Color(0xFF2E7D32))
                        )

                        eras.forEach { (key, label, color) ->
                            val acc = analytics.eraAccuracyMap[key] ?: 0
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                    Text("$acc%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { acc / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = color,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 4. Class-wise Progress
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "कक्षा 5 से 12 अध्ययन प्रगति:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        (5..12).forEach { lvl ->
                            val pct = analytics.classProgressMap[lvl] ?: 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "कक्षा $lvl",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(60.dp)
                                )
                                LinearProgressIndicator(
                                    progress = { pct / 100f },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "$pct%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 5. Recent Quizzes Log
            item {
                Text(
                    text = "हाल ही में दिए गए टेस्ट (Recent Quiz History):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (recentQuizzes.isEmpty()) {
                item {
                    Text(
                        text = "अभी तक कोई टेस्ट नहीं दिया गया है। होम स्क्रीन या 500 फोल्डर से 80 प्रश्नों का टेस्ट शुरू करें।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(recentQuizzes) { quiz ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = quiz.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(quiz.timestamp))
                                Text(
                                    text = "$dateStr | कुल: ${quiz.totalQuestions} प्रश्न",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (quiz.scorePercentage >= 60) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            ) {
                                Text(
                                    text = "${quiz.correctCount}/${quiz.totalQuestions} (${quiz.scorePercentage}%)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (quiz.scorePercentage >= 60) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("प्रगति रीसेट करें?") },
            text = { Text("क्या आप अपना संपूर्ण प्रश्न हल रिकॉर्ड और क्विज हिस्ट्री रीसेट करना चाहते हैं? इससे सभी 500 प्रश्न दोबारा अप्रत्यक्ष हो जाएंगे।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("हां, रीसेट करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}
