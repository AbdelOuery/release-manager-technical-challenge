package tech.challenge.release_manager.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import tech.challenge.release_manager.domain.exception.SystemVersionNotFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(e: SystemVersionNotFoundException) = mapOf("error" to e.message)
}