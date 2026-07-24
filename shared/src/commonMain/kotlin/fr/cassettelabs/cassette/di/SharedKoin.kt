package fr.cassettelabs.cassette.di

import fr.cassettelabs.cassette.core.di.coreModule
import fr.cassettelabs.cassette.data.di.dataModule
import fr.cassettelabs.cassette.domain.di.domainModule
import fr.cassettelabs.cassette.presentation.di.presentationModule
import org.koin.core.module.Module

fun sharedModules(): List<Module> =
    listOf(
        coreModule,
        domainModule,
        dataModule,
        presentationModule,
    )
