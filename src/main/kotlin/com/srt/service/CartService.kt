package com.srt.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class CartService(
    private val stringRedisTemplate: StringRedisTemplate,
) {
    // TODO: 티켓을 예약하기 위해 필요한 정보: StlbTrnClsfCd, TrnGpCd, DptRsStnCd, ArvRsStnCd, DptDt, DptTm, TrnNo
    fun findAll(memberNumber: String): List<String> {
        val key = generateTicketListKey(memberNumber)

        return stringRedisTemplate.opsForList().range(key, 0, -1) ?: emptyList()
    }

    fun addTicket(ticket: String, memberNumber: String) {
        val key = generateTicketListKey(memberNumber)

        stringRedisTemplate.execute { connection ->
            connection.multi()

            connection.listCommands().rPush(key.toByteArray(), ticket.toByteArray())
            connection.keyCommands().expire(key.toByteArray(), TICKET_LIST_TTL)

            connection.exec()
        }
    }

    fun deleteTicket(ticket: String, memberNumber: String) {
        val key = generateTicketListKey(memberNumber)

        stringRedisTemplate.execute { connection ->
            connection.multi()

            connection.listCommands().lRem(key.toByteArray(), 1, ticket.toByteArray())
            connection.keyCommands().expire(key.toByteArray(), TICKET_LIST_TTL)

            connection.exec()
        }
    }

    fun purchase(memberNumber: String) {
        TODO()
    }

    private fun generateTicketListKey(memberNumber: String): String {
        return "ticket-list:$memberNumber"
    }

    companion object {
        private const val TICKET_LIST_TTL = 3600L
    }
}
