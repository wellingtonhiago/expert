package ai.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        // Avoid noisy logs for static content
        filter { call ->
            val path = call.request.path()
            !path.startsWith("/static")
        }
    }
}
