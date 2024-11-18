package com.example.kainwell.ui.common.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxSize()) {
        Box {
            Text("Loading")
//            AnimationLoader(
//                animationResId = R.raw.loading_bee,
//                modifier = Modifier
//                    .size(LoadingIconSize)
//                    .align(Alignment.Center)
//
//            )
        }
    }
}