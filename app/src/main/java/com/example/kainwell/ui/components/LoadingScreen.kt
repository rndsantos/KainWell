package com.example.kainwell.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingScreen() {
    Box(
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}