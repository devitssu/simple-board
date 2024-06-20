package com.teamsparta.simpleboard.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<MutableMap<String, Any>> {
        val body: MutableMap<String, Any> = mutableMapOf("code" to "400")
        val errors: MutableMap<String, String> = mutableMapOf()

        e.bindingResult.fieldErrors.forEach { fieldError ->
            errors[fieldError.field] = fieldError.defaultMessage ?: ""
        }

        body["errors"] = errors

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleHandlerMethodValidationException(e: HandlerMethodValidationException): ResponseEntity<MutableMap<String, Any>> {
        val body: MutableMap<String, Any> = mutableMapOf("code" to "400")
        val errors = e.allValidationResults.map { it.resolvableErrors }.flatten().map { it.defaultMessage }
        body["errors"] = errors
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body)
    }

    @ExceptionHandler(AlreadyExistException::class)
    fun handleAlreadyExistException(e: AlreadyExistException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse("409", e.message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("400", e.message))
    }
}