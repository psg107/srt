package com.srt.client.vo

import com.srt.code.SeatType
import com.srt.code.StationCodes

internal data class GetTicketListRequest(
    val chtnDvCd: String = "1",
    val dptDt: String,
    val dptTm: String,
    val dptRsStnCd: String,
    val arvRsStnCd: String,
    val stlbTrnClsfCd: String = "05",
    val trnGpCd: String = "109",
    val psgNum: Int = 1,
    val seatAttCd: String = SeatType.GENERAL.code,
    val netfunnelKey: String,
) {
    init {
//        require(dptDt.length == 8) { "Invalid departure date: $dptDt" }
//        require(dptTm.length == 6) { "Invalid departure time: $dptTm" }
        // TODO: dptDt: yyyyMMdd
        // TODO: dptTm: HHmm00
        // TODO: psgNum >= 1
    }

    companion object {
        fun create(
            departureDate: String,
            departureTime: String,
            departureStationCode: StationCodes,
            arrivalStationCode: StationCodes,
            passengerNumber: Int,
            netFunnelKey: String,
        ): GetTicketListRequest {
            return GetTicketListRequest(
                dptDt = departureDate,
                dptTm = departureTime,
                dptRsStnCd = departureStationCode.code,
                arvRsStnCd = arrivalStationCode.code,
                psgNum = passengerNumber,
                netfunnelKey = netFunnelKey,
            )
        }
    }
}
