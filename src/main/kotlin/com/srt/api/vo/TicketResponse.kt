package com.srt.api.vo

import com.srt.service.vo.Ticket

data class GetTicketListResponse(
    val tickets: List<TicketResponse>,
) {
    companion object {
        fun of(tickets: List<Ticket>): GetTicketListResponse {
            return GetTicketListResponse(
                tickets = tickets.map(TicketResponse::of),
            )
        }
    }
}

data class TicketResponse(
    val trainNumber: String,
    val departureDate: String,
    val departureTime: String,
    val arrivalDate: String,
    val arrivalTime: String,
    val canReserve: Boolean,
) {
    companion object {
        fun of(ticket: Ticket): TicketResponse {
            return TicketResponse(
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
