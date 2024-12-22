package com.srt.client.vo

data class LoginRequest(
    val rsvTpCd: String = "",
    val goUrl: String = "",
    val from: String = "",
    val srchDvCd: String = "1",
    val srchDvNm: String,
    val hmpgPwdCphd: String,
) {
    companion object {
        fun create(id: String, password: String): LoginRequest {
            return LoginRequest(
                srchDvNm = id,
                hmpgPwdCphd = password,
            )
        }
    }
}
