package fr.cassettelabs.cassette

import android.app.Application
import fr.cassettelabs.cassette.di.sharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class CassetteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CassetteApplication)
            modules(sharedModules())
        }
    }
}
