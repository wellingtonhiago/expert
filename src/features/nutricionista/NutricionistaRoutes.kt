package ai.features.nutricionista

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

fun Application.nutricionistaRoutes() {
    val service by inject<NutricionistaService>()

    routing {
        route("/nutricionistas") {
            createNutricionistaRoute(service)
            listNutricionistasRoute(service)
            nutricionistaByIdRoute(service)
            nutricionistasByNameRoute(service)
            updateNutricionistaRoute(service)
            deleteNutricionistaRoute(service)
        }
    }
}

private fun Route.createNutricionistaRoute(service: NutricionistaService) {
    post {
        val request = call.receive<Nutricionista>()
        service.create(request)
        call.respond(HttpStatusCode.Created)
    }
}

private fun Route.listNutricionistasRoute(service: NutricionistaService) {
    get {
        val result = service.readAll()
        if (result.isEmpty()) call.respond(HttpStatusCode.NoContent)
        else call.respond(HttpStatusCode.OK, result)
    }
}

private fun Route.nutricionistaByIdRoute(service: NutricionistaService) {
    get("/{id}") {
        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Id é obrigatório")
        service.readById(id)?.let { n ->
            call.respond(HttpStatusCode.OK, n)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}

private fun Route.nutricionistasByNameRoute(service: NutricionistaService) {
    get("/by-name/{nome}") {
        val nome = call.parameters["nome"] ?: return@get call.respond(HttpStatusCode.BadRequest, "nome é obrigatório")
        val nutricionistasByName = service.readByName(nome)
        if (nutricionistasByName.isEmpty()) call.respond(HttpStatusCode.NoContent)
        else call.respond(HttpStatusCode.OK, nutricionistasByName)
    }
}

private fun Route.updateNutricionistaRoute(service: NutricionistaService) {
    put("/{id}") {
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "Id é obrigatório")
        val body = call.receive<Nutricionista>()
        service.updateById(id, body)?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}

private fun Route.deleteNutricionistaRoute(service: NutricionistaService) {
    delete("/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Id é obrigatório")
        service.deleteById(id)?.let {
            call.respond(HttpStatusCode.OK)
        } ?: call.respond(HttpStatusCode.NotFound)
    }
}
