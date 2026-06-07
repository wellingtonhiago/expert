# Plano de Implementação — Assistente de Nutrição com IA (Nutri AI Assistant)

> **Status:** In Review  
> **Based on:** `plans/implement-nutri-ai-assistant/requirements.md`  
> **Last updated:** 2026-06-07

---

## Visão Geral

Este plano descreve a implementação do Assistente de Nutrição baseado em Inteligência Artificial utilizando a biblioteca **Koog**. O assistente interage de forma autônoma com os usuários, fornecendo recomendações personalizadas com base nos objetivos de saúde de cada paciente. A personalização é alcançada através de uma ferramenta de busca dinâmica de metas por meio do CPF do paciente (Tooling), que consome dados diretamente do MongoDB via `PacienteRepository`.

---

## Dependências & Riscos

Antes do início do desenvolvimento, as seguintes dependências e riscos foram identificados:

| Item | Tipo | Notas |
|------|------|-------|
| Biblioteca Koog | Dependência | Necessária para orquestrar o agente de IA, estratégias (ReAct) e o registro das ferramentas (`@Tool`). |
| Chave da API da OpenAI | Dependência | O sistema exige a variável de ambiente `OPENAI_API_KEY` para que o modelo de LLM possa ser executado com sucesso. |
| Estrutura de Pacientes | Dependência | Depende do `PacienteRepository` e da coleção de pacientes preenchida no MongoDB para validações de teste. |
| Latência da API de IA | Risco | Chamadas externas ao LLM podem introduzir latência. O endpoint deve ser otimizado e monitorado. |

---

## Fases de Implementação

As tarefas de desenvolvimento estão organizadas em fases lógicas e sequenciais para garantir entregabilidade contínua e testabilidade.

### Fase 1 — Estruturação & Core Service (Camada de Domínio)

Nesta fase, criamos as estruturas básicas do domínio de NutriAI, as ferramentas de busca por CPF e o serviço central que integra com o Koog.

| # | Item | Prioridade | Requisito(s) |
|---|------|----------|----------------|
| 1.1 | Criar pacote `src/features/nutriai/` | Alta | REQ-1 |
| 1.2 | Implementar o `NutriAiService.kt` com suporte a `AIAgent` e ferramenta `@Tool` | Alta | REQ-1, REQ-2 |
| 1.3 | Desenvolver a função de ferramenta `getPatientGoals(cpf: String)` integrada ao `PacienteRepository` | Alta | REQ-2 |
| 1.4 | Configurar o módulo de injeção de dependências do Koin `NutriAiModule.kt` | Alta | REQ-1 |

---

### Fase 2 — Exposição de API (Rotas & Integração com Ktor)

Esta fase expõe o serviço de assistente de nutrição por meio de endpoints HTTP e integra o novo fluxo de rotas no roteamento global da aplicação.

| # | Item | Prioridade | Requisito(s) |
|---|------|----------|----------------|
| 2.1 | Criar `NutriAiRoutes.kt` contendo o endpoint `POST /nutri-assistant/chat` | Alta | REQ-3, REQ-4 |
| 2.2 | Integrar as novas rotas em `src/plugins/Routing.kt` | Alta | REQ-3 |
| 2.3 | Adicionar tratamento de indisponibilidade da API de IA e validações de payload | Média | REQ-4 |

---

### Fase 3 — Testes & Validação de Qualidade (QA)

Fase crucial para garantir que as rotas Ktor se comportam como esperado em cenários felizes e de erro, e que o agente executa a busca de metas por CPF corretamente.

| # | Item | Prioridade | Requisito(s) |
|---|------|----------|----------------|
| 3.1 | Criar testes unitários para a lógica do `NutriAiService` simulando a chamada da ferramenta | Alta | REQ-1, REQ-2 |
| 3.2 | Criar testes de integração utilizando `testApplication` do Ktor para o endpoint de chat | Alta | REQ-3, REQ-4 |
| 3.3 | Validar cenários de erro (ex: CPF ausente ou inválido, serviço de IA offline) | Média | REQ-4 |

---

## Checklist de Cobertura

Antes de marcar este plano como Aprovado, confirme:

- [x] Cada requisito no `requirements.md` está mapeado para pelo menos um item do plano.
- [x] As fases de desenvolvimento estão em ordem de dependência lógica.
- [x] Os riscos e dependências técnicas críticas foram identificados e documentados.
- [x] Nenhuma implementação direta de código foi misturada ao plano — mantendo o foco em metas de arquitetura e entregáveis.
