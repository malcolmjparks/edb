package com.eldritch.helpers.campaignmanagementservice.exceptions

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.Date

/**
 * JSON object returned when an exception is thrown and handled by [ControllerExceptionHandler].
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponseDTO @JvmOverloads constructor(
        val timestamp: Date = Date(),
        val status: Int,
        val error: String,
        val message: String,
        val path: String
)
