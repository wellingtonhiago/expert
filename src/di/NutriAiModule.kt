package ai.di

import ai.features.nutriai.NutriTools
import org.koin.dsl.module

object NutriAiModule {
    val module = module {
        single { NutriTools(get()) }
    }
}
