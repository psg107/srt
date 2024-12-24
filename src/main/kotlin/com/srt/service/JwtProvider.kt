package com.srt.service

import com.srt.exception.InvalidTokenException
import com.srt.service.vo.SrtSessionKey
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
    fun createToken(srtSessionKey: SrtSessionKey): String {
        return Jwts.builder()
            .claims(
                mapOf(
                    SESSION_ID to srtSessionKey.sessionId,
                    NET_FUNNEL_KEY to srtSessionKey.netFunnelKey,
                ),
            )
            .issuedAt(now())
            .expiration(now(TOKEN_EXPIRATION_TIME))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
    }

    fun verifyToken(token: String) {
        try {
            val claims = buildParser().parseSignedClaims(token).payload
            if (claims.expiration.before(now())) {
                throw InvalidTokenException("로그인 세션이 만료되었습니다.")
            }
        } catch (ex: Exception) {
            throw InvalidTokenException("로그인 정보가 잘못되었습니다. 다시 로그인을 해주세요.", ex)
        }
    }

    fun parseToken(token: String): SrtSessionKey {
        try {
            val claims = buildParser().parseSignedClaims(token).payload
            return SrtSessionKey(
                sessionId = claims[SESSION_ID] as String,
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
        const val NET_FUNNEL_KEY = "netFunnelKey"
    }
}
