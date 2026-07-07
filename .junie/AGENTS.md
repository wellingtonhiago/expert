# Guia de Desenvolvimento — Nutri-AI API

Este documento é o guia de referência para a Junie e para os desenvolvedores que trabalham no projeto **Nutri-AI API**. Ele descreve a stack, a arquitetura baseada em *features* do Ktor, como construir, rodar e testar a aplicação, como adicionar novas features e onde ficam os planos da Junie.

> **Idioma das instruções da Junie:** as instruções operacionais deste guia estão em português. Trechos de código, comandos e identificadores permanecem no idioma original do código-fonte.

---

## Visão Geral da Stack

O Nutri-AI API é um servidor **Ktor** (Netty) escrito em **Kotlin**, com:

- **Kotlin Toolchain** como ferramenta de build (`module.yaml` / `libs.versions.toml`).
- **MongoDB** como banco de dados, acessado via **driver coroutine** (assíncrono).
- **Koin** para injeção de dependências (Inversão de Controle).
- **Koog** (`$libs.koog.agents`) para a integração de IA (endpoint `/ai/chat`).
- **kotlinx.serialization** para JSON (Content Negotiation).

O ponto de entrada é `src/main.kt`, que sobe o servidor com `io.ktor.server.netty.EngineMain`. A ordem de inicialização dos módulos Ktor é definida em `resources/application.yaml`.

---

## Build & Configuração

A ferramenta de build é o **Kotlin Toolchain**.

### Pré-requisitos
- JDK 17 ou superior.

### Variáveis de Ambiente / Configuração
As credenciais do MongoDB devem ser fornecidas via variáveis de ambiente (ou em `resources/application.yaml`).

| Variável | Descrição |
| --- | --- |
| `DB_MONGO_USER` | Usuário do MongoDB. |
| `DB_MONGO_PASSWORD` | Senha do MongoDB. |
| `DB_MONGO_DATABASE_NAME` | (Opcional) Nome do banco; o padrão é `nutricao_db` em `application.yaml`. |
| `OPENAI_API_KEY` | Necessária **apenas** para a rota de IA (`POST /ai/chat`). Sem ela, a rota responde `503 Service Unavailable`. |

### Comandos Comuns
- **Build do projeto:**
  ```powershell
  .\kotlin.bat build
  ```
- **Rodar a aplicação:**
  ```powershell
  .\kotlin.bat run
  ```
- **Rodar os testes:**
  ```powershell
  .\kotlin.bat test
  ```

---

## Arquitetura baseada em features do Ktor

O projeto é organizado **por feature de negócio**, não por camada técnica global. Cada feature é autocontida e segue o fluxo de portas/adapters:

```
Routes  →  Service  →  Repository (porta / interface)  →  *MongoRepository (adapter MongoDB)
```

As features de referência são `paciente` e `nutricionista`, ambas em `src/features/`. Cada uma contém:

- **Modelo de domínio** (`@Serializable`) — ex.: `Paciente.kt`, `Nutricionista.kt`. A validação é feita no próprio construtor com `require(...)` (ex.: CPF/nome não podem ser vazios).
- **`*Repository.kt`** — define a **porta** (interface, ex.: `PacienteRepository`) e o **adapter** (ex.: `PacienteMongoRepository`, que usa a collection `pacientes` do MongoDB via driver coroutine).
- **`*Service.kt`** — camada de regras de negócio que orquestra o repositório.
- **`*Routes.kt`** — define as rotas. O `Service` é obtido do Koin via `inject<Service>()`, e as sub-rotas são funções de extensão privadas (ex.: `Route.createPacienteRoute(service)`), agrupadas sob um `route("/pacientes")`.

### Wiring (como tudo é conectado)

1. **Registro no Koin:** cada feature tem um módulo em `src/di/` (ex.: `PacienteModule`, `NutricionistaModule`, `MongoModule`) que registra repositório (com `bind` para a porta) e service:
   ```kotlin
   val module = module {
       single { PacienteMongoRepository(get()) } bind PacienteRepository::class
       single { PacienteService(get()) }
   }
   ```
2. **Instalação do Koin:** `src/plugins/Koin.kt` (`configureKoin`) instala o plugin e agrega todos os módulos.
3. **Agregação das rotas:** `src/plugins/Routing.kt` (`configureRouting`) é o ponto central que registra as rotas de cada feature:
   ```kotlin
   fun Application.configureRouting() {
       routing {
           get("/") { call.respondText("Nutri-AI API is running!") }
       }
       pacienteRoutes()
       nutricionistaRoutes()
       aiRoutesTest()
   }
   ```
4. **Ordem de inicialização:** definida em `resources/application.yaml`, na ordem
   `Serialization → StatusPages → Koin → MongoFactory → Routing`.

---

## Estrutura de Pastas

