package com.srt.client.vo

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginResponse(
    val userMap: Map<String, String>,
) {
    val success: Boolean
        get() = userMap["MSG"] == "정상적으로 로그인되었습니다."

    val sessionId: String
        get() {
            return userMap["KR_JSESSIONID"]?.let {
                Regex("^JSESSIONID=(.+);Path=/$").find(it)
            }?.let {
                it.groupValues[1]
            } ?: throw RuntimeException("세션 정보를 가져올 수 없습니다.")
        }

    val wmonid: String
        get() {
            return userMap["KR_JSESSIONID"]?.let {
                Regex("^JSESSIONID=(.+);Path=/$").find(it)
            }?.let {
                it.groupValues[1]
            } ?: throw RuntimeException("세션 정보를 가져올 수 없습니다.")
        }
}
