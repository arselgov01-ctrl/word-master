package com.wordmaster.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.ui.components.SpeakerButton
import com.wordmaster.app.ui.components.YandexBanner
import com.wordmaster.app.ui.theme.BackgroundCard
import com.wordmaster.app.ui.theme.BackgroundDark
import com.wordmaster.app.ui.theme.ButtonBlue
import com.wordmaster.app.ui.theme.CardGradientEnd
import com.wordmaster.app.ui.theme.CardGradientStart
import com.wordmaster.app.ui.theme.CorrectGreen
import com.wordmaster.app.ui.theme.GoldYellow
import com.wordmaster.app.ui.theme.TextGray
import com.wordmaster.app.ui.theme.TextMuted
import com.wordmaster.app.ui.theme.TextWhite
import com.wordmaster.app.ui.theme.WrongRed
import com.wordmaster.app.ui.theme.WrongRedLight
import com.wordmaster.app.viewmodel.QuizState

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
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Изучение слов", fontWeight = FontWeight.Bold, color = TextWhite)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("✓ ${state.sessionCorrect}", color = CorrectGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(20.dp))
                Text("✗ ${state.sessionWrong}", color = WrongRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (state.bestStreak > 0) {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("🏆 ${state.bestStreak}", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            ) {
                when {
                    state.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ButtonBlue)
                        }
                    }
                    state.currentWord == null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎉", fontSize = 64.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Все слова выучены!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldYellow,
                                    textAlign = TextAlign.Center
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
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp)
                        ) {
                            WordQuizCard(word = state.currentWord)

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Выберите правильный перевод:",
                                fontSize = 14.sp,
                                color = TextGray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            state.answers.forEachIndexed { index, answer ->
                                WordAnswerRow(
                                    index = index,
                                    word = answer,
                                    isSelected = state.selectedAnswer?.id == answer.id,
                                    isCorrectAnswer = state.currentWord.id == answer.id,
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
                                    Text("Правильный ответ:", fontSize = 13.sp, color = WrongRedLight)
                                    Text(
                                        "${state.currentWord.english} → ${state.currentWord.russian}",
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

            // Sticky banner ad below the quiz content. Closable via × icon;
            // resetKey ties the banner state to the active word so each new
            // quiz session starts with the banner visible again.
            YandexBanner(
                modifier = Modifier.fillMaxWidth(),
                resetKey = "quiz"
            )
        }

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
                    modifier = Modifier.padding(16.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldYellow,
                    textAlign = TextAlign.Center
                )
            }
        }

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
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.celebrationMessage,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun WordQuizCard(word: WordEntity) {
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
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TextWhite.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = getCategoryLabel(word.category),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = TextWhite.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = word.english.uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SpeakerButton(text = word.english, tint = TextWhite, size = 36.dp)
                }
                if (word.streak in 1..4) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
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

@Composable
private fun WordAnswerRow(
    index: Int,
    word: WordEntity,
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
                text = word.russian,
                color = TextWhite,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            if (isAnswered && (isCorrectAnswer || isSelected)) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCorrectAnswer) "✓" else "✗",
                    color = if (isCorrectAnswer) CorrectGreen else WrongRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getCategoryLabel(category: String): String = when (category) {
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
