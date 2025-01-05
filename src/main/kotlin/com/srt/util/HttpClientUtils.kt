package com.srt.util

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.request
import io.ktor.http.Cookie
import io.ktor.http.setCookie

suspend inline fun <reified T> HttpClient.requestPost(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {},
): PostResult<T> {
    var rawBody = ""

    return try {
        val response = this.post(url) {
            block()
        }
        rawBody = response.body<String>()

        PostResult(
            request = response.request,
            body = response.body<T>(),
            cookies = response.setCookie().map { SimpleCookie.of(it) },
        )
    } catch (ex: Exception) {
        throw RuntimeException("요청 처리 중 예기치 않은 오류가 발생했습니다. - $rawBody", ex)
    }
}

suspend inline fun <reified T> HttpClient.requestGet(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {},
): PostResult<T> {
    var rawBody = ""

    return try {
        val response = this.get(url) {
            block()
        }
        rawBody = response.body<String>()

        PostResult(
            request = response.request,
            body = response.body<T>(),
            cookies = response.setCookie().map { SimpleCookie.of(it) },
        )
    } catch (ex: Exception) {
        throw RuntimeException("요청 처리 중 예기치 않은 오류가 발생했습니다. - $rawBody", ex)
    }
}

data class PostResult<T>(
    val request: HttpRequest,
    val body: T,
    val cookies: List<SimpleCookie>,
)

data class SimpleCookie(
    val name: String,
    val value: String,
) {
    companion object {
        fun of(cookie: Cookie): SimpleCookie {
            return SimpleCookie(
                name = cookie.name,
                value = cookie.value,
            )
        }
    }
}

fun List<SimpleCookie>.getByName(name: String): SimpleCookie {
    return this.first { it.name == name }
}

fun List<SimpleCookie>.getByName(name: String, predicate: (SimpleCookie) -> Boolean): SimpleCookie {
    return this.first { it.name == name && predicate(it) }
}

fun List<SimpleCookie>.findByName(name: String): SimpleCookie? {
    return this.firstOrNull { it.name == name }
}
