package com.srt.client

import com.srt.client.vo.GetTicketListRequest
import com.srt.client.vo.GetTicketListResponse
import com.srt.client.vo.LoginRequest
import com.srt.service.JwtProvider.Companion.MEMBER_NUMBER
import com.srt.service.JwtProvider.Companion.SESSION_ID
import com.srt.service.JwtProvider.Companion.SRAIL_TYPE10
import com.srt.service.JwtProvider.Companion.SRAIL_TYPE8
import com.srt.service.JwtProvider.Companion.WMONID
import com.srt.service.vo.SrtSession
import com.srt.service.vo.Ticket
import com.srt.share.code.StationCodes
import com.srt.share.value.NetFunnelKey
import com.srt.share.value.NetFunnelKeyAcknowledgeUrl
import com.srt.share.value.RawNetFunnelKeyBody
import com.srt.util.getByName
import com.srt.util.requestGet
import com.srt.util.requestPost
import com.srt.util.toFormUrlEncodedString
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.cookie
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.contentType
import io.ktor.http.userAgent
import kotlinx.coroutines.runBlocking
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
                sessionId = cookies.getByName(SESSION_ID).value,
                wmonid = cookies.getByName(WMONID).value,
                srail_type10 = cookies.getByName(SRAIL_TYPE10) { it.value != "NULL" }.value,
                srail_type8 = cookies.getByName(SRAIL_TYPE8) { it.value != "Y" }.value,
                memberNumber = cookies.getByName(MEMBER_NUMBER).value,
            )
        }
    }

    suspend fun getTicketList(
        departureDate: String,
        departureTime: String,
        departureStationCode: StationCodes,
        arrivalStationCode: StationCodes,
        passengerNumber: Int,
        session: SrtSession,
    ): List<Ticket> {
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
                    netFunnelKey = getNetFunnelKey().netFunnelKey,
                ).toFormUrlEncodedString(),
            )
        }.body.also {
            if (it.isSuccess.not()) {
                throw RuntimeException("티켓 목록을 가져올 수 없습니다.")
            }
        }.let { response ->
            response.trainListMap.map { it.toTicket() }
        }
    }

    private fun getNetFunnelKey(): NetFunnelKey {
        return runBlocking {
            httpClient.requestGet<String>("https://nf.letskorail.com/ts.wseq") {
                setDefaultUserAgent()
                cookie("referer", "https://app.srail.or.kr/")
                parameter("opcode", "5101")
                parameter("nfid", "0")
                parameter("prefix", "NetFunnel.gRtype=5101;")
                parameter("sid", "service_1")
                parameter("aid", "act_10")
                parameter("js", "true")
            }.let {
                RawNetFunnelKeyBody(it.body).tryExtractNetFunnelKeyAndAcknowledgeUrl()
            }.also { (netFunnelKey, acknowledgeUrl) ->
                netFunnelKey.acknowledge(acknowledgeUrl).let { isSuccess ->
                    if (isSuccess.not()) {
                        throw RuntimeException("NetFunnelKey를 확인할 수 없습니다.")
                    }
                }
            }.let { (netFunnelKey, _) ->
                netFunnelKey
            }
        }
    }

    private fun RawNetFunnelKeyBody.tryExtractNetFunnelKeyAndAcknowledgeUrl(): Pair<NetFunnelKey, NetFunnelKeyAcknowledgeUrl> {
        val netFunnelKey = Regex("key=(.+?)&").find(body)?.groupValues?.get(1)
            ?: throw RuntimeException("NetFunnelKey를 가져올 수 없습니다.")
        val acknowledgeUrl = Regex("ip=(.+?)&").find(body)?.groupValues?.get(1)
            ?: throw RuntimeException("NetFunnelKeyAcknowledgeUrl를 가져올 수 없습니다.")

        return NetFunnelKey(netFunnelKey) to NetFunnelKeyAcknowledgeUrl(acknowledgeUrl)
    }

    private suspend fun NetFunnelKey.acknowledge(acknowledgeUrl: NetFunnelKeyAcknowledgeUrl): Boolean {
        return httpClient.requestGet<String>("https://${acknowledgeUrl.acknowledgeUrl}/ts.wseq") {
            setDefaultUserAgent()
            cookie("referer", "https://app.srail.or.kr/")
            parameter("opcode", "5004")
            parameter("key", netFunnelKey)
            parameter("nfid", "0")
            parameter("prefix", "NetFunnel.gRtype=5004;")
            parameter("js", "true")
        }.body.contains("Success")
    }

    private fun HttpMessageBuilder.setDefaultUserAgent(): HttpMessageBuilder {
        userAgent("Mozilla/5.0 (Linux; Android 11; SM-G977N Build/RP1A.200720.012; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/92.0.4515.159 Mobile Safari/537.36SRT-APP-Android V.2.0.4")
        headers.append("X-Requested-With", "kr.co.srail.newapp")
        return this
    }

    private fun HttpMessageBuilder.setAuthenticationCookies(session: SrtSession): HttpMessageBuilder {
        cookie(SESSION_ID, session.sessionId)
        cookie(WMONID, session.wmonid)
        cookie(SRAIL_TYPE10, session.srail_type10)
        cookie(SRAIL_TYPE8, session.srail_type8)
        cookie(MEMBER_NUMBER, session.memberNumber)
        return this
    }

    companion object {
        const val BASE_URL = "https://app.srail.or.kr"
    }
}
