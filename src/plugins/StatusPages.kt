package ai.plugins

import ai.core.exceptions.AppException
import ai.core.exceptions.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AppException> { call, cause ->
            val status = when (cause) {
                is AppException.NotFound   -> HttpStatusCode.NotFound
                is AppException.BadRequest -> HttpStatusCode.BadRequest
                is AppException.Conflict   -> HttpStatusCode.Conflict
            }
            call.respond(status, ErrorResponse(status.value, status.description, cause.message ?: ""))
        }
        exception<IllegalArgumentException> { call, cause ->
            val status = HttpStatusCode.BadRequest
            call.respond(status, ErrorResponse(status.value, status.description, cause.message ?: "Invalid argument"))
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            val status = HttpStatusCode.InternalServerError
            call.respond(status, ErrorResponse(status.value, status.description, "Internal server error"))
        }
    }
}
