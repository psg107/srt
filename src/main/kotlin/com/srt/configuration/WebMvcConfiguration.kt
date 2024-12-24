package com.srt.configuration

import com.srt.service.JwtProvider
import org.springframework.context.annotation.Configuration
import org.springframework.util.AntPathMatcher
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfiguration(
    private val jwtProvider: JwtProvider,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(tokenInterceptor())
            .addPathPatterns("/srt/**")
            .excludePathPatterns("/health", "/srt/login")
            .pathMatcher(AntPathMatcher())
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(TokenArgumentResolver())
    }

    fun tokenInterceptor(): TokenInterceptor = TokenInterceptor(jwtProvider)
}
