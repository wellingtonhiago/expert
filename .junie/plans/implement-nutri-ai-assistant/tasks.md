# Lista de Tarefas — Assistente de Nutrição com IA (Nutri AI Assistant)

> **Status:** Planejado (Aguardando Aprovação)  
> **Based on:** `plans/implement-nutri-ai-assistant/plan.md`  
> **Last updated:** 2026-06-07

---

<!-- Este arquivo é o painel de controle de execução para este recurso.
     Marque as tarefas com [x] conforme forem concluídas. Não delete tarefas. -->

---

## Fase 1 — Estruturação & Core Service (Camada de Domínio)

- [ ] **1.1** — Criar o diretório de funcionalidade `src/features/nutriai/` para centralizar a lógica.  
  _Plano: 1.1 | Req: REQ-1_

- [ ] **1.2** — Implementar a classe `NutriAiService` em `NutriAiService.kt`.  
  _Plano: 1.2 | Req: REQ-1_

- [ ] **1.3** — Definir a ferramenta (anotada com `@Tool` e `@LLMDescription`) `getPatientGoals` no `NutriAiService` recebendo `cpf: String` e consumindo dados de `PacienteRepository`.  
  _Plano: 1.3 | Req: REQ-2_

- [ ] **1.4** — Instanciar e expor o `AIAgent` configurado com a ferramenta `getPatientGoals`, prompt de sistema de nutricionista e o executor correspondente.  
  _Plano: 1.2 | Req: REQ-1_

- [ ] **1.5** — Criar o módulo Koin `NutriAiModule.kt` e registrar a injeção do `NutriAiService`.  
  _Plano: 1.4 | Req: REQ-1_

---

## Fase 2 — Exposição de API (Rotas & Integração com Ktor)

- [ ] **2.1** — Desenvolver o arquivo `NutriAiRoutes.kt` definindo o endpoint `POST /nutri-assistant/chat`.  
  _Plano: 2.1 | Req: REQ-3_

- [ ] **2.2** — Extrair parâmetros da requisição HTTP (incluindo validação de entrada de dados de texto ou JSON) no endpoint de chat.  
  _Plano: 2.3 | Req: REQ-4_

- [ ] **2.3** — Adicionar verificação de disponibilidade da IA (por exemplo, checar se a chave do modelo está configurada no ambiente) retornando status HTTP 503 caso indisponível.  
  _Plano: 2.3 | Req: REQ-4_

- [ ] **2.4** — Chamar o `AIAgent` de `NutriAiService` com a entrada do usuário e retornar as recomendações da IA.  
  _Plano: 2.1 | Req: REQ-3_

- [ ] **2.5** — Instalar e registrar as novas rotas do assistente em `src/plugins/Routing.kt`.  
  _Plano: 2.2 | Req: REQ-3_

---

## Fase 3 — Testes & Validação de Qualidade (QA)

- [ ] **3.1** — Implementar testes unitários para a ferramenta de busca de metas `getPatientGoals` simulando cenários onde o paciente existe ou não.  
  _Plano: 3.1 | Req: REQ-1, REQ-2_

- [ ] **3.2** — Criar testes de integração com `testApplication` para verificar se o endpoint de chat responde adequadamente aos requests válidos.  
  _Plano: 3.2 | Req: REQ-3_

- [ ] **3.3** — Implementar testes de fluxo negativo e resiliência (ex: requisições com dados em branco, chaves inválidas, simulação de erro no banco de dados).  
  _Plano: 3.3 | Req: REQ-4_

---

## Progresso

| Fase | Total | Concluído | Restante |
|-------|-------|------|-----------|
| Fase 1 | 5 | 0 | 5 |
| Fase 2 | 5 | 0 | 5 |
| Fase 3 | 3 | 0 | 3 |
| **Total** | **13** | **0** | **13** |
