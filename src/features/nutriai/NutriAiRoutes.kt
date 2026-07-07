package ai.features.nutriai

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.agent.reActStrategy
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
import org.koin.ktor.ext.inject

/**
 * System prompt that guides the nutrition assistant. It instructs the agent to ask for the
 * patient's CPF when it is missing and to use the [NutriTools] tool to fetch the goal before
 * suggesting foods.
 */
private const val SYSTEM_PROMPT = """
Você é um assistente de nutrição. Siga estas regras:
1. Se o usuário não informar o CPF do paciente, peça o CPF antes de qualquer sugestão.
2. Quando o CPF for informado, use a ferramenta buscarObjetivoPaciente para obter o objetivo do paciente.
3. Se nenhum paciente for encontrado para o CPF, informe isso claramente ao usuário.
4. Com base no objetivo do paciente, sugira alimentos e refeições apropriados.
Responda sempre em português.
"""

fun Application.nutriAiRoutes() {
    val nutriTools by inject<NutriTools>()

    // Register /nutri routes even when Koog is not installed, so the API surface stays stable
    // and returns a meaningful error instead of a 404.
    routing {
        route("/nutri") {
            post("/chat") {
                if (!isKoogAvailable()) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        "Koog is not configured (missing OPENAI_API_KEY)"
                    )
                    return@post
                }

                val userInput = call.receive<String>()
                val toolRegistry = ToolRegistry {
                    tools(nutriTools.asTools())
                }

                val input = "$SYSTEM_PROMPT\n\nMensagem do usuário: $userInput"
                val agent = aiAgent(
                    strategy = reActStrategy(),
                    model = OpenAIModels.Chat.GPT4_1,
                    tools = toolRegistry
                )
                val output = agent.run(input)
                call.respondText(output)
            }
        }
    }
}

/** `true` when the Koog plugin can be used in the current runtime (API key present). */
private fun isKoogAvailable(): Boolean = !System.getenv("OPENAI_API_KEY").isNullOrBlank()
