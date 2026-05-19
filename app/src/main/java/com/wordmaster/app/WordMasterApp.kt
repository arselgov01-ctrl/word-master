package com.wordmaster.app

import android.app.Application
import com.wordmaster.app.ads.AdsManager
import com.wordmaster.app.data.SentenceRepository
import com.wordmaster.app.data.WordDatabase
import com.wordmaster.app.data.WordRepository
import com.wordmaster.app.settings.SettingsManager
import com.wordmaster.app.util.TtsManager
import com.yandex.mobile.ads.common.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WordMasterApp : Application() {
    val database by lazy { WordDatabase.getDatabase(this) }
    val repository by lazy { WordRepository(database.wordDao()) }
    val sentenceRepository by lazy { SentenceRepository(database.sentenceDao()) }
    val ttsManager by lazy { TtsManager(this) }
    val settingsManager by lazy { SettingsManager(this) }
    val adsManager by lazy { AdsManager(this) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Eagerly initialize TTS so the engine has time to warm up before first use.
        ttsManager
        // Yandex Mobile Ads init + interstitial preload.
        MobileAds.initialize(this) {
            adsManager.preload()
        }
        // Keep TTS speech rate in sync with user settings.
        appScope.launch {
            settingsManager.settings
                .map { it.ttsSpeed }
                .distinctUntilChanged()
                .onEach { ttsManager.setSpeechRate(it) }
                .collect {}
        }
    }

    override fun onTerminate() {
        ttsManager.shutdown()
        adsManager.destroy()
        super.onTerminate()
    }
}
