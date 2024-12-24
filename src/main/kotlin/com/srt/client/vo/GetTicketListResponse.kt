package com.srt.client.vo

import kotlinx.serialization.Serializable

// TODO
@Serializable
internal data class GetTicketListResponse(
    val resultMap: List<GetTicketListResultMapResponse>,
) {
    val isSuccess: Boolean
        get() = resultMap.isNotEmpty() && resultMap.first().strResult == "SUCC"

    val tickets: List<String>
        get() = resultMap.first().trainListMap.map { it.seatNo }
}

@Serializable
internal data class GetTicketListResultMapResponse(
    val strResult: String,
    val trainListMap: List<GetTicketListTrainListMapResponse>,
)

@Serializable
internal data class GetTicketListTrainListMapResponse(
    val rcvdAmt: Long,
    val scarNo: Long,
    val seatPrc: Long,
    val psrmClCd: String,
    val rqSeatAttCd: String,
    val JRNYLIST_KEY: Long,
    val psgTpCd: String,
    val SEATLIST_KEY: Long,
    val seatNo: String,
    val psgTpDvCd: String,
    val dcntKndCd: String,
    val dcntAmt: Long,
    val seatFare: Long,
)
