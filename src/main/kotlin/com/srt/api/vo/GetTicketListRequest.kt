package com.srt.api.vo

import com.srt.code.StationCodes
import com.srt.service.vo.GetTicketListQuery

data class GetTicketListRequest(
    val departureDate: String,
    val departureTime: String,
    val departureStationCode: String,
    val arrivalStationCode: String,
    val passengerNumber: Int = 1,
) {
    init {
        // TODO: validate
    }

    fun toQuery(): GetTicketListQuery {
        return GetTicketListQuery(
            departureDate = departureDate,
            departureTime = departureTime,
            departureStationCode = StationCodes.fromCode(departureStationCode),
            arrivalStationCode = StationCodes.fromCode(arrivalStationCode),
            passengerNumber = passengerNumber,
        )
    }
}