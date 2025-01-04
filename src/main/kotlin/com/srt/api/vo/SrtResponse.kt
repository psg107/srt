package com.srt.api.vo

import kotlinx.serialization.Serializable

@Serializable
data class SrtResponse<T>(
    val token: String,
    val data: T? = null,
) {
    companion object {
        fun <T> of(token: String, data: T): SrtResponse<T> {
            return SrtResponse(
                token = token,
                data = data,
            )
        }

        fun of(token: String): SrtResponse<Unit> {
            return SrtResponse(
                token = token,
            )
        }
    }
}
