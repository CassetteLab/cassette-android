package fr.cassette.cassette

import android.app.Application
import fr.cassette.cassette.core.di.coreModule
import fr.cassette.cassette.data.di.dataModule
import fr.cassette.cassette.domain.di.domainModule
import fr.cassette.cassette.presentation.core.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CassetteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@CassetteApplication)
            modules(
                coreModule,
                dataModule,
                domainModule,
                presentationModule
            )
        }
    }
}
