package com.example.ui.screens.classes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun ChapterNotesScreen(
    chapterId: String,
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val chapter = viewModel.getChapter(chapterId)
    val fontSize by viewModel.readerFontSize.collectAsState()
    val bgMode by viewModel.readerBackgroundMode.collectAsState()

    var showThemeMenu by remember { mutableStateOf(false) }
    var isMarkedRead by remember { mutableStateOf(false) }

    LaunchedEffect(chapterId) {
        if (chapter != null) {
            viewModel.markChapterNotesRead(chapterId, chapter.classLevel)
            isMarkedRead = true
        }
    }

    val (backgroundColor, textColor, cardBg) = when (bgMode) {
        "Sepia" -> Triple(Color(0xFFFBF0D9), Color(0xFF2C2213), Color(0xFFF3E5C8))
        "Dark" -> Triple(Color(0xFF121824), Color(0xFFE2E8F0), Color(0xFF1E293B))
        else -> Triple(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.surface)
    }

    if (chapter == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("अध्याय उपलब्ध नहीं है।")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = chapter.titleHindi,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "कक्षा ${chapter.classLevel} - अध्याय ${chapter.chapterNumber}",
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
                    // Font Size Decrease
                    IconButton(
                        onClick = {
                            if (viewModel.readerFontSize.value > 14f) {
                                viewModel.readerFontSize.value -= 1.5f
                            }
                        },
                        modifier = Modifier.testTag("font_decrease_btn")
                    ) {
                        Text("A-", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }

                    // Font Size Increase
                    IconButton(
                        onClick = {
                            if (viewModel.readerFontSize.value < 24f) {
                                viewModel.readerFontSize.value += 1.5f
                            }
                        },
                        modifier = Modifier.testTag("font_increase_btn")
                    ) {
                        Text("A+", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }

                    // Reading theme switcher
                    Box {
                        IconButton(
                            onClick = { showThemeMenu = true },
                            modifier = Modifier.testTag("reader_theme_btn")
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = "Theme")
                        }
                        DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("डिफ़ॉल्ट थीम (Default)") },
                                onClick = {
                                    viewModel.readerBackgroundMode.value = "Default"
                                    showThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("वार्म सेपिया (Warm Sepia)") },
                                onClick = {
                                    viewModel.readerBackgroundMode.value = "Sepia"
                                    showThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("डार्क मोड (Midnight Dark)") },
                                onClick = {
                                    viewModel.readerBackgroundMode.value = "Dark"
                                    showThemeMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        bottomBar = {
            Surface(
                color = backgroundColor,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.startClassRevisionQuiz(
                                classLevel = chapter.classLevel,
                                chapterId = chapter.id,
                                questionCount = 80
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("chapter_notes_start_80_quiz_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "इस अध्याय का 80 प्रश्न रिवीजन टेस्ट दें",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Chapter Title & Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
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
                                    text = "NCERT कक्षा ${chapter.classLevel} इतिहास",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "अध्ययन किया गया",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = chapter.titleHindi,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        Text(
                            text = chapter.titleEnglish,
                            style = MaterialTheme.typography.titleSmall,
                            color = textColor.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = chapter.overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor.copy(alpha = 0.9f),
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Key Points Section
            if (chapter.keyPoints.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "मुख्य परीक्षा बिंदु (Key Highlights)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            chapter.keyPoints.forEach { point ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = point,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize.sp),
                                        color = textColor,
                                        lineHeight = (fontSize * 1.4f).sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Historical Timeline Table
            if (chapter.timeline.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ऐतिहासिक कालक्रम (Timeline)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            chapter.timeline.forEach { event ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = event.period,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = event.eventHindi,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = textColor.copy(alpha = 0.08f)
                                )
                            }
                        }
                    }
                }
            }

            // Full In-depth Notes Text
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "विस्तृत अध्याय अध्ययन नोट्स",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = chapter.fullNotes,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize.sp),
                            color = textColor,
                            lineHeight = (fontSize * 1.55f).sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}
