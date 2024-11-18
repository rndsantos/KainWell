package com.example.kainwell.ui.utils

import android.content.Context
import android.content.res.Configuration

fun isDarkMode(context: Context): Boolean {
    val nightModeFlags: Int =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}