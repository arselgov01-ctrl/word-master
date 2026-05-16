package com.wordmaster.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.ui.components.AnswerButton
import com.wordmaster.app.ui.components.WordCard
import com.wordmaster.app.ui.theme.*
import com.wordmaster.app.viewmodel.QuizState
import com.wordmaster.app.data.WordEntity

import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    state: QuizState,
    learnedCount: Int,
    totalCount: Int,
    onAnswerSelected: (WordEntity) -> Unit,
    onNextWord: () -> Unit,
    onMarkLearned: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            // Верхняя панель
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Изучение",
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CorrectGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "📖 $learnedCount/$totalCount",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                color = CorrectGreen
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Назад", tint = TextWhite)
                    }
                },
                actions = {
                    // Серия текущая
                    if (state.currentStreak > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldYellow.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🔥 ${state.currentStreak}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                color = GoldYellow
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )

            // Статистика сессии
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "✓ ${state.sessionCorrect}",
                    color = CorrectGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    "✗ ${state.sessionWrong}",
                    color = WrongRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (state.bestStreak > 0) {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        "🏆 ${state.bestStreak}",
                        color = GoldYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ButtonBlue)
                }
            } else if (state.currentWord == null) {
                // Все слова выучены!
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Поздравляем!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldYellow
                        )
                        Text(
                            "Все слова выучены!",
                            fontSize = 16.sp,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onBack,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
                        ) {
                            Text("На главную")
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    // Карточка слова
                    WordCard(word = state.currentWord)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Подсказка
                    Text(
                        text = "Выберите правильный перевод:",
                        fontSize = 14.sp,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Варианты ответов
                    state.answers.forEachIndexed { index, answer ->
                        AnswerButton(
                            answer = answer,
                            index = index,
                            isSelected = state.selectedAnswer?.id == answer.id,
                            isCorrectAnswer = state.currentWord.id == answer.id,
                            isAnswered = state.selectedAnswer != null,
                            onClick = { onAnswerSelected(answer) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Показываем правильный ответ если ошибся
                    AnimatedVisibility(
                        visible = state.selectedAnswer != null && state.isCorrect == false
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = WrongRed.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Правильный ответ:",
                                    fontSize = 13.sp,
                                    color = WrongRedLight
                                )
                                Text(
                                    "${state.currentWord.english} → ${state.currentWord.russian}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Кнопки действий
                    AnimatedVisibility(
                        visible = state.selectedAnswer != null,
                        enter = slideInVertically { it } + fadeIn()
                    ) {
                        Column {
                            Button(
                                onClick = onNextWord,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
                            ) {
                                Icon(Icons.Filled.ArrowForward, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Следующее слово", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onMarkLearned,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CorrectGreen),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CorrectGreen.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Запомнил", fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = onSkip,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, TextMuted)
                                ) {
                                    Icon(Icons.Filled.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Пропустить", fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Кнопки до ответа
                    AnimatedVisibility(
                        visible = state.selectedAnswer == null,
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onMarkLearned,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CorrectGreen),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CorrectGreen.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Уже знаю", fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = onSkip,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TextMuted)
                            ) {
                                Icon(Icons.Filled.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Пропустить", fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Сообщение о достижении
        AnimatedVisibility(
            visible = state.showCelebration,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .padding(20.dp)
                    .padding(top = 60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldYellow.copy(alpha = 0.15f))
            ) {
                Text(
                    text = state.celebrationMessage,
                    modifier = Modifier.padding(20.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldYellow,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Анимация празднования
        AnimatedVisibility(
            visible = state.showCelebration,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GoldYellow.copy(alpha = 0.15f),
                border = BorderStroke(2.dp, GoldYellow.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.celebrationMessage,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
