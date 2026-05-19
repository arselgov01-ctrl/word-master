package com.wordmaster.app.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.ui.components.AnswerButton
import com.wordmaster.app.ui.components.WordCard
import com.wordmaster.app.ui.theme.*
import com.wordmaster.app.viewmodel.ReviewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    state: ReviewState,
    onAnswerSelected: (WordEntity) -> Unit,
    onNextWord: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Проверка",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    if (state.reviewTotal > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ButtonPurple.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "✓${state.reviewCorrect} ✗${state.reviewWrong}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                color = ButtonPurple
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Назад", tint = TextWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ButtonPurple)
            }
        } else if (state.currentWord == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Нет слов для проверки",
                        fontSize = 18.sp,
                        color = TextGray
                    )
                    Text(
                        "Сначала выучите несколько слов!",
                        fontSize = 14.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
                    ) {
                        Text("Назад")
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
                // Пометка что это режим проверки
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ButtonPurple.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔄", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Режим проверки выученных слов. Ошибка вернёт слово на изучение!",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                WordCard(word = state.currentWord)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Выберите правильный перевод:",
                    fontSize = 14.sp,
                    color = TextGray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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

                // Результат
                AnimatedVisibility(visible = state.selectedAnswer != null) {
                    Column {
                        if (state.isCorrect == false) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = WrongRed.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "⚠️ Слово возвращено на изучение!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WrongRedLight
                                    )
                                    Text(
                                        "${state.currentWord.english} → ${state.currentWord.russian}",
                                        fontSize = 15.sp,
                                        color = TextWhite,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CorrectGreen.copy(alpha = 0.1f))
                            ) {
                                Text(
                                    "✅ Отлично! Вы помните это слово!",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CorrectGreenLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onNextWord,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
                        ) {
                            Icon(Icons.Filled.ArrowForward, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Следующее слово", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
