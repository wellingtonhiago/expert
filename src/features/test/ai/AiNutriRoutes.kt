package ai.features.test.ai

import ai.koog.agents.ext.agent.reActStrategy
import ai.koog.ktor.aiAgent
import ai.koog.ktor.llm
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.clients.openrouter.OpenRouterModels
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.aiNutriRoutesExample() {

    routing {
        route("/ai") {
            post("/chat") {
                val userInput = call.receiveText()
                // Create and run a default single‑run agent using a specific model
                val output = aiAgent(
                    strategy = reActStrategy(),
                    model = OpenAIModels.Chat.GPT4_1,
                    input = userInput
                )
                call.respond(HttpStatusCode.OK, output)
            }
        }
        post("/llm-chat") {
            val userInput = call.receiveText()

            val messages = llm().execute(
                prompt("chat") {
                    system("You are a helpful assistant that clarifies questions")
                    user(userInput)
                },
                GoogleModels.Gemini2_5Pro
            )

            // Join all assistant messages into a single string
            val text = messages.joinToString(separator = "") { it.content }
            call.respond(HttpStatusCode.OK, text)
        }

        get("/stream") {
            val flow = llm().executeStreaming(
                prompt = prompt(id = "streaming") { user("Stream this response, please") },
                model = OpenRouterModels.GPT4o
            )

            // Example: buffer and send as one chunk
            val sb = StringBuilder()
            flow.collect { chunk -> sb.append(chunk) }
            call.respondText(sb.toString())
        }

        post("/moderated-chat") {
            val userInput = call.receiveText()

            val moderation = llm().moderate(
                prompt("moderation") { user(userInput) },
                OpenAIModels.Moderation.Omni
            )

            if (moderation.isHarmful) {
                call.respond(HttpStatusCode.BadRequest, "Harmful content detected")
                return@post
            }

            val output = aiAgent(
                strategy = reActStrategy(),
                model = OpenAIModels.Chat.GPT4_1,
                input = userInput
            )
            call.respond(HttpStatusCode.OK, output)
        }
    }


}
