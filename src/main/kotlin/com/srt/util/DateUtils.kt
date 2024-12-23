package com.srt.util

import java.util.Date

fun now(offset: Number = 0): Date {
    return Date(System.currentTimeMillis() + offset.toLong())
}
