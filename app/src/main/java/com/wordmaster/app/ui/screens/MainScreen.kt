package com.wordmaster.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    learned: Int,
    total: Int,
    totalCorrect: Int,
    totalWrong: Int,
    sentencesLearned: Int,
    sentencesTotal: Int,
    onStartQuiz: () -> Unit,
    onShowLearned: () -> Unit,
    onShowStats: () -> Unit,
    onShowReview: () -> Unit,
    onShowDictionary: () -> Unit,
    onShowSentences: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Логотип / Заголовок
        Text(
            text = "🧠",
            fontSize = 48.sp
        )
        Text(
            text = "WordMaster",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Text(
            text = "Слова, предложения и озвучка",
            fontSize = 14.sp,
            color = TextGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Прогресс карточка
        com.wordmaster.app.ui.components.ProgressCard(
            learned = learned,
            total = total
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Статистика в мини-карточках
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMiniCard(
                icon = "✓",
                value = "$totalCorrect",
                label = "Верно",
                color = CorrectGreen,
                modifier = Modifier.weight(1f)
            )
            StatMiniCard(
                icon = "✗",
                value = "$totalWrong",
                label = "Ошибок",
                color = WrongRed,
                modifier = Modifier.weight(1f)
            )
            StatMiniCard(
                icon = "📊",
                value = if (totalCorrect + totalWrong > 0)
                    "${(totalCorrect * 100 / (totalCorrect + totalWrong))}%"
                else "—",
                label = "Точность",
                color = GoldYellow,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Прогресс по предложениям
        if (sentencesTotal > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundCard)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📜", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Предложения",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            "Выучено $sentencesLearned из $sentencesTotal",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = if (sentencesTotal > 0) "${sentencesLearned * 100 / sentencesTotal}%" else "0%",
                        color = ButtonPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Главная кнопка — Учить слова
        Button(
            onClick = onStartQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Учить слова", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Кнопка — Учить предложения
        Button(
            onClick = onShowSentences,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
        ) {
            Text("📜", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Учить предложения", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Проверить выученные
        OutlinedButton(
            onClick = onShowReview,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonTeal),
            border = androidx.compose.foundation.BorderStroke(1.dp, ButtonTeal)
        ) {
            Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Проверить выученные", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Две кнопки: Словарь и Изученные
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onShowDictionary,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, ButtonBlue)
            ) {
                Icon(Icons.Filled.LibraryBooks, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Словарь", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onShowLearned,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CorrectGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, CorrectGreen)
            ) {
                Icon(Icons.Filled.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Изученные", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Статистика
        OutlinedButton(
            onClick = onShowStats,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldYellow),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow)
        ) {
            Icon(Icons.Filled.BarChart, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Статистика", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Подсказка дня
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundCard)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💡", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Совет",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow
                    )
                    Text(
                        "Учите по 20-30 слов в день для лучшего запоминания",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }
        }
    }
}

@Composable
private fun StatMiniCard(
    icon: String,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 16.sp)
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextGray
            )
        }
    }
}
