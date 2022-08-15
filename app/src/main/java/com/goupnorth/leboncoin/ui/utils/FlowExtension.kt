package com.goupnorth.leboncoin.ui.utils

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<T>.setState(reduce: T.() -> T) {
    value = value.reduce()
}
