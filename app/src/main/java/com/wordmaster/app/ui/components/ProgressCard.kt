package com.wordmaster.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.ui.theme.*

@Composable
fun ProgressCard(
    learned: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (total > 0) learned.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "progress"
    )

    val progressColor = when {
        progress < 0.25f -> ProgressBronze
        progress < 0.5f -> ProgressSilver
        progress < 0.75f -> ProgressGold
        else -> ProgressPlatinum
    }

    val rankTitle = when {
        progress < 0.1f -> "🌱 Новичок"
        progress < 0.25f -> "📖 Ученик"
        progress < 0.5f -> "📚 Знаток"
        progress < 0.75f -> "🎓 Эксперт"
        progress < 1f -> "👑 Мастер"
        else -> "🏆 Легенда"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BackgroundCard
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rankTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "$learned / $total",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Кастомный прогресс бар
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BackgroundCardLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = animatedProgress)
                        .clip(RoundedCornerShape(6.dp))
                        .background(progressColor)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(progress * 100).toInt()}% пройдено",
                fontSize = 13.sp,
                color = TextGray
            )
        }
    }
}
