package ai.features.paciente

/**
 * Business rules for [Paciente]. Depends only on the [PacienteRepository] port,
 * which makes it trivially unit-testable with an in-memory fake.
 */
class PacienteService(
    private val repository: PacienteRepository
) {
    suspend fun create(paciente: Paciente): Boolean =
        repository.insert(paciente)

    suspend fun readAll(): List<Paciente> =
        repository.findAll()

    suspend fun readById(id: String): Paciente? =
        repository.findById(id)

    suspend fun readByName(nome: String): List<Paciente> {
        require(nome.isNotBlank()) { "nome para busca não pode ser vazio" }
        return repository.findByName(nome)
    }

    suspend fun readByCpf(cpf: String): Paciente? {
        require(cpf.isNotBlank()) { "cpf para busca não pode ser vazio" }
        return repository.findByCpf(cpf)
    }

    suspend fun update(id: String, paciente: Paciente): Paciente? =
        repository.replace(id, paciente)

    suspend fun delete(id: String): Paciente? =
        repository.deleteById(id)
}
