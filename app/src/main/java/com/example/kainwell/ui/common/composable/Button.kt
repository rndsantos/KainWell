package com.example.kainwell.ui.common.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role

@Composable
fun KainWellButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ButtonShape,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    border: BorderStroke? = null,
    backgroundGradient: List<Color> = listOf(),
    disabledBackgroundGradient: List<Color> = listOf(),
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledContentColor: Color = MaterialTheme.colorScheme.scrim,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        shape = shape,
        color = if (backgroundGradient.isEmpty()) containerColor else Color.Transparent,
        contentColor = if (enabled) contentColor else disabledContentColor,
        border = border,
        modifier = modifier
            .clip(shape)
            .then(
                if (backgroundGradient.isNotEmpty()) {
                    Modifier.background(
                        brush = Brush.horizontalGradient(
                            colors = if (enabled) backgroundGradient else disabledBackgroundGradient
                        )
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.labelLarge
        ) {
            Row(
                Modifier
                    .defaultMinSize(
                        minWidth = ButtonDefaults.MinWidth,
                        minHeight = ButtonDefaults.MinHeight
                    )
                    .indication(interactionSource, ripple())
                    .padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

private val ButtonShape = RoundedCornerShape(percent = 50)