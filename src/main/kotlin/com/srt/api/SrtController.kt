package com.srt.api

import com.srt.api.vo.LoginRequest
import com.srt.api.vo.LoginResponse
import com.srt.service.SrtService
import org.springframework.http.ResponseEntity
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
                srtService.login(loginRequest.toAccount()),
            ),
        )
    }
}
