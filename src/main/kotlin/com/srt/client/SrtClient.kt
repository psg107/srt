package com.srt.client

import com.srt.client.vo.GetNetFunnelKeyRequest
import com.srt.client.vo.GetTicketListRequest
import com.srt.client.vo.LoginRequest
import com.srt.client.vo.LoginResponse
import com.srt.code.StationCodes
import com.srt.configuration.TokenHolder
import com.srt.util.toFormUrlEncodedString
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.cookie
import io.ktor.client.request.post
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
    suspend fun login(id: String, password: String): SessionKey {
        return httpClient.post("$BASE_URL/apb/selectListApb01080_n.do") {
            setDefaultUserAgent()
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            setBody(LoginRequest.create(id, password).toFormUrlEncodedString())
        }.body<LoginResponse>().let {
            if (it.success.not()) {
                throw RuntimeException("로그인에 실패했습니다. ${it.userMap["MSG"]}")
            }
            it.sessionId
        }
    }

    suspend fun getNetFunnelKey(key: SessionKey): NetFunnelKey {
        return httpClient.post("https://nf.letskorail.com/ts.wseq") {
            setDefaultUserAgent()
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.FormUrlEncoded)
            cookie("JSESSIONID_ETK", key)
            setBody(GetNetFunnelKeyRequest().toFormUrlEncodedString())
        }.body<String>().let {
            Regex("key=(.+?)&").find(it)?.groupValues?.get(1)
                ?: throw RuntimeException("NetFunnelKey를 가져올 수 없습니다.")
        }
    }

    suspend fun getTicketList(
        departureDate: String,
        departureTime: String,
        departureStationCode: StationCodes,
        arrivalStationCode: StationCodes,
        passengerNumber: Int,
        tokenHolder: TokenHolder,
    ): List<String> {
        // TODO: 정상적인 경로로 접근 부탁드립니다. 오류 발생함, netFunnelKey, JSESSIONID_ETK 외 추가적으로 필요한 정보가 있음
        return httpClient.post("$BASE_URL/ara/selectListAra10007_n.do") {
            setDefaultUserAgent()
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            cookie("JSESSIONID_ETK", tokenHolder.sessionId)
            setBody(
                GetTicketListRequest.create(
                    departureDate = departureDate,
                    departureTime = departureTime,
                    departureStationCode = departureStationCode,
                    arrivalStationCode = arrivalStationCode,
                    passengerNumber = passengerNumber,
                    netFunnelKey = tokenHolder.netFunnelKey,
                ).toFormUrlEncodedString(),
            )
        }.body<String>().let {
            println(it)
            emptyList<String>()
        }
//        return httpClient.post("$BASE_URL/ara/selectListAra10007_n.do") {
//            setDefaultUserAgent()
//            contentType(ContentType.Application.FormUrlEncoded)
//            accept(ContentType.Application.Json)
//            cookie("JSESSIONID_ETK", tokenHolder.sessionId)
//            setBody(GetTicketListRequest.create(
//                departureDate = departureDate,
//                departureTime = departureTime,
//                departureStationCode = departureStationCode,
//                arrivalStationCode = arrivalStationCode,
//                passengerNumber = passengerNumber,
//                netFunnelKey = tokenHolder.netFunnelKey,
//            ).toFormUrlEncodedString())
//        }.body<GetTicketListResponse>().let {
//            if (it.isSuccess.not()) {
//                throw RuntimeException("티켓 목록을 가져올 수 없습니다.")
//            }
//            it.tickets
//        }
    }

    private fun HttpMessageBuilder.setDefaultUserAgent(): HttpMessageBuilder {
        userAgent("Mozilla/5.0 (Linux; Android 11; SM-G977N Build/RP1A.200720.012; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/92.0.4515.159 Mobile Safari/537.36SRT-APP-Android V.2.0.4")
        return this
    }

    companion object {
        const val BASE_URL = "https://app.srail.or.kr"
    }
}

typealias SessionKey = String
typealias NetFunnelKey = String
