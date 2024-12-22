package com.srt.util

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun Any.toFormUrlEncodedString(): String {
    val memberProperties = this::class.memberProperties as Collection<KProperty1<Any, *>>
    return memberProperties
        .filter { it.get(this) != null }
        .map { it.name to it.get(this)?.toString() }
        .filter { it.second != null }
        .joinToString("&") { (key, value) ->
            "$key=$value"
        }
}
