package ai.features.nutricionista

class NutricionistaService(
    private val repository: NutricionistaRepository
) {
    suspend fun create(nutricionista: Nutricionista) {
        repository.insert(nutricionista)
    }

    suspend fun readAll(): List<Nutricionista> =
        repository.findAll()

    suspend fun readById(id: String): Nutricionista? =
        repository.findById(id)

    suspend fun readByName(nome: String): List<Nutricionista> {
        require(nome.isNotBlank()) { "nome para busca não pode ser vazio" }
        return repository.findByName(nome)
    }

    suspend fun updateById(id: String, nutricionista: Nutricionista): Nutricionista? {
        require(id.isNotBlank()) { "id para atualização não pode ser vazio" }
        return repository.replace(id, nutricionista)
    }

    suspend fun deleteById(id: String): Nutricionista? =
        repository.deleteById(id)
}
