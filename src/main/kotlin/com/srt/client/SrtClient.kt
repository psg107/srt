package com.srt.client

import com.srt.util.toFormUrlEncodedString
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.contentType
import io.ktor.http.userAgent
import org.springframework.stereotype.Component

@Component
class SrtClient(
    private val httpClient: HttpClient,
) {
    suspend fun login(id: String, password: String): String {
        return httpClient.post("$BASE_URL/apb/selectListApb01080_n.do") {
            setDefaultUserAgent()
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            setBody(LoginRequest.create(id, password).toFormUrlEncodedString())
        }.body<LoginResponse>().let {
            if (it.success.not()) {
                throw RuntimeException("로그인에 실패했습니다. ${it.userMap["MSG"]}")
            }
            it.sessionId
        }
    }

    private fun HttpMessageBuilder.setDefaultUserAgent(): HttpMessageBuilder {
        userAgent("Mozilla/5.0 (Linux; Android 11; SM-G977N Build/RP1A.200720.012; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/92.0.4515.159 Mobile Safari/537.36SRT-APP-Android V.2.0.4")
        return this
    }

    companion object {
        const val BASE_URL = "https://app.srail.or.kr"
    }
}
