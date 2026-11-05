package com.example.ui.screens.classes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.HistoryEra
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterListScreen(
    classLevel: Int,
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val chapters = viewModel.getChaptersForClass(classLevel)
    val classInfo = viewModel.classesList.find { it.classLevel == classLevel }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "कक्षा $classLevel: अध्याय सूची",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = classInfo?.bookTitleHindi ?: "इतिहास पाठ्यक्रम",
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
                    FilledTonalButton(
                        onClick = {
                            viewModel.startClassRevisionQuiz(classLevel = classLevel, questionCount = 80)
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("class_mega_quiz_btn")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("80Q क्लास टेस्ट")
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${classInfo?.bookTitleHindi} (${classInfo?.bookTitleEnglish})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "प्रत्येक अध्याय के विस्तृत नोट्स और 80 प्रश्नों का रिवीजन टेस्ट उपलब्ध है।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            items(chapters) { chapter ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chapter_card_${chapter.id}")
                        .clickable {
                            viewModel.navigateTo(AppScreen.ChapterNotes(chapter.id))
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "अध्याय ${chapter.chapterNumber}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            val (eraText, eraColor) = when (chapter.era) {
                                HistoryEra.ANCIENT -> "प्राचीन" to MaterialTheme.colorScheme.primary
                                HistoryEra.MEDIEVAL -> "मध्यकालीन" to MaterialTheme.colorScheme.secondary
                                HistoryEra.MODERN -> "आधुनिक" to MaterialTheme.colorScheme.tertiary
                                HistoryEra.WORLD -> "विश्व" to MaterialTheme.colorScheme.primary
                            }

                            SuggestionChip(
                                onClick = {},
                                label = { Text(eraText, style = MaterialTheme.typography.labelSmall) }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = chapter.titleHindi,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = chapter.titleEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = chapter.overview,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.navigateTo(AppScreen.ChapterNotes(chapter.id))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("read_notes_btn_${chapter.id}")
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("नोट्स पढ़ें")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.startClassRevisionQuiz(
                                        classLevel = classLevel,
                                        chapterId = chapter.id,
                                        questionCount = 80
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chapter_80_quiz_btn_${chapter.id}")
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("80Q क्विज")
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
