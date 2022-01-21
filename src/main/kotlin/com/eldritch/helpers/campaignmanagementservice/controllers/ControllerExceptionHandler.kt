package com.eldritch.helpers.campaignmanagementservice.controllers

import com.eldritch.helpers.campaignmanagementservice.exceptions.CampaignRunningException
import com.eldritch.helpers.campaignmanagementservice.exceptions.ErrorResponseDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler
    fun handleCampaignRunningException(request: ServletWebRequest, ex: CampaignRunningException, response: HttpServletResponse): ResponseEntity<ErrorResponseDTO> {
        logger.warn(ex.message)
        return createResponseEntity(request.request.requestURI, HttpStatus.NOT_MODIFIED, ex)
    }

    private fun createResponseEntity(path: String, httpStatus: HttpStatus, ex: Throwable): ResponseEntity<ErrorResponseDTO> {
        return ResponseEntity(
                ErrorResponseDTO(
                        status = httpStatus.value(),
                        error = httpStatus.reasonPhrase,
                        message = ex.message ?: EXCEPTION_NO_MESSAGE,
                        path = path
                ), httpStatus)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)
        private const val EXCEPTION_NO_MESSAGE = "An unknown error occurred."
    }

}
