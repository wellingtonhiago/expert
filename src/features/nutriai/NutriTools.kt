package ai.features.nutriai

import ai.features.paciente.PacienteService
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

/**
 * Koog [ToolSet] exposing patient nutrition data to the AI Agent.
 *
 * The agent decides when to call [buscarObjetivoPaciente] based on the user's message,
 * typically after the user provides a CPF.
 */
@LLMDescription("Ferramentas para consultar dados nutricionais de pacientes.")
class NutriTools(
    private val pacienteService: PacienteService
) : ToolSet {

    @Tool
    @LLMDescription(
        "Busca o objetivo nutricional de um paciente a partir do seu CPF. " +
            "Retorna o objetivo cadastrado (ex.: 'Ganho de massa') ou uma mensagem informando " +
            "que nenhum paciente foi encontrado para o CPF informado."
    )
    suspend fun buscarObjetivoPaciente(
        @LLMDescription("O CPF do paciente, apenas números, sem pontos ou traços.")
        cpf: String
    ): String {
        val paciente = pacienteService.readByCpf(cpf)
        return if (paciente != null) {
            "Paciente ${paciente.nome} (CPF ${paciente.cpf}) possui o objetivo: ${paciente.objetivo}."
        } else {
            "Nenhum paciente encontrado para o CPF $cpf."
        }
    }
}
