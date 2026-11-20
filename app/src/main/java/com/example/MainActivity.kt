package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.FitMarathiDatabase
import com.example.data.repository.DailyHealthGoalRepository
import com.example.data.repository.FitMarathiRepository
import com.example.ui.FitMarathiApp
import com.example.ui.theme.FitMarathiTheme
import com.example.ui.viewmodel.DailyHealthGoalViewModel
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

    private lateinit var repository: FitMarathiRepository
    private lateinit var dailyHealthViewModel: DailyHealthGoalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure App Check before Compose UI or Firebase operations
        configureAppCheck(this, intent)

        val database = FitMarathiDatabase.getInstance(applicationContext)
        val healthGoalRepository = DailyHealthGoalRepository(database.dailyHealthGoalDao())
        repository = FitMarathiRepository(
            context = applicationContext,
            dailyHealthGoalRepository = healthGoalRepository
        )
        dailyHealthViewModel = ViewModelProvider(
            this,
            DailyHealthGoalViewModel.Factory(repository = healthGoalRepository)
        )[DailyHealthGoalViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            FitMarathiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FitMarathiApp(
                        repository = repository,
                        dailyHealthViewModel = dailyHealthViewModel
                    )
                }
            }
        }
    }
}
