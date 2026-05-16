package com.wordmaster.app

import android.app.Application
import com.wordmaster.app.data.SentenceRepository
import com.wordmaster.app.data.WordDatabase
import com.wordmaster.app.data.WordRepository
import com.wordmaster.app.util.TtsManager

class WordMasterApp : Application() {
    val database by lazy { WordDatabase.getDatabase(this) }
    val repository by lazy { WordRepository(database.wordDao()) }
    val sentenceRepository by lazy { SentenceRepository(database.sentenceDao()) }
    val ttsManager by lazy { TtsManager(this) }

    override fun onCreate() {
        super.onCreate()
        // Eagerly initialize TTS so the engine has time to warm up before first use.
        ttsManager
    }

    override fun onTerminate() {
        ttsManager.shutdown()
        super.onTerminate()
    }
}
