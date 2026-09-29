package com.example.moodify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moodify.ui.MoodifyApp
import com.example.moodify.ui.theme.MoodifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodifyTheme {
                MoodifyApp()
            }
        }
    }
}
