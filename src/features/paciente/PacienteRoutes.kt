package ai.features.paciente

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.pacienteRoutes() {
    val service by inject<PacienteService>()

    routing {
        route("/pacientes") {
            createPacienteRoute(service)
            listPacientesRoute(service)
            pacienteByIdRoute(service)
            pacientesByNameRoute(service)
            updatePacienteRoute(service)
            deletePacienteRoute(service)
        }
    }
}

private fun Route.createPacienteRoute(service: PacienteService) {
    post {
        val request = call.receive<Paciente>()
        val success = service.create(request)
        call.respond(HttpStatusCode.Created, success)
    }
}

private fun Route.listPacientesRoute(service: PacienteService) {
    get {
        val result = service.readAll()
        call.respond(HttpStatusCode.OK, result)
    }
}

private fun Route.pacienteByIdRoute(service: PacienteService) {
    get("/{id}") {
        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Id é obrigatório")
        service.readById(id)?.let { paciente ->
            call.respond(HttpStatusCode.OK, paciente)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}

// Fix: previously mapped to "/{nome}" which conflicted with "/{id}".
// Aligned with the nutricionista feature for consistency.
private fun Route.pacientesByNameRoute(service: PacienteService) {
    get("/by-name/{nome}") {
        val nome = call.parameters["nome"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Nome é obrigatório")
        val pacientes = service.readByName(nome)
        if (pacientes.isEmpty()) call.respond(HttpStatusCode.NoContent)
        else call.respond(HttpStatusCode.OK, pacientes)
    }
}

private fun Route.updatePacienteRoute(service: PacienteService) {
    put("/{id}") {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Id é obrigatório")
        service.update(id, call.receive<Paciente>())?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}

private fun Route.deletePacienteRoute(service: PacienteService) {
    delete("/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Id é obrigatório")
        service.delete(id)?.let {
            call.respond(HttpStatusCode.OK)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}
