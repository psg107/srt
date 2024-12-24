package com.srt.configuration

import com.srt.configuration.TokenAttribute.AUTHORIZATION_HEADER
import com.srt.configuration.TokenAttribute.AUTHORIZATION_HEADER_PREFIX
import com.srt.configuration.TokenAttribute.TOKEN_ATTRIBUTE_NAME
import com.srt.exception.AuthorizationFailedException
import com.srt.service.JwtProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.AsyncHandlerInterceptor

class TokenInterceptor(
    private val jwtProvider: JwtProvider,
) : AsyncHandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) {
            return true
        }

        if (handler.method.isAnnotationPresent(LoginRequired::class.java)) {
            request.extractTokenFromAuthorizationHeader()?.also {
                jwtProvider.verifyToken(it)
            }?.let {
                setTokenToAttribute(request, it)
            } ?: throw AuthorizationFailedException("로그인이 필요합니다.")
        }

        return super.preHandle(request, response, handler)
    }

    private fun HttpServletRequest.extractTokenFromAuthorizationHeader(): String? {
        return this.getHeader(AUTHORIZATION_HEADER)?.takeIf { it.startsWith(AUTHORIZATION_HEADER_PREFIX) }?.substring(
            AUTHORIZATION_HEADER_PREFIX.length,
        )
    }

    private fun setTokenToAttribute(request: HttpServletRequest, token: String) {
        request.setAttribute(
            TOKEN_ATTRIBUTE_NAME,
            TokenHolder(token),
        )
    }
}
