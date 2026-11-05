package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.analytics.AnalyticsScreen
import com.example.ui.screens.bookmarks.BookmarksScreen
import com.example.ui.screens.classes.ChapterListScreen
import com.example.ui.screens.classes.ChapterNotesScreen
import com.example.ui.screens.classes.ClassesListScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.quiz.QuizResultScreen
import com.example.ui.screens.quiz.QuizScreen
import com.example.ui.screens.sampurna500.Sampurna500FolderScreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.HistoryViewModel

@Composable
fun AryaHistoryApp(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle Android system back button
    BackHandler(enabled = currentScreen !is AppScreen.Home) {
        viewModel.navigateBack()
    }

    val isTopLevelScreen = currentScreen is AppScreen.Home ||
            currentScreen is AppScreen.Classes ||
            currentScreen is AppScreen.Sampurna500Folder ||
            currentScreen is AppScreen.Analytics

    Scaffold(
        bottomBar = {
            if (isTopLevelScreen) {
                NavigationBar(modifier = Modifier.testTag("main_navigation_bar")) {
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Home,
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("होम") },
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Classes,
                        onClick = { viewModel.navigateTo(AppScreen.Classes) },
                        icon = { Icon(Icons.Default.School, contentDescription = "Classes") },
                        label = { Text("कक्षा 5-12") },
                        modifier = Modifier.testTag("nav_item_classes")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Sampurna500Folder,
                        onClick = { viewModel.navigateTo(AppScreen.Sampurna500Folder) },
                        icon = { Icon(Icons.Default.FolderSpecial, contentDescription = "500 Bank") },
                        label = { Text("500 बैंक") },
                        modifier = Modifier.testTag("nav_item_500_bank")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Analytics,
                        onClick = { viewModel.navigateTo(AppScreen.Analytics) },
                        icon = { Icon(Icons.Default.Insights, contentDescription = "Analytics") },
                        label = { Text("प्रगति") },
                        modifier = Modifier.testTag("nav_item_analytics")
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Home -> HomeScreen(viewModel = viewModel)
                is AppScreen.Classes -> ClassesListScreen(viewModel = viewModel)
                is AppScreen.ChapterList -> ChapterListScreen(classLevel = screen.classLevel, viewModel = viewModel)
                is AppScreen.ChapterNotes -> ChapterNotesScreen(chapterId = screen.chapterId, viewModel = viewModel)
                is AppScreen.Sampurna500Folder -> Sampurna500FolderScreen(viewModel = viewModel)
                is AppScreen.Quiz -> QuizScreen(viewModel = viewModel)
                is AppScreen.QuizResult -> QuizResultScreen(viewModel = viewModel)
                is AppScreen.Analytics -> AnalyticsScreen(viewModel = viewModel)
                is AppScreen.Bookmarks -> BookmarksScreen(viewModel = viewModel)
            }
        }
    }
}
