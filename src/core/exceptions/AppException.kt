package ai.core.exceptions

/**
 * Base exception hierarchy for domain/business errors.
 * Mapped to HTTP responses in [br.com.expert.plugins.configureStatusPages].
 */
sealed class AppException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    class NotFound(message: String) : AppException(message)
    class BadRequest(message: String) : AppException(message)
    class Conflict(message: String) : AppException(message)
}
