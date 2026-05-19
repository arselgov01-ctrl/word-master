package com.wordmaster.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.ui.components.YandexBanner
import com.wordmaster.app.ui.theme.BackgroundCard
import com.wordmaster.app.ui.theme.BackgroundDark
import com.wordmaster.app.ui.theme.ButtonBlue
import com.wordmaster.app.ui.theme.ButtonPurple
import com.wordmaster.app.ui.theme.ButtonTeal
import com.wordmaster.app.ui.theme.CorrectGreen
import com.wordmaster.app.ui.theme.GoldYellow
import com.wordmaster.app.ui.theme.TextGray
import com.wordmaster.app.ui.theme.TextWhite
import com.wordmaster.app.ui.theme.WrongRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    learned: Int,
    total: Int,
    totalCorrect: Int,
    totalWrong: Int,
    sentencesLearned: Int,
    sentencesTotal: Int,
    onStartWordQuiz: () -> Unit,
    onStartSentenceQuiz: () -> Unit,
    onReviewWords: () -> Unit,
    onReviewSentences: () -> Unit,
    onShowWordDictionary: () -> Unit,
    onShowLearnedWords: () -> Unit,
    onShowSentenceDictionary: () -> Unit,
    onShowLearnedSentences: () -> Unit,
    onShowStats: () -> Unit,
    onShowSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    "WordMaster",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontSize = 20.sp
                )
            },
            actions = {
                IconButton(onClick = onShowSettings) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Настройки",
                        tint = TextWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🧠", fontSize = 36.sp)
            Text(
                text = "Слова, предложения и озвучка",
                fontSize = 13.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            com.wordmaster.app.ui.components.ProgressCard(
                learned = learned,
                total = total
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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

            if (sentencesTotal > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundCard)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📜", fontSize = 20.sp)
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
                            text = "${sentencesLearned * 100 / sentencesTotal}%",
                            color = ButtonPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Учить слова
            Button(
                onClick = onStartWordQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Учить слова", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Учить предложения (сразу квиз)
            Button(
                onClick = onStartSentenceQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
            ) {
                Text("📜", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Учить предложения", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Проверить выученные слова
            OutlinedButton(
                onClick = onReviewWords,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonTeal),
                border = BorderStroke(1.dp, ButtonTeal)
            ) {
                Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Проверить выученные слова", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Проверить выученные предложения
            OutlinedButton(
                onClick = onReviewSentences,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonPurple),
                border = BorderStroke(1.dp, ButtonPurple)
            ) {
                Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Проверить выученные предложения", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Словарь слов | Изученные
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onShowWordDictionary,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonBlue),
                    border = BorderStroke(1.dp, ButtonBlue)
                ) {
                    Icon(Icons.Filled.LibraryBooks, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Словарь слов", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onShowLearnedWords,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CorrectGreen),
                    border = BorderStroke(1.dp, CorrectGreen)
                ) {
                    Icon(Icons.Filled.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Изученные", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 6. Словарь предложений | Изученные
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onShowSentenceDictionary,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ButtonPurple),
                    border = BorderStroke(1.dp, ButtonPurple)
                ) {
                    Icon(Icons.Filled.LibraryBooks, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Словарь предлож.", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onShowLearnedSentences,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CorrectGreen),
                    border = BorderStroke(1.dp, CorrectGreen)
                ) {
                    Icon(Icons.Filled.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Изученные", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 7. Статистика
            OutlinedButton(
                onClick = onShowStats,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldYellow),
                border = BorderStroke(1.dp, GoldYellow)
            ) {
                Icon(Icons.Filled.BarChart, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Статистика", fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundCard)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Совет",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow
                        )
                        Text(
                            "Учите по 20-30 слов в день для лучшего запоминания",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Sticky banner ad at the bottom (Yandex Mobile Ads).
        YandexBanner(modifier = Modifier.fillMaxWidth())
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
