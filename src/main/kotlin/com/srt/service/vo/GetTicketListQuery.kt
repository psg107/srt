package com.srt.service.vo

import com.srt.code.StationCodes

data class GetTicketListQuery(
    val departureDate: String,
    val departureTime: String,
    val departureStationCode: StationCodes,
    val arrivalStationCode: StationCodes,
    val passengerNumber: Int,
)
