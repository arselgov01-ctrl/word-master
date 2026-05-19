package com.wordmaster.app.ui.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
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
 * sized to the device width via Yandex's recommended sticky size,
 * with a small × button in the top-right corner that lets the user
 * hide the banner for the current screen session. State is local to
 * the Composable, so re-entering the screen brings the banner back.
 *
 * Pass [resetKey] to force the banner (and its dismissed state) to
 * re-create — e.g. tie it to the quiz screen identity so each new
 * quiz session starts with the banner visible.
 */
@Composable
fun YandexBanner(
    modifier: Modifier = Modifier,
    resetKey: Any? = Unit
) {
    var dismissed by remember(resetKey) { mutableStateOf(false) }
    if (dismissed) return

    val width = LocalConfiguration.current.screenWidthDp

    Box(modifier = modifier.fillMaxWidth()) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                createBanner(ctx, width).also { view ->
                    view.loadAd(AdRequest.Builder().build())
                }
            },
            update = { /* unit ad; nothing to update */ }
        )

        // Small × in the top-right corner to hide the banner.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { dismissed = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Скрыть рекламу",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
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
