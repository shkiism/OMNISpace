package amirparsa.shk.omnispace

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OmniApp : Application() {

    companion object {
        lateinit var instance: OmniApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
