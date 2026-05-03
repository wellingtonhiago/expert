package ai.features.nutricionista

import java.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Nutricionista(
    val nome: String,
    val crn: String,
    val createdAt: String = Instant.now().toString()
) {
    init {
        require(nome.isNotBlank()) { "nome do nutricionista não pode ser vazio" }
        require(crn.isNotBlank()) { "crn do nutricionista não pode ser vazio" }
    }
}
