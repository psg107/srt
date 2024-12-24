package com.srt.api.vo

import com.srt.service.vo.LoginCommand

data class LoginRequest(
    val id: String,
    val password: String,
) {
    fun toCommand(): LoginCommand {
        return LoginCommand(
            id = id,
            password = password,
        )
    }
}
