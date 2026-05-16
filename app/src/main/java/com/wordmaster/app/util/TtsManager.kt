package com.wordmaster.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class TtsManager(context: Context) {

    private val ready = AtomicBoolean(false)
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val engine = tts ?: return@TextToSpeech
                val result = engine.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.w(TAG, "English TTS not supported on this device")
                    ready.set(false)
                } else {
                    ready.set(true)
                }
            } else {
                Log.w(TAG, "TTS init failed: $status")
                ready.set(false)
            }
        }
    }

    fun isReady(): Boolean = ready.get()

    fun speak(text: String) {
        if (text.isBlank()) return
        val engine = tts ?: return
        if (!ready.get()) return
        engine.stop()
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready.set(false)
    }

    companion object {
        private const val TAG = "TtsManager"
    }
}
