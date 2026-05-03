package br.com.expert.di

import ai.features.paciente.PacienteMongoRepository
import ai.features.paciente.PacienteRepository
import ai.features.paciente.PacienteService
import org.koin.dsl.bind
import org.koin.dsl.module

object PacienteModule {

    val module = module {
        single { PacienteMongoRepository(get()) } bind PacienteRepository::class
        single { PacienteService(get()) }
    }
}