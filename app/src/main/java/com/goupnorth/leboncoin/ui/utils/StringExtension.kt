package com.goupnorth.leboncoin.ui.utils

import java.util.*

fun String.capitalize(locale: Locale = Locale.getDefault()): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}
