package ai.features.paciente

import kotlinx.coroutines.runBlocking
import kotlin.test.*

/**
 * Fake in-memory [PacienteRepository] so the service can be unit-tested without MongoDB.
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

class PacienteCpfTest {

    private val paciente = Paciente("12345678901", "John Doe", "Ganho de massa")
    private val service = PacienteService(FakePacienteRepository(listOf(paciente)))

    @Test
    fun `readByCpf returns the matching paciente`() = runBlocking {
        val result = service.readByCpf("12345678901")
        assertNotNull(result)
        assertEquals("John Doe", result.nome)
        assertEquals("Ganho de massa", result.objetivo)
    }

    @Test
    fun `readByCpf returns null when no paciente matches`() = runBlocking {
        val result = service.readByCpf("00000000000")
        assertNull(result)
    }

    @Test
    fun `readByCpf rejects blank cpf`() {
        assertFailsWith<IllegalArgumentException> {
            runBlocking { service.readByCpf("") }
        }
    }
}
