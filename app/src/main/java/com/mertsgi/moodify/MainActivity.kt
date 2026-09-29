package com.mertsgi.moodify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.mertsgi.moodify.ui.MoodifyApp
import com.mertsgi.moodify.ui.theme.MoodifyTheme
import com.mertsgi.moodify.viewmodel.MoodifyViewModel
import com.mertsgi.moodify.viewmodel.MoodifyViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MoodifyViewModel by viewModels {
        val app = application as MoodifyApplication
        MoodifyViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodifyTheme {
                MoodifyApp(viewModel = viewModel)
            }
        }
    }
}
