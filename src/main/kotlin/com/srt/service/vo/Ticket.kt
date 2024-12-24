package com.srt.service.vo

data class Ticket(
    val trainNumber: String,
    val departureDate: String,
    val departureTime: String,
    val arrivalDate: String,
    val arrivalTime: String,
    val canReserve: Boolean,
)
