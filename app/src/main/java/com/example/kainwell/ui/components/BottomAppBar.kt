package com.example.kainwell.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kainwell.ui.Dimensions.MediumPadding

@Composable
fun KainWellBottomAppBar(
    onClick: () -> Unit,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 5.dp,
    ) {
        KainWellButton(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            shape = MaterialTheme.shapes.small,
            contentPadding = contentPadding,
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(MediumPadding)
        ) {
            content()
        }
    }
}