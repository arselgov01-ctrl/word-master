package com.wordmaster.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.data.SentenceEntity
import com.wordmaster.app.ui.components.SpeakerButton
import com.wordmaster.app.ui.theme.BackgroundCard
import com.wordmaster.app.ui.theme.BackgroundDark
import com.wordmaster.app.ui.theme.ButtonBlue
import com.wordmaster.app.ui.theme.ButtonPurple
import com.wordmaster.app.ui.theme.CardGradientEnd
import com.wordmaster.app.ui.theme.CardGradientStart
import com.wordmaster.app.ui.theme.CorrectGreen
import com.wordmaster.app.ui.theme.CorrectGreenLight
import com.wordmaster.app.ui.theme.TextGray
import com.wordmaster.app.ui.theme.TextMuted
import com.wordmaster.app.ui.theme.TextWhite
import com.wordmaster.app.ui.theme.WrongRed
import com.wordmaster.app.ui.theme.WrongRedLight
import com.wordmaster.app.viewmodel.SentenceReviewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSentencesScreen(
    state: SentenceReviewState,
    onStart: () -> Unit,
    onAnswerSelected: (SentenceEntity) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        if (state.currentSentence == null && !state.isLoading) {
            onStart()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Проверка предложений", fontWeight = FontWeight.Bold, color = TextWhite)
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

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ButtonPurple)
                }
            }
            state.currentSentence == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Нет предложений для проверки", fontSize = 17.sp, color = TextGray)
                        Text(
                            "Сначала выучите несколько предложений!",
                            fontSize = 13.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onBack,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
                        ) {
                            Text("Назад")
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ButtonPurple.copy(alpha = 0.1f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🔄", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Режим проверки. Ошибка вернёт предложение на изучение!",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SentenceReviewCard(sentence = state.currentSentence)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Выберите правильный перевод:",
                        fontSize = 14.sp,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    state.answers.forEachIndexed { index, answer ->
                        SentenceAnswerRow(
                            index = index,
                            sentence = answer,
                            isSelected = state.selectedAnswer?.id == answer.id,
                            isCorrectAnswer = state.currentSentence.id == answer.id,
                            isAnswered = state.selectedAnswer != null,
                            onClick = { onAnswerSelected(answer) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

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
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            "⚠️ Предложение возвращено на изучение!",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WrongRedLight
                                        )
                                        Text(
                                            state.currentSentence.russian,
                                            fontSize = 14.sp,
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
                                        "✅ Отлично! Вы помните это предложение!",
                                        modifier = Modifier.padding(14.dp),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CorrectGreenLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = onNext,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
                            ) {
                                Icon(Icons.Filled.ArrowForward, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Следующее предложение", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun SentenceReviewCard(sentence: SentenceEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardGradientStart)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(colors = listOf(CardGradientStart, CardGradientEnd))
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TextWhite.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = sentence.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = TextWhite.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sentence.english,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SpeakerButton(text = sentence.english, tint = TextWhite, size = 36.dp)
                }
            }
        }
    }
}

@Composable
private fun SentenceAnswerRow(
    index: Int,
    sentence: SentenceEntity,
    isSelected: Boolean,
    isCorrectAnswer: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !isAnswered -> BackgroundCard
        isCorrectAnswer -> CorrectGreen.copy(alpha = 0.2f)
        isSelected -> WrongRed.copy(alpha = 0.2f)
        else -> BackgroundCard
    }
    val borderColor = when {
        !isAnswered -> TextMuted.copy(alpha = 0.3f)
        isCorrectAnswer -> CorrectGreen
        isSelected -> WrongRed
        else -> TextMuted.copy(alpha = 0.3f)
    }
    val labels = listOf("A", "B", "C", "D", "E", "F")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isAnswered) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ButtonBlue.copy(alpha = 0.15f),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        labels.getOrNull(index) ?: "?",
                        color = ButtonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = sentence.russian,
                color = TextWhite,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
