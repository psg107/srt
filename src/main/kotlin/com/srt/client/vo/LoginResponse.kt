package com.srt.client.vo

import kotlinx.serialization.Serializable

// TODO: 더 많은 필드들 존재, 로그인 성공 판단을 위한 값 확인 필요
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
}
