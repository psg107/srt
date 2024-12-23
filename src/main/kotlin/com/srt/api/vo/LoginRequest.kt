package com.srt.api.vo

import com.srt.service.vo.Account

data class LoginRequest(
    val id: String,
    val password: String,
) {
    fun toAccount(): Account {
        return Account(
            id = id,
            password = password,
        )
    }
}
