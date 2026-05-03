package ai.features.test.ai

import ai.koog.agents.core.agent.AIAgent

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.tool
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.params.LLMParams
import kotlinx.serialization.Serializable


suspend fun main() {
//    exampleOne()
//    exampleTwo()
    exampleThree()

}

suspend fun exampleOne() {
    val client = OpenAILLMClient(apiKey = System.getenv("OPENAI_API_KEY"))
    val model = OpenAIModels.CostOptimized.GPT4oMini
    val userMessage = readln()

    val prompt = prompt(
        id = "translation-request",
        params = LLMParams(
            temperature = 0.7,
            maxTokens = 100
        )
    ) {
        user("traduza essa mensagem para inglês: $userMessage")
    }

    val response = client.execute(prompt, model)
    with(response.first()) {
        println("Resposta do modelo traduzida do inglês é: $content")
        println("Mera info: $metaInfo")
    }
}

suspend fun exampleTwo() {
    val client = OpenAILLMClient(apiKey = System.getenv("OPENAI_API_KEY"))
    val model = OpenAIModels.CostOptimized.GPT4oMini
    val userMessage = readln()

    val prompt = prompt(
        id = "tool-by-hand",
        params = LLMParams(temperature = 0.7, maxTokens = 100)
    ) {
        system(
            """
       You're a banking assistant. You can send money by writing the following JSON:
       {
           "name": "send_money",
           "params": {
               "recipient": <recipient_name>,
               "amount": <amount_in_euros>,
               "purpose": <purpose_of_the_transaction>
           }
       }
   """.trimIndent()
        )
        user("Send 20 euros to Daniel for dinner at the restaurant")
    }
}

suspend fun exampleThree() {
//            val client = OpenAILLMClient(apiKey = System.getenv("OPENAI_API_KEY"))
//            val executor = SingleLLMPromptExecutor(llmClient = client)

    //Mesmo código que o de cima
    val executor =
        simpleOpenAIExecutor(apiToken = System.getenv("OPENAI_API_KEY"))

    val model = OpenAIModels.CostOptimized.GPT4oMini

    val toolRegistry = ToolRegistry {
        tool(::sendMoney)
    }
    val agent = AIAgent(
        promptExecutor = executor,
        llmModel = model,
        toolRegistry = toolRegistry,
        systemPrompt = "You're a banking assistant. Accompany the user with their request."
    ) {
        handleEvents {
            onBeforeLLMCall { ctx ->
                println("Request to LLM:")
                println("    # Messages:")
                ctx.prompt.messages.forEach { println("    $it") }
                println("    # Tools:")
                ctx.tools.forEach { println("    $it") }
            }
            onAfterLLMCall { ctx ->
                println("LLM response:")
                ctx.responses.forEach { println("    $it") }
            }
        }
    }

    val userMessage = "Send 25 euros to Daniel for dinner at the restaurant."
    val result = agent.run(userMessage)
    println("Final result:")
    println(result)
}


@Tool
@LLMDescription("Transfers a specified amount in euros to a recipient.")
fun sendMoney(
    @LLMDescription("The amount in euros to be transferred.")
    amount: Int,
    @LLMDescription("The name of the recipient who will receive the money.")
    recipient: String,
    @LLMDescription("A brief description or reason for the transaction.")
    purpose: String
): String {
    println("=======")
    println("Sending $amount EUR to $recipient with the purpose: \"$purpose\".")
    println("Please confirm the transaction by entering 'yes' or 'no'.")
    println("=======")
    // Sending money
    val confirmation = readln()
    return if (confirmation.lowercase() in setOf("yes", "y"))
        "Money was sent."
    else
        "Money transfer wasn't confirmed by the user."
}

@Serializable
data class Contact(val id: Int, val name: String, val lastName: String? = null, val phoneNumber: String? = null)

