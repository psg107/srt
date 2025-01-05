package com.srt.api

import com.srt.api.vo.GetTicketListRequest
import com.srt.api.vo.GetTicketListResponse
import com.srt.api.vo.LoginRequest
import com.srt.api.vo.LoginResponse
import com.srt.configuration.LoginRequired
import com.srt.service.JwtProvider
import com.srt.service.SrtService
import com.srt.service.vo.SrtSession
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/srt")
@RestController
class SrtController(
    private val srtService: SrtService,
    private val jwtProvider: JwtProvider,
) {
    @PostMapping("/login")
    suspend fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(
            LoginResponse.of(
                token = jwtProvider.createToken(
                    srtService.login(loginRequest.toCommand()),
                ).token,
            ),
        )
    }

    @LoginRequired
    @GetMapping("/list")
    suspend fun list(@ParameterObject request: GetTicketListRequest, session: SrtSession): ResponseEntity<GetTicketListResponse> {
        return srtService.list(request.toQuery(), session).let { tickets ->
            ResponseEntity.ok(
                GetTicketListResponse.of(tickets),
            )
        }
    }
}
