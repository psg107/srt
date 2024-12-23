package com.srt.service

import com.srt.service.vo.SrtSessionKey
import com.srt.util.now
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

    fun verifyToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
                .build()
                .parseSignedClaims(token)
            claims.payload.expiration.after(now())
        } catch (e: Exception) {
            false
        }
    }

    fun parseToken(token: String): SrtSessionKey {
        val claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload
        return SrtSessionKey(
            sessionId = claims[SESSION_ID] as String,
            netFunnelKey = claims[NET_FUNNEL_KEY] as String,
        )
    }

    companion object {
        const val TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 // 1시간
        const val SESSION_ID = "sessionId"
        const val NET_FUNNEL_KEY = "netFunnelKey"
    }
}
