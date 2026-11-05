package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.HeritageBlue
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.analytics.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "Arya History",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Arya History",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "इतिहास अध्ययन एवं प्रश्न बैंक",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.Bookmarks) },
                        modifier = Modifier.testTag("home_bookmarks_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (bookmarkedIds.isNotEmpty()) {
                                    Badge { Text("${bookmarkedIds.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Bookmarks")
                        }
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
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Featured Folder Banner: 500 Questions Bank
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sampurna_500_banner_card")
                        .clickable { viewModel.navigateTo(AppScreen.Sampurna500Folder) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        HeritageBlue
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AntiqueGold.copy(alpha = 0.25f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FolderSpecial,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "विशेष फोल्डर",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "500 प्रश्न बैंक",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFFFFDF94),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "संपूर्ण इतिहास 500 महाक्विज बैंक",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "प्राचीन, मध्यकालीन, आधुनिक व विश्व इतिहास से 500 महत्वपूर्ण प्रश्न। कोई प्रश्न रिपीट नहीं होगा! विस्तृत व्याख्या सहित।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.startSampurna500Quiz(count = 80) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("banner_start_80_quiz"),
                                    colors = ButtonDefaults.buttonColors(containerColor = AntiqueGold)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("80 प्रश्न टेस्ट", color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.navigateTo(AppScreen.Sampurna500Folder) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("banner_explore_folder"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("फोल्डर खोलें", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Quick Performance Snapshot
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Attempted Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(AppScreen.Analytics) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Quiz,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("हल किए प्रश्न", style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${analytics.totalAttempted}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "500 में से ${analytics.sampurna500Attempted}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Accuracy Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(AppScreen.Analytics) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("सटीकता (Accuracy)", style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${analytics.accuracyPercentage}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (analytics.accuracyPercentage >= 70) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "सही: ${analytics.totalCorrect} | गलत: ${analytics.totalWrong}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. Class-wise Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "कक्षा 5 से 12 इतिहास (NCERT)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "चैप्टर-वाइज नोट्स एवं 80 प्रश्न रिवीजन टेस्ट",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo(AppScreen.Classes) },
                        modifier = Modifier.testTag("see_all_classes_button")
                    ) {
                        Text("सभी देखें")
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // 4. Class Quick Cards Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val classes = viewModel.classesList
                    val chunked = classes.chunked(2)
                    chunked.forEach { rowClasses ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowClasses.forEach { classInfo ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("class_card_${classInfo.classLevel}")
                                        .clickable {
                                            viewModel.navigateTo(AppScreen.ChapterList(classInfo.classLevel))
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "Class ${classInfo.classLevel}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }

                                            Text(
                                                text = "${classInfo.totalChapters} अध्याय",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = classInfo.bookTitleHindi,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "नोट्स व 80Q क्विज",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Icon(
                                                Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Daily History Thought / Memory Pearl
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ऐतिहासिक तथ्य एवं परीक्षा गुरुमंत्र",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "“इतिहास केवल तारीखों और युद्धों का नाम नहीं, बल्कि मानव समाज, सभ्यता और विचारों के विकास की निरंतर यात्रा है।” — अध्याय पूरा करने के बाद 80 प्रश्नों का रिवीजन क्विज अवश्य दें।",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
