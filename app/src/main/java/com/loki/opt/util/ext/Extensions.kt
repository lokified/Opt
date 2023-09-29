package com.loki.opt.util.ext

fun Int.toTwoDigit(): String {
    val value = this.toString()
    return if (value.length == 2) value else "0$this"
}

fun String.toSingleDigit(): String {
    return if (this.startsWith("0")) this.removePrefix("0") else this
}