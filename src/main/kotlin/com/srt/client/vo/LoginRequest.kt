package com.srt.client.vo

internal data class LoginRequest(
    val srchDvCd: String = "1",
    val srchDvNm: String,
    val check: String = "Y",
    val auto: String = "Y",
    val login_referer: String = "https%3A%2F%2Fapp.srail.or.kr%2Fara%2Fara0101v.do",
    val hmpgPwdCphd: String,
    val page: String = "menu",
    val customerYn: String = "",
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
