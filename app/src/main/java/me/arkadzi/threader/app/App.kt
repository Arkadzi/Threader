package me.arkadzi.threader.app

import android.app.Application
import me.arkadzi.threader.data.OkPageLoader
import me.arkadzi.threader.domain.processing.BfsPageAnalyzer
import me.arkadzi.threader.presentation.features.main.MainActivity
import me.arkadzi.threader.domain.processing.PageLoader
import me.arkadzi.threader.presentation.features.main.IMainPresenter
import me.arkadzi.threader.presentation.features.main.MainPresenter
import me.arkadzi.threader.presentation.utils.MessageHandler
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class App : Application() {
    val appModule = module {
        single<PageLoader> { OkPageLoader() }
        single { BfsPageAnalyzer(get()) }
        single { MessageHandler(get()) }
        scope(named<MainActivity>()) {
            scoped<IMainPresenter> { MainPresenter(get(), get()) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}