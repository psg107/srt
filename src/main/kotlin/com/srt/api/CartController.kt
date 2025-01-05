package com.srt.api

import com.srt.configuration.LoginRequired
import com.srt.service.CartService
import com.srt.service.vo.SrtSession
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/cart")
@RestController
class CartController(
    private val cartService: CartService,
) {
    @LoginRequired
    @GetMapping
    suspend fun getAllTickets(session: SrtSession): ResponseEntity<List<String>> {
        return ResponseEntity.ok(
            cartService.findAll(session.memberNumber),
        )
    }

    @LoginRequired
    @PostMapping
    suspend fun addTicket(ticket: String = "test", session: SrtSession) {
        cartService.addTicket(ticket, session.memberNumber)
    }

    @LoginRequired
    @DeleteMapping
    suspend fun deleteTicket(ticket: String = "test", session: SrtSession) {
        cartService.deleteTicket(ticket, session.memberNumber)
    }
}
