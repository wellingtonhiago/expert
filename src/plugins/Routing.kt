package ai.plugins

import ai.example.aiRoutesTest
import ai.features.nutriai.nutriAiRoutes
import ai.features.nutricionista.nutricionistaRoutes
import ai.features.paciente.pacienteRoutes
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Central place that wires every feature's routes.
 * Adding a new feature means creating its `*Routes.kt` and adding one line here.
 */
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Nutri-AI API is running!")
        }
    }
    pacienteRoutes()
    nutricionistaRoutes()
    aiRoutesTest()
    nutriAiRoutes()
}
