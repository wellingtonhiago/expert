package ai.di

import ai.features.nutricionista.NutricionistaMongoRepository
import ai.features.nutricionista.NutricionistaRepository
import ai.features.nutricionista.NutricionistaService
import org.koin.dsl.bind
import org.koin.dsl.module

object NutricionistaModule {
    val module = module {
        single { NutricionistaMongoRepository(get()) } bind NutricionistaRepository::class
        single { NutricionistaService(get()) }
    }
}
