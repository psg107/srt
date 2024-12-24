package com.srt.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(
        InvalidTokenException::class,
        AuthorizationFailedException::class,
    )
    fun handleUnauthorizedException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        log.error(ex.message, ex)
        return handleExceptionInternal(
            ex,
            ExceptionView(
                message = ex.message.orEmpty(),
                status = HttpStatus.UNAUTHORIZED,
            ),
            HttpHeaders.EMPTY,
            HttpStatus.UNAUTHORIZED,
            request,
        )!!
    }

    @ExceptionHandler(Exception::class)
    fun handleDefaultException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        log.error(ex.message, ex)
        return handleExceptionInternal(
            ex,
            ExceptionView(
                message = "서비스 호출 중 오류가 발생했습니다.",
                status = HttpStatus.INTERNAL_SERVER_ERROR,
            ),
            HttpHeaders.EMPTY,
            HttpStatus.INTERNAL_SERVER_ERROR,
            request,
        )!!
    }
}

data class ExceptionView(
    val message: String,
    val status: HttpStatus,
)
