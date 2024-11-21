package com.example.kainwell.ui.common.ext

import java.util.Locale

fun String.titlecase(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
}