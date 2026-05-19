package com.wordmaster.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.data.SentenceEntity
import com.wordmaster.app.ui.components.SpeakerButton
import com.wordmaster.app.ui.components.YandexBanner
import com.wordmaster.app.ui.theme.BackgroundCard
import com.wordmaster.app.ui.theme.BackgroundDark
import com.wordmaster.app.ui.theme.ButtonBlue
import com.wordmaster.app.ui.theme.ButtonPurple
import com.wordmaster.app.ui.theme.CardGradientEnd
import com.wordmaster.app.ui.theme.CardGradientStart
import com.wordmaster.app.ui.theme.CorrectGreen
import com.wordmaster.app.ui.theme.GoldYellow
import com.wordmaster.app.ui.theme.TextGray
import com.wordmaster.app.ui.theme.TextMuted
import com.wordmaster.app.ui.theme.TextWhite
import com.wordmaster.app.ui.theme.WrongRed
import com.wordmaster.app.ui.theme.WrongRedLight
import com.wordmaster.app.viewmodel.SentenceQuizState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceQuizScreen(
    state: SentenceQuizState,
    learnedCount: Int,
    totalCount: Int,
    onAnswerSelected: (SentenceEntity) -> Unit,
    onNextSentence: () -> Unit,
    onMarkLearned: () -> Unit,
    onSkip: () -> Unit,
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
                        "Изучение предложений",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ButtonPurple.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "📜 $learnedCount/$totalCount",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 13.sp,
                            color = ButtonPurple
                        )
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("✓ ${state.sessionCorrect}", color = CorrectGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(20.dp))
            Text("✗ ${state.sessionWrong}", color = WrongRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ButtonPurple)
                    }
                }
                state.currentSentence == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎉", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Все предложения выучены!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldYellow,
                                textAlign = TextAlign.Center
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
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp)
                    ) {
                    SentenceQuizCard(sentence = state.currentSentence)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Выберите правильный перевод:",
                        fontSize = 14.sp,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    state.answers.forEachIndexed { index, answer ->
                        AnswerOptionRow(
                            index = index,
                            sentence = answer,
                            isSelected = state.selectedAnswer?.id == answer.id,
                            isCorrectAnswer = state.currentSentence.id == answer.id,
                            isAnswered = state.selectedAnswer != null,
                            onClick = { onAnswerSelected(answer) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

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
                                    "Правильный перевод:",
                                    fontSize = 13.sp,
                                    color = WrongRedLight
                                )
                                Text(
                                    state.currentSentence.russian,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = state.selectedAnswer != null,
                        enter = slideInVertically { it } + fadeIn()
                    ) {
                        Column {
                            Button(
                                onClick = onNextSentence,
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
                                    border = BorderStroke(1.dp, CorrectGreen.copy(alpha = 0.5f))
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
                                    border = BorderStroke(1.dp, TextMuted)
                                ) {
                                    Icon(Icons.Filled.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Пропустить", fontSize = 13.sp)
                                }
                            }
                        }
                    }

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
                                border = BorderStroke(1.dp, CorrectGreen.copy(alpha = 0.5f))
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
                                border = BorderStroke(1.dp, TextMuted)
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
        }

        // Sticky banner ad below the quiz content. Closable via × icon.
        YandexBanner(
            modifier = Modifier.fillMaxWidth(),
            resetKey = "sentence_quiz"
        )
    }
}

@Composable
private fun SentenceQuizCard(sentence: SentenceEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                .padding(24.dp),
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
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sentence.english,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SpeakerButton(text = sentence.english, tint = TextWhite)
                }
            }
        }
    }
}

@Composable
private fun AnswerOptionRow(
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
    val labels = listOf("A", "B", "C", "D")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { mod ->
                if (!isAnswered) mod else mod
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick,
        enabled = !isAnswered
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
