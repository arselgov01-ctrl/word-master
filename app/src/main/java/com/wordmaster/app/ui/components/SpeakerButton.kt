package com.wordmaster.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordmaster.app.WordMasterApp
import com.wordmaster.app.ui.theme.ButtonBlue

@Composable
fun SpeakerButton(
    text: String,
    modifier: Modifier = Modifier,
    tint: Color = ButtonBlue,
    size: Dp = 40.dp
) {
    val context = LocalContext.current
    val tts = (context.applicationContext as WordMasterApp).ttsManager
    IconButton(
        onClick = { tts.speak(text) },
        modifier = modifier.size(size)
    ) {
        Icon(
            imageVector = Icons.Filled.VolumeUp,
            contentDescription = "Озвучить",
            tint = tint
        )
    }
}
