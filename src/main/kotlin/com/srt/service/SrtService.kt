package com.srt.service

import com.srt.client.SrtClient
import com.srt.service.vo.GetTicketListQuery
import com.srt.service.vo.LoginCommand
import com.srt.service.vo.SrtSession
import com.srt.service.vo.Ticket
import org.springframework.stereotype.Service

@Service
class SrtService(
    private val srtClient: SrtClient,
) {
    suspend fun login(loginCommand: LoginCommand): SrtSession {
        return srtClient.login(loginCommand.id, loginCommand.password).apply {
            netFunnelKey = srtClient.getNetFunnelKey().netFunnelKey
        }
    }

    suspend fun list(getTicketListQuery: GetTicketListQuery, session: SrtSession): List<Ticket> {
        return srtClient.getTicketList(
            departureDate = getTicketListQuery.departureDate,
            departureTime = getTicketListQuery.departureTime,
            departureStationCode = getTicketListQuery.departureStationCode,
            arrivalStationCode = getTicketListQuery.arrivalStationCode,
            passengerNumber = getTicketListQuery.passengerNumber,
            session = session,
        )
    }
}
