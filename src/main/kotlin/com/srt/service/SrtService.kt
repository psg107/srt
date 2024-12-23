package com.srt.service

import com.srt.client.SrtClient
import com.srt.service.vo.Account
import com.srt.service.vo.SrtSessionKey
import org.springframework.stereotype.Service

@Service
class SrtService(
    private val jwtProvider: JwtProvider,
    private val srtClient: SrtClient,
) {
    suspend fun login(account: Account): String {
        val sessionId = srtClient.login(account.id, account.password)
        val netFunnelKey = srtClient.getNetFunnelKey(sessionId)
        val srtSessionKey = SrtSessionKey(
            sessionId = sessionId,
            netFunnelKey = netFunnelKey,
        )

        return jwtProvider.createToken(srtSessionKey)
    }
}
