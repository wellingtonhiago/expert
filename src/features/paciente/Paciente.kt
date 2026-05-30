package ai.features.paciente

import java.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Paciente(
    val cpf: String,
    val nome: String,
    val objetivo: String,
    val createdAt: String = Instant.now().toString()
) {
    init {
        require(cpf.isNotBlank()) { "cpf do paciente não pode ser vazio" }
        require(nome.isNotBlank()) { "nome do paciente não pode ser vazio" }
    }
}
