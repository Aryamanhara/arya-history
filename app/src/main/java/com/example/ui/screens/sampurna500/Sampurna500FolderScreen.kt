package com.example.ui.screens.sampurna500

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.repository.SampurnaFilterType
import com.example.ui.theme.AntiqueGold
import com.example.ui.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sampurna500FolderScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.analytics.collectAsState()
    val currentFilter by viewModel.sampurnaFilter.collectAsState()
    val questions by viewModel.sampurnaQuestions.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var expandedQuestionId by remember { mutableStateOf<String?>(null) }
    var selectedQuizSize by remember { mutableIntStateOf(80) }

    val filteredQuestions = remember(questions, searchQuery) {
        if (searchQuery.isBlank()) {
            questions
        } else {
            questions.filter {
                it.text.contains(searchQuery, ignoreCase = true) ||
                        it.topic.contains(searchQuery, ignoreCase = true) ||
                        it.explanation.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "संपूर्ण इतिहास 500 प्रश्न बैंक",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "500 प्रश्न क्विज मोड (Zero Repeat) एवं एक्सप्लोरर",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.startSampurna500Quiz(
                                count = selectedQuizSize,
                                filter = currentFilter
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("sampurna_folder_start_quiz_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$selectedQuizSize प्रश्न क्विज मोड शुरू करें",
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
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // 1. 500 Question Folder Progress Tracker Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "500 प्रश्न बैंक प्रगति ट्रैकर",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "${analytics.sampurna500Attempted} / 500 हल",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val progressFraction = (analytics.sampurna500Attempted.toFloat() / 500f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = AntiqueGold,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "शेष नए प्रश्न: ${analytics.sampurna500Remaining}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "सटीकता: ${analytics.accuracyPercentage}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (analytics.accuracyPercentage >= 70) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // 2. Quiz Size Selector (80 questions as primary default per user request)
            item {
                Column {
                    Text(
                        text = "क्विज प्रश्न संख्या चुनें:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(25, 50, 80, 100).forEach { size ->
                            val isSelected = selectedQuizSize == size
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedQuizSize = size },
                                label = {
                                    Text(
                                        text = "$size प्रश्न${if (size == 80) " (मानक)" else ""}",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.testTag("quiz_size_chip_$size")
                            )
                        }
                    }
                }
            }

            // 3. Filter Chips: All, Unattempted (No-Repeat), Mistakes, Eras
            item {
                Column {
                    Text(
                        text = "प्रश्न श्रेणी एवं दोहराव रहित फिल्टर (No Repeat):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val filters = listOf(
                            SampurnaFilterType.ALL to "सभी 500 प्रश्न",
                            SampurnaFilterType.UNATTEMPTED_ONLY to "अप्रत्यक्ष (Zero Repeat)",
                            SampurnaFilterType.MISTAKES_ONLY to "सुधार हेतु गलत प्रश्न",
                            SampurnaFilterType.ANCIENT to "प्राचीन भारत",
                            SampurnaFilterType.MEDIEVAL to "मध्यकालीन भारत",
                            SampurnaFilterType.MODERN to "आधुनिक भारत",
                            SampurnaFilterType.WORLD to "विश्व इतिहास"
                        )

                        items(filters) { (filterType, label) ->
                            val isSelected = currentFilter == filterType
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSampurnaFilter(filterType) },
                                label = { Text(label) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                modifier = Modifier.testTag("filter_chip_${filterType.name}")
                            )
                        }
                    }
                }
            }

            // 4. Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("500 प्रश्नों में से कोई भी विषय खोजें...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sampurna_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // 5. Questions Counter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "उपलब्ध प्रश्न: ${filteredQuestions.size}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "क्लिक कर व्याख्या देखें",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 6. Question List with Expandable Options & Explanation
            items(filteredQuestions) { question ->
                val isExpanded = expandedQuestionId == question.id
                val isBookmarked = question.id in bookmarkedIds

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("question_item_${question.id}")
                        .clickable {
                            expandedQuestionId = if (isExpanded) null else question.id
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = question.id.replace("s500_", "#"),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(question.era, style = MaterialTheme.typography.labelSmall) }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
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
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = question.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(8.dp))

                                question.options.forEachIndexed { idx, opt ->
                                    val isCorrect = idx == question.correctOptionIndex
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCorrect) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${('A' + idx)}. ",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = opt,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (isCorrect) {
                                                Spacer(modifier = Modifier.weight(1f))
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "विस्तृत व्याख्या (Explanation):",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
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
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}
