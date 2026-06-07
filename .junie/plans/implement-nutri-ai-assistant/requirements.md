# Requisitos de Alto Nível — Assistente de Nutrição com IA (Nutri AI Assistant)

> **Feature slug:** `.junie/plans/implement-nutri-ai-assistant/`  
> **Status:** In Review  
> **Last updated:** 2026-06-07

---

## REQ-1: Integração com Koog & Agente de IA

- **User Story:**
  > Como um paciente, quero conversar com um assistente virtual inteligente para obter conselhos nutricionais baseados nas minhas metas pessoais de saúde.
- **Critérios de Aceitação:**
  - O sistema deve inicializar um `AIAgent` utilizando a biblioteca Koog.
  - O agente deve ser configurado com uma persona amigável, ética e profissional de nutricionista.
  - O agente deve ser capaz de receber mensagens e manter o fluxo da conversa aplicando as estratégias de raciocínio da biblioteca (como ReAct).

---

## REQ-2: Ferramenta de Consulta de Metas por CPF (Tooling)

- **User Story:**
  > Como o assistente de nutrição, quero consultar as metas e objetivos do paciente pelo seu CPF para personalizar meus conselhos nutricionais.
- **Critérios de Aceitação:**
  - O sistema deve disponibilizar uma ferramenta (anotada com `@Tool` e `@LLMDescription`) chamada `getPatientGoals`.
  - A ferramenta deve aceitar um CPF (String) como parâmetro e realizar a busca utilizando o `PacienteRepository`.
  - Se o paciente for encontrado, deve retornar seus objetivos e metas estruturados.
  - Se o paciente não for encontrado, deve retornar uma mensagem informando a ausência do registro para que a IA oriente o paciente de forma correta.

---

## REQ-3: API REST para Chat de Nutrição (Endpoints Ktor)

- **User Story:**
  > Como um integrador de sistema ou aplicativo de front-end, quero enviar mensagens de chat para o assistente de nutrição e receber as respostas geradas pela IA em tempo real.
- **Critérios de Aceitação:**
  - Deve ser exposta a rota `POST /nutri-assistant/chat` que aceita um payload JSON com a mensagem do usuário (e opcionalmente o CPF do paciente).
  - O endpoint deve injetar `NutriAiService` via Koin e encaminhar a mensagem para o agente.
  - A resposta deve ser retornada em formato JSON de forma limpa e estruturada.

---

## REQ-4: Resiliência, Robustez e Tratamento de Erros

- **User Story:**
  > Como operador do sistema, quero que o assistente trate cenários de falha (como falha na API de IA ou CPF não cadastrado) para evitar falhas silenciosas e instabilidade na aplicação.
- **Critérios de Aceitação:**
  - Caso a chave de API da IA não esteja configurada no ambiente (ex: `OPENAI_API_KEY` ausente), o endpoint deve responder com status `503 Service Unavailable` e uma mensagem clara de erro.
  - Entrada de usuários contendo dados inválidos ou CPFs incorretos deve ser validada e retornar status `400 Bad Request`.
  - As interações cruciais devem ser devidamente registradas em logs via Logback.
