package com.srt.api

import com.srt.api.vo.GetTicketListRequest
import com.srt.api.vo.GetTicketListResponse
import com.srt.api.vo.LoginRequest
import com.srt.api.vo.LoginResponse
import com.srt.configuration.LoginRequired
import com.srt.configuration.TokenHolder
import com.srt.service.SrtService
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
) {
    @PostMapping("/login")
    suspend fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(
            LoginResponse(
                srtService.login(loginRequest.toCommand()),
            ),
        )
    }

    @LoginRequired
    @GetMapping("/list")
    suspend fun list(@ParameterObject request: GetTicketListRequest, tokenHolder: TokenHolder): ResponseEntity<GetTicketListResponse> {
        return ResponseEntity.ok(
            GetTicketListResponse(
                srtService.list(request.toQuery(), tokenHolder),
            ),
        )
    }
}
