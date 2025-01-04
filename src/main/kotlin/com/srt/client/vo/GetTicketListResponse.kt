package com.srt.client.vo

import com.srt.service.vo.Ticket
import kotlinx.serialization.Serializable

@Serializable
internal data class GetTicketListResponse(
    val resultMap: List<GetTicketListResultMapResponse>,
    val trainListMap: List<GetTicketListTrainListResponse>,
) {
    val isSuccess: Boolean
        get() = resultMap.isNotEmpty() && resultMap.first().strResult == "SUCC"
}

@Serializable
internal data class GetTicketListResultMapResponse(
    val strResult: String,
)

@Serializable
internal data class GetTicketListTrainListResponse(
    val etcRsvPsbCdNm: String,
    val rcvdAmt: String,
    val sprmRsvPsbStr: String,
    val trnOrdrNo: Long,
    val gnrmRsvPsbStr: String,
    val gnrmRsvPsbColor: String,
    val runTm: String,
    val sprmRsvPsbColor: String,
    val arvTm: String,
    val fresRsvPsbCdNm: String? = null,
    val runDt: String,
    val stlbDturDvCd: String,
    val ocurDlayTnum: Long,
    val dlaySaleFlg: String,
    val seatAttCd: String,
    val rcvdFare: String,
    val arvStnConsOrdr: String,
    val sprmRsvPsbImg: String,
    val rsvWaitPsbCdNm: String,
    val chtnDvCd: String,
    val rsvWaitPsbCd: String,
    val seatSelect: String,
    val dptStnRunOrdr: String,
    val stlbTrnClsfCd: String,
    val trainDiscGenRt: String,
    val dptTm: String,
    val stmpRsvPsbFlgCd: String,
    val trnNstpLeadInfo: String,
    val arvDt: String,
    val gnrmRsvPsbCdNm: String,
    val gnrmRsvPsbImg: String,
    val trnGpCd: String,
    val sprmRsvPsbCdNm: String,
    val payTable: String,
    val dptRsStnCd: String,
    val ymsAplFlg: String,
    val fresOprCno: Long,
    val doReserv: String,
    val timeTable: String,
    val stndRsvPsbCdNm: String,
    val dptStnConsOrdr: String,
    val arvRsStnCd: String,
    val dptDt: String,
    val trnNo: String,
    val arvStnRunOrdr: String,
    val expnDptDlayTnum: String,
    val trnCpsCd1: String,
    val trnCpsCd2: String?,
    val trnCpsCd3: String,
    val trnCpsCd4: String,
    val trnCpsCd5: String,
) {
    fun toTicket(): Ticket {
        return Ticket(
            trainNumber = trnNo,
            departureDate = dptDt,
            departureTime = dptTm,
            arrivalDate = arvDt,
            arrivalTime = arvTm,
            canReserve = (sprmRsvPsbStr == "예약가능"),
        )
    }
}
