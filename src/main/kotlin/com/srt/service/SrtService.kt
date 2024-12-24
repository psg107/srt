package com.srt.service

import com.srt.client.SrtClient
import com.srt.configuration.TokenHolder
import com.srt.service.vo.GetTicketListQuery
import com.srt.service.vo.LoginCommand
import com.srt.service.vo.SrtSessionKey
import org.springframework.stereotype.Service

@Service
class SrtService(
    private val jwtProvider: JwtProvider,
    private val srtClient: SrtClient,
) {
    suspend fun login(loginCommand: LoginCommand): String {
        val sessionId = srtClient.login(loginCommand.id, loginCommand.password)
        val netFunnelKey = srtClient.getNetFunnelKey(sessionId)
        val srtSessionKey = SrtSessionKey(
            sessionId = sessionId,
            netFunnelKey = netFunnelKey,
        )

        return jwtProvider.createToken(srtSessionKey)
    }

    suspend fun list(getTicketListQuery: GetTicketListQuery, tokenHolder: TokenHolder): List<String> {
        return srtClient.getTicketList(
            departureDate = getTicketListQuery.departureDate,
            departureTime = getTicketListQuery.departureTime,
            departureStationCode = getTicketListQuery.departureStationCode,
            arrivalStationCode = getTicketListQuery.arrivalStationCode,
            passengerNumber = getTicketListQuery.passengerNumber,
            tokenHolder = tokenHolder,
        )
    }
}
