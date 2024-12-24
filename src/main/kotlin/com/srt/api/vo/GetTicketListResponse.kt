package com.srt.api.vo

import com.srt.service.vo.Ticket

data class GetTicketListResponse(
    val trainNumber: String,
    val departureDate: String,
    val departureTime: String,
    val arrivalDate: String,
    val arrivalTime: String,
    val canReserve: Boolean,
) {
    companion object {
        fun of(ticket: Ticket): GetTicketListResponse {
            return GetTicketListResponse(
                trainNumber = ticket.trainNumber,
                departureDate = ticket.departureDate,
                departureTime = ticket.departureTime,
                arrivalDate = ticket.arrivalDate,
                arrivalTime = ticket.arrivalTime,
                canReserve = ticket.canReserve,
            )
        }
    }
}
