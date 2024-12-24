package com.srt.configuration

import com.srt.configuration.TokenAttribute.TOKEN_ATTRIBUTE_NAME
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class TokenArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return TokenHolder::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        return webRequest.getTokenFromAttribute()
    }

    private fun NativeWebRequest.getTokenFromAttribute(): TokenHolder {
        return this.getAttribute(TOKEN_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST) as TokenHolder
    }
}
