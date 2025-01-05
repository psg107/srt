package com.srt.api.vo

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
) {
    companion object {
        fun of(token: String): LoginResponse {
            return LoginResponse(
                token = token,
            )
        }
    }
}
