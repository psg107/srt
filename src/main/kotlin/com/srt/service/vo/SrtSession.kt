package com.srt.service.vo

data class SrtSession(
    var sessionId: String,
    val wmonid: String,
    val srail_type10: String,
    val srail_type8: String,
    var netFunnelKey: String? = null,
) {
    fun updateSessionId(sessionId: String) = apply {
        this.sessionId = sessionId
    }
}
