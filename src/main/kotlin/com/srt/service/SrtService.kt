package com.srt.service

import com.srt.client.SrtClient
import com.srt.service.vo.GetTicketListQuery
import com.srt.service.vo.LoginCommand
import com.srt.service.vo.SrtSession
import com.srt.service.vo.Ticket
import com.srt.share.value.JsonWebToken
import org.springframework.stereotype.Service

@Service
class SrtService(
    private val jwtProvider: JwtProvider,
    private val srtClient: SrtClient,
) {
    suspend fun login(loginCommand: LoginCommand): JsonWebToken {
        return srtClient.login(loginCommand.id, loginCommand.password).apply {
            netFunnelKey = srtClient.getNetFunnelKey(this.sessionId).netFunnelKey
        }.let {
            jwtProvider.createToken(it)
        }
    }

    suspend fun list(getTicketListQuery: GetTicketListQuery, session: SrtSession): Pair<JsonWebToken, List<Ticket>> {
        return srtClient.getTicketList(
            departureDate = getTicketListQuery.departureDate,
            departureTime = getTicketListQuery.departureTime,
            departureStationCode = getTicketListQuery.departureStationCode,
            arrivalStationCode = getTicketListQuery.arrivalStationCode,
            passengerNumber = getTicketListQuery.passengerNumber,
            session = session,
        ).let { (sessionId, tickets) ->
            jwtProvider.createToken(session.updateSessionId(sessionId.sessionId)) to tickets
        }
    }
}
