package ag.android.moviewatchlist

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/*
    @HiltAndroidApp = Global dependency container so all objects can be injected across the app

    Application class is a lifecycle-aware place to store dependencies globally
 */
@HiltAndroidApp
class MovieWatchlistApp : Application()