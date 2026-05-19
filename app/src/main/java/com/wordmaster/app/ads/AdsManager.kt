package com.wordmaster.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader

/**
 * Owns the Yandex interstitial ad lifecycle for the whole app:
 * keeps a single [InterstitialAdLoader], preloads the next creative,
 * and exposes [onQuizAnswer] that decides — based on how many
 * questions/wrong answers the user has accumulated — when to show
 * the interstitial. Impressions are time-throttled so we never spam.
 *
 * A separate banner placement is wired directly from the Compose UI
 * via [com.wordmaster.app.ui.components.YandexBanner].
 */
class AdsManager(context: Context) {

    private val appContext = context.applicationContext
    private val loader: InterstitialAdLoader = InterstitialAdLoader(appContext).apply {
        setAdLoadListener(object : InterstitialAdLoadListener {
            override fun onAdLoaded(ad: InterstitialAd) {
                loadedAd = ad
                ad.setAdEventListener(adEventListener)
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                loadedAd = null
                Log.w(TAG, "Interstitial failed to load: ${error.code} ${error.description}")
            }
        })
    }

    private var loadedAd: InterstitialAd? = null
    private var lastShownAtMs: Long = 0L
    private var answeredSinceLastAd: Int = 0
    private var wrongSinceLastAd: Int = 0

    private val adEventListener = object : InterstitialAdEventListener {
        override fun onAdShown() {}
        override fun onAdFailedToShow(error: AdError) {
            Log.w(TAG, "Interstitial failed to show: ${error.description}")
            loadedAd = null
            requestNewAd()
        }
        override fun onAdDismissed() {
            loadedAd = null
            requestNewAd()
        }
        override fun onAdClicked() {}
        override fun onAdImpression(data: ImpressionData?) {}
    }

    fun preload() {
        if (loadedAd == null) requestNewAd()
    }

    private fun requestNewAd() {
        val config = AdRequestConfiguration.Builder(INTERSTITIAL_AD_UNIT_ID).build()
        loader.loadAd(config)
    }

    /**
     * Called from a quiz screen after the user has answered one card.
     * Tracks per-impression counters and shows the interstitial when
     * either threshold is hit ([ANSWERS_PER_AD] or [WRONG_PER_AD]) AND
     * at least [MIN_INTERVAL_MS] has elapsed since the previous one.
     * If no creative is currently loaded, kicks off a new request and
     * returns silently — the next eligible answer will retry.
     */
    fun onQuizAnswer(activity: Activity, wasCorrect: Boolean) {
        answeredSinceLastAd++
        if (!wasCorrect) wrongSinceLastAd++

        val now = System.currentTimeMillis()
        val enoughTime = now - lastShownAtMs >= MIN_INTERVAL_MS
        val thresholdHit =
            answeredSinceLastAd >= ANSWERS_PER_AD ||
                wrongSinceLastAd >= WRONG_PER_AD

        if (!thresholdHit || !enoughTime) return

        val ad = loadedAd
        if (ad == null) {
            requestNewAd()
            return
        }
        lastShownAtMs = now
        answeredSinceLastAd = 0
        wrongSinceLastAd = 0
        ad.show(activity)
    }

    fun destroy() {
        loadedAd?.setAdEventListener(null)
        loadedAd = null
        loader.setAdLoadListener(null)
    }

    companion object {
        private const val TAG = "AdsManager"

        const val INTERSTITIAL_AD_UNIT_ID = "R-M-19297771-1"
        const val BANNER_AD_UNIT_ID = "R-M-19297771-2"

        /** Number of answered cards (correct or wrong) that triggers an interstitial. */
        private const val ANSWERS_PER_AD = 25

        /** Number of wrong answers that triggers an interstitial (whichever hits first). */
        private const val WRONG_PER_AD = 4

        /** Minimum wall-clock interval between two consecutive impressions. */
        private const val MIN_INTERVAL_MS = 60_000L
    }
}
