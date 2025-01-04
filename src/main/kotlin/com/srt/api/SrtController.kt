package com.srt.api

import com.srt.api.vo.GetTicketListRequest
import com.srt.api.vo.GetTicketListResponse
import com.srt.api.vo.LoginRequest
import com.srt.api.vo.SrtResponse
import com.srt.configuration.LoginRequired
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
) {
    @PostMapping("/login")
    suspend fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<SrtResponse<Unit>> {
        return ResponseEntity.ok(
            SrtResponse.of(
                token = srtService.login(loginRequest.toCommand()).token,
            ),
        )
    }

    @LoginRequired
    @GetMapping("/list")
    suspend fun list(@ParameterObject request: GetTicketListRequest, session: SrtSession): ResponseEntity<SrtResponse<List<GetTicketListResponse>>> {
        return srtService.list(request.toQuery(), session).let { (token, tickets) ->
            ResponseEntity.ok(
                SrtResponse.of(
                    token = token.token,
                    data = tickets.map { GetTicketListResponse.of(it) },
                ),
            )
        }
    }
}
