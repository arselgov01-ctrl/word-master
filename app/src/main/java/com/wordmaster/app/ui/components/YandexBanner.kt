package com.wordmaster.app.ui.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.wordmaster.app.ads.AdsManager
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

/**
 * Yandex sticky banner (R-M-19297771-2). Renders as an inline ad
 * sized to the device width via Yandex's recommended sticky size.
 *
 * The hosting Composable is responsible for placing this at the
 * bottom of its layout — we don't add internal padding/margins.
 */
@Composable
fun YandexBanner(modifier: Modifier = Modifier) {
    val width = LocalConfiguration.current.screenWidthDp

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            createBanner(ctx, width).also { view ->
                view.loadAd(AdRequest.Builder().build())
            }
        },
        update = { /* unit ad; nothing to update */ }
    )
}

private fun createBanner(context: Context, screenWidthDp: Int): BannerAdView =
    BannerAdView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        visibility = View.VISIBLE
        setAdUnitId(AdsManager.BANNER_AD_UNIT_ID)
        setAdSize(BannerAdSize.stickySize(context, screenWidthDp))
        setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {}
            override fun onAdFailedToLoad(error: AdRequestError) {}
            override fun onAdClicked() {}
            override fun onLeftApplication() {}
            override fun onReturnedToApplication() {}
            override fun onImpression(data: ImpressionData?) {}
        })
    }
