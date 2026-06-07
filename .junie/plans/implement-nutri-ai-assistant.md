---
sessionId: session-260606-173555-wkq3
---

# Spec-Driven Development (SDD) — Nutri AI Assistant

Este documento centraliza e acompanha o fluxo de Spec-Driven Development (SDD) para o desenvolvimento do **Nutri AI Assistant**.

---

## 📁 Arquivos Dedicados do SDD
Toda a especificação, planejamento e controle de execução da funcionalidade foram divididos de forma modular nos seguintes arquivos dedicados em `.junie/plans/implement-nutri-ai-assistant/`:
1. [Requisitos (`requirements.md`)](implement-nutri-ai-assistant/requirements.md): Detalhamento dos requisitos de negócio, histórias de usuário (User Stories) e critérios de aceitação.
2. [Plano de Implementação (`plan.md`)](implement-nutri-ai-assistant/plan.md): Estruturação da arquitetura técnica, divisão de fases, prioridades, análise de riscos e dependências.
3. [Lista de Tarefas (`tasks.md`)](implement-nutri-ai-assistant/tasks.md): Painel de controle detalhado com checkboxes e rastreabilidade para guiar a execução passo a passo.

---

## 🎯 Status Atual do Desenvolvimento
- **Fase Atual:** Planejamento e Especificação Concluídos (Aguardando Aprovação do Usuário).
- **Próximo Passo:** Obter a aprovação das especificações criadas em `requirements.md` e `plan.md` para iniciar a execução da **Fase 1** da lista de tarefas (`tasks.md`).

---

## 💡 Resumo da Arquitetura Proposta

O assistente nutricional inteligente utilizará a biblioteca **Koog** integrada de forma nativa ao ecossistema Ktor da aplicação:
- **Camada de Domínio / Serviço (`src/features/nutriai/`):**
  - `NutriAiService`: Inicialização do `AIAgent` configurado com a estratégia ReAct.
  - `@Tool getPatientGoals`: Função que recebe o CPF de um paciente e consulta seus objetivos diretamente do banco de dados MongoDB utilizando o `PacienteRepository`.
- **Injeção de Dependência (`NutriAiModule.kt`):**
  - Registro de escopo Koin para disponibilizar as dependências necessárias de IA.
- **Camada de Exposição REST (`src/features/nutriai/NutriAiRoutes.kt`):**
  - Endpoint `POST /nutri-assistant/chat` para intermediar conversas interativas de pacientes com o nutricionista inteligente.
- **Configurações Globais (`src/plugins/Routing.kt`):**
  - Registro dinâmico das novas rotas do assistente.

---

## 🚀 Etapas de Entrega de Alto Nível

### 1. Fase 1: Fundação & Serviço Core (Domínio)
- Criação do pacote `nutriai`.
- Implementação de `NutriAiService` e sua respectiva ferramenta de busca de metas.
- Configuração do módulo Koin (`NutriAiModule`).

### 2. Fase 2: Exposição HTTP & Rotas Ktor
- Implementação da rota `POST /nutri-assistant/chat`.
- Tratamento de cenários resilientes (ex: verificação da chave de API da IA).
- Integração da nova rota no plugin `Routing.kt`.

### 3. Fase 3: Testes, QA e Resiliência
- Testes unitários focando no comportamento do `AIAgent` e na resolução correta de suas ferramentas.
- Testes de integração utilizando `testApplication` do Ktor para o endpoint HTTP em cenários felizes e de exceção.