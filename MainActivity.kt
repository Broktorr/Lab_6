package com.example.jetcompous

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userViewModel: UserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        setContent {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                val navController = rememberNavController()
                MainActivityApp(userViewModel, navController)
            }
        }
    }
}

@Composable
fun MainActivityApp(userViewModel: UserViewModel, navController: NavController) {
    MaterialTheme (colors = darkColors()) {
        MyApp(userViewModel, navController)
    }
}


