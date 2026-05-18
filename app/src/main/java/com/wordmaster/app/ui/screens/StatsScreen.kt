package com.wordmaster.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    totalWords: Int,
    learnedWords: Int,
    totalCorrect: Int,
    totalWrong: Int,
    onBack: () -> Unit,
    onResetStats: () -> Unit
) {
    val totalAnswers = totalCorrect + totalWrong
    val accuracy = if (totalAnswers > 0) (totalCorrect * 100.0 / totalAnswers) else 0.0
    val remaining = totalWords - learnedWords
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Статистика",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Назад", tint = TextWhite)
                }
            },
            actions = {
                IconButton(onClick = { showResetDialog = true }) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Сбросить статистику",
                        tint = TextWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Основной прогресс
            com.wordmaster.app.ui.components.ProgressCard(
                learned = learnedWords,
                total = totalWords
            )

            // Детальная статистика
            Text(
                "📊 Подробности",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "📖",
                    title = "Всего слов",
                    value = "$totalWords",
                    color = ButtonBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "✅",
                    title = "Выучено",
                    value = "$learnedWords",
                    color = CorrectGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "📝",
                    title = "Осталось",
                    value = "$remaining",
                    color = GoldYellow,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "🎯",
                    title = "Точность",
                    value = "${accuracy.toInt()}%",
                    color = ButtonPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "✓",
                    title = "Верных",
                    value = "$totalCorrect",
                    color = CorrectGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "✗",
                    title = "Ошибок",
                    value = "$totalWrong",
                    color = WrongRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "🔢",
                    title = "Всего ответов",
                    value = "$totalAnswers",
                    color = TextGray,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "📈",
                    title = "Ср. попыток",
                    value = if (learnedWords > 0) "${totalAnswers / learnedWords}" else "—",
                    color = ButtonTeal,
                    modifier = Modifier.weight(1f)
                )
            }

            // Достижения
            Text(
                "🏆 Достижения",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(top = 8.dp)
            )

            AchievementItem("🌱 Первый шаг", "Выучить первое слово", learnedWords >= 1)
            AchievementItem("📖 Десяточка", "Выучить 10 слов", learnedWords >= 10)
            AchievementItem("📚 Полтинник", "Выучить 50 слов", learnedWords >= 50)
            AchievementItem("🎓 Сотня", "Выучить 100 слов", learnedWords >= 100)
            AchievementItem("🏅 Марафонец", "Выучить 250 слов", learnedWords >= 250)
            AchievementItem("👑 Полпути", "Выучить 500 слов", learnedWords >= 500)
            AchievementItem("🏆 Полиглот", "Выучить все 1000 слов", learnedWords >= 1000)
            AchievementItem("🎯 Снайпер", "Точность более 90%", accuracy >= 90 && totalAnswers >= 50)
            AchievementItem("💪 Упорный", "Дать 500 ответов", totalAnswers >= 500)
            AchievementItem("🔥 Неудержимый", "Дать 1000 ответов", totalAnswers >= 1000)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = BackgroundCard,
            title = {
                Text(
                    "Сбросить статистику?",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Счётчики правильных и неправильных ответов, серии и точность " +
                        "будут обнулены для всех слов и предложений. Список выученных " +
                        "слов и предложений останется без изменений.",
                    color = TextGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onResetStats()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = WrongRed)
                ) {
                    Text("Сбросить", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextGray)
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    icon: String,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                fontSize = 12.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AchievementItem(
    icon: String,
    description: String,
    isUnlocked: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) CorrectGreen.copy(alpha = 0.1f)
            else BackgroundCard.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (isUnlocked) icon else "🔒",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    icon.split(" ").drop(1).joinToString(" ").ifEmpty { description },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) TextWhite else TextMuted
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = if (isUnlocked) TextGray else TextMuted
                )
            }
            if (isUnlocked) {
                Text("✅", fontSize = 20.sp)
            }
        }
    }
}
