package com.srt.client

import com.srt.client.vo.GetNetFunnelKeyRequest
import com.srt.client.vo.GetTicketListRequest
import com.srt.client.vo.GetTicketListResponse
import com.srt.client.vo.LoginRequest
import com.srt.service.vo.SrtSession
import com.srt.service.vo.Ticket
import com.srt.share.code.StationCodes
import com.srt.share.value.NetFunnelKey
import com.srt.share.value.SessionId
import com.srt.util.findByName
import com.srt.util.getByName
import com.srt.util.requestPost
import com.srt.util.toFormUrlEncodedString
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.cookie
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.contentType
import io.ktor.http.userAgent
import org.springframework.stereotype.Component

@Component
class SrtClient(
    private val httpClient: HttpClient,
) {
    suspend fun login(id: String, password: String): SrtSession {
        return httpClient.requestPost<String>("$BASE_URL/apb/selectListApb01080_n.do") {
            setDefaultUserAgent()
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            setBody(LoginRequest.create(id, password).toFormUrlEncodedString())
        }.also {
            if (it.cookies.size < 4) {
                throw RuntimeException("로그인에 실패했습니다. 쿠키를 가져올 수 없습니다.")
            }
        }.cookies.let { cookies ->
            SrtSession(
                sessionId = cookies.getByName("JSESSIONID_XEBEC").value,
                wmonid = cookies.getByName("WMONID").value,
                srail_type10 = cookies.getByName("srail_type10", { it.value != "NULL" }).value,
                srail_type8 = cookies.getByName("srail_type8", { it.value != "Y" }).value,
            )
        }
    }

    suspend fun getNetFunnelKey(sessionId: String): NetFunnelKey {
        return httpClient.requestPost<String>("https://nf.letskorail.com/ts.wseq") {
            setDefaultUserAgent()
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.FormUrlEncoded)
            cookie("JSESSIONID_ETK", sessionId)
            setBody(GetNetFunnelKeyRequest().toFormUrlEncodedString())
        }.let {
            extractNetFunnelKeyOrThrows(it.body)
        }
    }

    suspend fun getTicketList(
        departureDate: String,
        departureTime: String,
        departureStationCode: StationCodes,
        arrivalStationCode: StationCodes,
        passengerNumber: Int,
        session: SrtSession,
    ): Pair<SessionId, List<Ticket>> {
        return httpClient.requestPost<GetTicketListResponse>("$BASE_URL/ara/selectListAra10007_n.do") {
            setDefaultUserAgent()
            setAuthenticationCookies(session)
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            setBody(
                GetTicketListRequest.create(
                    departureDate = departureDate,
                    departureTime = departureTime,
                    departureStationCode = departureStationCode,
                    arrivalStationCode = arrivalStationCode,
                    passengerNumber = passengerNumber,
                    netFunnelKey = session.netFunnelKey!!,
                ).toFormUrlEncodedString(),
            )
        }.also {
            if (it.body.isSuccess.not()) {
                throw RuntimeException("티켓 목록을 가져올 수 없습니다.")
            }
        }.let {
            val sessionId = SessionId((it.cookies.findByName("JSESSIONID_XEBEC")?.value ?: session.sessionId))
            val tickets = it.body.trainListMap.map { it.toTicket() }

            sessionId to tickets
        }
    }

    private fun extractNetFunnelKeyOrThrows(body: String): NetFunnelKey {
        return Regex("key=(.+?)&").find(body)?.groupValues?.get(1)?.let {
            NetFunnelKey(it)
        } ?: throw RuntimeException("NetFunnelKey를 가져올 수 없습니다.")
    }

    private fun HttpMessageBuilder.setDefaultUserAgent(): HttpMessageBuilder {
        userAgent("Mozilla/5.0 (Linux; Android 11; SM-G977N Build/RP1A.200720.012; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/92.0.4515.159 Mobile Safari/537.36SRT-APP-Android V.2.0.4")
        return this
    }

    private fun HttpMessageBuilder.setAuthenticationCookies(session: SrtSession): HttpMessageBuilder {
        cookie("JSESSIONID_XEBEC", session.sessionId)
        cookie("WMONID", session.wmonid)
        cookie("srail_type10", session.srail_type10)
        cookie("srail_type8", session.srail_type8)
        return this
    }

    companion object {
        const val BASE_URL = "https://app.srail.or.kr"
    }
}
