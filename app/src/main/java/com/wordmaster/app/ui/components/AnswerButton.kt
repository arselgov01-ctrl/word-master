package com.wordmaster.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.ui.theme.*

@Composable
fun AnswerButton(
    answer: WordEntity,
    index: Int,
    isSelected: Boolean,
    isCorrectAnswer: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !isAnswered -> BackgroundCardLight
        isCorrectAnswer -> CorrectGreen.copy(alpha = 0.2f)
        isSelected -> WrongRed.copy(alpha = 0.2f)
        else -> BackgroundCardLight.copy(alpha = 0.5f)
    }

    val borderColor = when {
        !isAnswered -> TextMuted.copy(alpha = 0.3f)
        isCorrectAnswer -> CorrectGreen
        isSelected -> WrongRed
        else -> TextMuted.copy(alpha = 0.1f)
    }

    val textColor = when {
        !isAnswered -> TextWhite
        isCorrectAnswer -> CorrectGreen
        isSelected -> WrongRed
        else -> TextGray.copy(alpha = 0.5f)
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected || (isAnswered && isCorrectAnswer)) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val letters = listOf("A", "B", "C", "D", "E", "F")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isAnswered) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(
            width = if (isSelected || (isAnswered && isCorrectAnswer)) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when {
                    !isAnswered -> SecondaryDark
                    isCorrectAnswer -> CorrectGreen
                    isSelected -> WrongRed
                    else -> SecondaryDark.copy(alpha = 0.3f)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = letters.getOrElse(index) { "" },
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = answer.russian,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = if (isSelected || (isAnswered && isCorrectAnswer)) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            if (isAnswered) {
                AnimatedVisibility(
                    visible = isCorrectAnswer || isSelected,
                    enter = scaleIn() + fadeIn()
                ) {
                    Text(
                        text = if (isCorrectAnswer) "✓" else "✗",
                        color = if (isCorrectAnswer) CorrectGreen else WrongRed,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
