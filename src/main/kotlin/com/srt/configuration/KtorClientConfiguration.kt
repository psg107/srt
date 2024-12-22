package com.srt.configuration

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json

@Configuration
class KtorClientConfiguration {
    @Bean
    fun httpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}
