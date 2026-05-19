package com.wordmaster.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.ui.components.SpeakerButton
import com.wordmaster.app.ui.theme.*

@Composable
fun WordCard(
    word: WordEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardGradientStart)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(CardGradientStart, CardGradientEnd)
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Категория
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TextWhite.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = getCategoryLabel(word.category),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = TextWhite.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Английское слово + кнопка озвучивания
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = word.english.uppercase(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SpeakerButton(text = word.english, tint = TextWhite)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Информация о прогрессе
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (word.streak > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CorrectGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🔥 ${word.streak}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = CorrectGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    val progressText = when {
                        word.correctCount == 0 && word.wrongCount == 0 -> "Новое слово"
                        else -> "✓${word.correctCount} ✗${word.wrongCount}"
                    }

                    Text(
                        text = progressText,
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }

                // Индикатор до выучивания
                if (word.streak < 5) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        if (index < word.streak) CorrectGreen
                                        else TextMuted.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }
                    Text(
                        text = "ещё ${5 - word.streak} до запоминания",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun getCategoryLabel(category: String): String {
    return when (category) {
        "verbs" -> "🏃 Глагол"
        "nouns" -> "📦 Существительное"
        "adjectives" -> "🎨 Прилагательное"
        "adverbs" -> "⚡ Наречие"
        "prepositions" -> "🔗 Предлог/Союз"
        "pronouns" -> "👤 Местоимение"
        "numbers" -> "🔢 Число"
        "abstract" -> "💭 Абстрактное"
        else -> "📝 Общее"
    }
}
