package ai.features.nutriai

import ai.features.paciente.Paciente
import ai.features.paciente.PacienteRepository
import ai.features.paciente.PacienteService
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import io.ktor.server.application.install
import kotlin.test.*

/**
 * Fake in-memory [PacienteRepository] so the route can be exercised without MongoDB.
 */
private class FakePacienteRepository(
    private val pacientes: List<Paciente>
) : PacienteRepository {
    override suspend fun insert(paciente: Paciente): Boolean = true
    override suspend fun findAll(): List<Paciente> = pacientes
    override suspend fun findById(id: String): Paciente? = null
    override suspend fun findByName(nome: String): List<Paciente> = pacientes.filter { it.nome == nome }
    override suspend fun findByCpf(cpf: String): Paciente? = pacientes.firstOrNull { it.cpf == cpf }
    override suspend fun replace(id: String, paciente: Paciente): Paciente? = null
    override suspend fun deleteById(id: String): Paciente? = null
}

class NutriAiTest {

    /**
     * When the Koog plugin cannot be used (no OPENAI_API_KEY), the endpoint must respond with
     * 503 Service Unavailable instead of failing or returning 404. This keeps the test
     * deterministic and free of external dependencies (no network, no MongoDB).
     */
    @Test
    fun `nutri chat returns 503 when koog is not configured`() = testApplication {
        // The route only reaches the Koog agent when OPENAI_API_KEY is set; without it we expect 503.
        assumeKoogUnavailable()

        val paciente = Paciente("12345678901", "John Doe", "Ganho de massa")
        val testModule = module {
            single<PacienteService> { PacienteService(FakePacienteRepository(listOf(paciente))) }
            single { NutriTools(get()) }
        }

        application {
            install(Koin) {
                modules(testModule)
            }
            nutriAiRoutes()
        }

        val response = client.post("/nutri/chat") {
            setBody("Meu CPF é 12345678901, o que devo comer?")
        }

        assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
        assertTrue(response.bodyAsText().contains("Koog", ignoreCase = true))
    }

    private fun assumeKoogUnavailable() {
        val apiKey = System.getenv("OPENAI_API_KEY")
        assertTrue(
            apiKey.isNullOrBlank(),
            "This test only runs when OPENAI_API_KEY is not set (Koog disabled)."
        )
    }
}
