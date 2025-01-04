package com.srt.service

import com.srt.exception.InvalidTokenException
import com.srt.service.vo.SrtSession
import com.srt.share.value.JsonWebToken
import com.srt.util.now
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JwtProvider(
    @Value("\${jwt.secret-key}") private val secretKey: String,
) {
    fun createToken(srtSession: SrtSession): JsonWebToken {
        return Jwts.builder()
            .claims(
                mapOf(
                    SESSION_ID to srtSession.sessionId,
                    WMONID to srtSession.wmonid,
                    SRAIL_TYPE10 to srtSession.srail_type10,
                    SRAIL_TYPE8 to srtSession.srail_type8,
                    NET_FUNNEL_KEY to srtSession.netFunnelKey,
                ),
            )
            .issuedAt(now())
            .expiration(now(TOKEN_EXPIRATION_TIME))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
            .let(::JsonWebToken)
    }

    fun verifyToken(token: JsonWebToken) {
        try {
            val claims = buildParser().parseSignedClaims(token.token).payload
            if (claims.expiration.before(now())) {
                throw InvalidTokenException("로그인 세션이 만료되었습니다.")
            }
        } catch (ex: Exception) {
            throw InvalidTokenException("로그인 정보가 잘못되었습니다. 다시 로그인을 해주세요.", ex)
        }
    }

    fun parseToken(token: JsonWebToken): SrtSession {
        try {
            val claims = buildParser().parseSignedClaims(token.token).payload
            return SrtSession(
                sessionId = claims[SESSION_ID] as String,
                wmonid = claims[WMONID] as String,
                srail_type10 = claims[SRAIL_TYPE10] as String,
                srail_type8 = claims[SRAIL_TYPE8] as String,
                netFunnelKey = claims[NET_FUNNEL_KEY] as String,
            )
        } catch (ex: Exception) {
            throw InvalidTokenException("로그인 정보가 잘못되었습니다.")
        }
    }

    private fun buildParser(): JwtParser {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .build()
    }

    companion object {
        const val TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 // 1시간
        const val SESSION_ID = "sessionId"
        const val WMONID = "wmonid"
        const val SRAIL_TYPE10 = "srail_type10"
        const val SRAIL_TYPE8 = "srail_type8"
        const val NET_FUNNEL_KEY = "netFunnelKey"
    }
}
