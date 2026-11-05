package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.navigation.AryaHistoryApp
import com.example.ui.theme.AryaHistoryTheme
import com.example.ui.viewmodel.HistoryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AryaHistoryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AryaHistoryApp(viewModel = viewModel)
                }
            }
        }
    }
}
