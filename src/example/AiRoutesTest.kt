package ai.example

import ai.koog.ktor.aiAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.aiRoutesTest() {
    // Register /ai routes even when Koog is not installed, so the API surface stays stable
    // and returns a meaningful error instead of a 404.
    routing {
        route("/ai") {
            post("/chat") {
                if (!isKoogAvailable()) {
                    call.respond(HttpStatusCode.ServiceUnavailable, "Koog is not configured (missing OPENAI_API_KEY)")
                    return@post
                }
                val userInput = call.receive<String>()
                val output = aiAgent(userInput, model = OpenAIModels.Chat.GPT4_1)
                call.respondText(output)
            }
        }
    }
}

/** `true` when the Koog plugin can be used in the current runtime (API key present). */
fun isKoogAvailable(): Boolean = !System.getenv("OPENAI_API_KEY").isNullOrBlank()

