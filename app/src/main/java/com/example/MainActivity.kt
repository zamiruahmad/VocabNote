package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.LexiconApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: LexiconViewModel by viewModels()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
      val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
      val activeFont by viewModel.activeFont.collectAsStateWithLifecycle()

      MyApplicationTheme(
         appTheme = activeTheme,
         darkMode = darkMode,
         activeFont = activeFont
      ) {
        LexiconApp(viewModel)
      }
    }
  }
}