```
src/
├── main.kt                  # Ponto de entrada (EngineMain / Netty)
├── core/
│   └── exceptions/          # AppException (NotFound/BadRequest/Conflict) + ErrorResponse
├── di/                      # Módulos Koin (MongoModule, PacienteModule, NutricionistaModule)
├── features/                # Features de negócio (autocontidas)
│   ├── paciente/            # Paciente, PacienteRepository(+Mongo), PacienteService, PacienteRoutes
│   └── nutricionista/       # Nutricionista, ...Repository(+Mongo), ...Service, ...Routes
├── infrastructure/
│   └── MongoFactory.kt      # connectToMongoDB — conexão com o MongoDB
├── plugins/                 # Configuração do Ktor
│   ├── Serialization.kt     # configureSerialization (kotlinx JSON)
│   ├── StatusPages.kt       # configureStatusPages (mapeia AppException → HTTP)
│   ├── Koin.kt              # configureKoin (DI)
│   ├── Routing.kt           # configureRouting (agrega as rotas)
│   └── CallLogging.kt       # logging das requisições
└── example/
    └── AiRoutesTest.kt      # Exemplo de integração de IA (Koog) — rota POST /ai/chat
resources/
├── application.yaml         # Config Ktor + ordem dos módulos + db.mongo
└── logback.xml              # Configuração de logs
test/                        # Testes (unitários e de integração com testApplication)
```

---

## Como adicionar uma nova feature

Use `paciente` como referência. Para uma nova feature `exemplo`:

1. **Modelo de domínio:** crie `src/features/exemplo/Exemplo.kt` como `@Serializable`, validando os campos no construtor com `require(...)`.
2. **Repositório (porta + adapter):** crie `src/features/exemplo/ExemploRepository.kt` com a interface `ExemploRepository` e a implementação `ExemploMongoRepository(database: MongoDatabase)` usando a collection apropriada.
3. **Service:** crie `src/features/exemplo/ExemploService.kt` com as regras de negócio, recebendo o repositório por construtor.
4. **Rotas:** crie `src/features/exemplo/ExemploRoutes.kt` com `fun Application.exemploRoutes()`, injetando o service via `by inject<ExemploService>()` e agrupando as sub-rotas privadas sob `route("/exemplos")`.
5. **Módulo Koin:** crie `src/di/ExemploModule.kt` registrando o repositório (com `bind` para a porta) e o service; adicione-o em `plugins/Koin.kt`.
6. **Registro das rotas:** adicione `exemploRoutes()` em `plugins/Routing.kt` (`configureRouting`).

---

## Tratamento de Erros

Os erros de domínio são modelados em `src/core/exceptions/AppException.kt` como uma hierarquia `sealed`:

- `AppException.NotFound` → `404 Not Found`
- `AppException.BadRequest` → `400 Bad Request`
- `AppException.Conflict` → `409 Conflict`

Essas exceções são mapeadas para respostas HTTP em `src/plugins/StatusPages.kt` (`configureStatusPages`), retornando um payload `ErrorResponse`. Prefira lançar `AppException` nas camadas de service/repository em vez de responder erros manualmente nas rotas.

---

## Testes

Os testes são executados via Kotlin Toolchain (`.\kotlin.bat test`).

- **Testes unitários:** ficam em `test/`, usam anotações de `kotlin.test` e **não devem** depender de serviços externos (como o MongoDB).
- **Testes de integração:** usam `testApplication { ... }` do Ktor. Garanta dados mock ou um banco de teste configurado — testes que carregam a aplicação completa falham sem as credenciais do MongoDB.

### Exemplo simples de teste
Teste unitário para o modelo `Paciente`:

```kotlin
package ai.features.paciente

import kotlin.test.*

class PacienteTest {
    @Test
    fun `test valid paciente`() {
        val paciente = Paciente("12345678901", "John Doe", "Lose weight")
        assertEquals("12345678901", paciente.cpf)
        assertEquals("John Doe", paciente.nome)
    }

    @Test
    fun `test invalid paciente blank cpf`() {
        assertFailsWith<IllegalArgumentException> {
            Paciente("", "John Doe", "Lose weight")
        }
    }
}
```

---

## Planos da Junie

Os planos gerados pela Junie ficam armazenados em **`.junie/plans/`**.
> **Regra obrigatória:** todo plano da Junie **deve ser escrito em português**.

---

## Convenções & Notas

- **Estrutura por feature:** mantenha cada feature autocontida em `src/features/<feature>` (modelo, repository+adapter, service, routes).
- **Injeção de dependências:** sempre registre repositórios e services em um módulo Koin (`src/di/`) e instale-o em `plugins/Koin.kt`.
- **Acesso ao banco:** use sempre o **driver coroutine** do MongoDB (operações `suspend`).
- **Logs:** configurados em `resources/logback.xml`. Para depurar conexão com o MongoDB, verifique `infrastructure/MongoFactory.kt` e confirme a connection string do cluster Atlas.
- **Nota sobre inconsistência de pacotes (ponto de atenção):** alguns arquivos usam o pacote `ai.*` (ex.: `ai.di`, `ai.plugins`) e outros usam `br.com.expert.*` (ex.: `br.com.expert.di.PacienteModule`, `br.com.expert.plugins`). Isso é apenas documentado aqui — **não altere o código** para uniformizar sem solicitação explícita; ao criar novas features, prefira seguir o padrão `ai.*`.
