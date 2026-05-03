package ai.di

import ai.infrastructure.connectToMongoDB
import io.ktor.server.application.Application
import org.koin.dsl.module

object MongoModule {
    fun mongoModule(application: Application) = module {

        single { application.connectToMongoDB() }
    }
}
