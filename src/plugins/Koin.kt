package ai.plugins

import ai.di.MongoModule
import ai.di.NutriAiModule
import ai.di.NutricionistaModule
import br.com.expert.di.PacienteModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

fun Application.configureKoin() {
    // 1. Install Koin for Dependency Injection
    install(Koin) {
        SLF4JLogger()
        modules(
            MongoModule.mongoModule(this@configureKoin),
            NutricionistaModule.module,
            PacienteModule.module,
            NutriAiModule.module
        )
    }
}
