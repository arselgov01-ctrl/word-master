package com.wordmaster.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnedWordsScreen(
    words: List<WordEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onMarkAsUnlearned: (Int) -> Unit,
    onResetAll: () -> Unit,
    onBack: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var expandedWordId by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Выученные слова (${words.size})",
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
                if (words.isNotEmpty()) {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Filled.RestartAlt, "Сбросить", tint = WrongRed)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text("Поиск слова...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextGray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Filled.Clear, null, tint = TextGray)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ButtonBlue,
                unfocusedBorderColor = TextMuted,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                cursorColor = ButtonBlue
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (words.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📚", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (searchQuery.isNotEmpty()) "Ничего не найдено"
                        else "Пока нет выученных слов",
                        fontSize = 18.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                    if (searchQuery.isEmpty()) {
                        Text(
                            "Начните учить слова в режиме викторины!",
                            fontSize = 14.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(words, key = { it.id }) { word ->
                    LearnedWordItem(
                        word = word,
                        isExpanded = expandedWordId == word.id,
                        onToggleExpand = {
                            expandedWordId = if (expandedWordId == word.id) -1 else word.id
                        },
                        onMarkAsUnlearned = { onMarkAsUnlearned(word.id) }
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Сбросить все?", color = TextWhite) },
            text = {
                Text(
                    "Все выученные слова будут возвращены на изучение. Вы уверены?",
                    color = TextGray
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onResetAll()
                    showResetDialog = false
                }) {
                    Text("Сбросить", color = WrongRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Отмена", color = TextGray)
                }
            },
            containerColor = BackgroundCard
        )
    }
}

@Composable
private fun LearnedWordItem(
    word: WordEntity,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onMarkAsUnlearned: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(12.dp),
        color = BackgroundCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = word.english,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = word.russian,
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✓${word.correctCount}", fontSize = 12.sp, color = CorrectGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("✗${word.wrongCount}", fontSize = 12.sp, color = WrongRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = TextMuted.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Категория: ${getCategoryName(word.category)}",
                        fontSize = 13.sp,
                        color = TextGray
                    )
                    Text(
                        "Серия правильных: ${word.streak}",
                        fontSize = 13.sp,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onMarkAsUnlearned,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WrongRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WrongRed.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.Undo, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Вернуть на изучение", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private fun getCategoryName(category: String): String {
    return when (category) {
        "verbs" -> "Глаголы"
        "nouns" -> "Существительные"
        "adjectives" -> "Прилагательные"
        "adverbs" -> "Наречия"
        "prepositions" -> "Предлоги/Союзы"
        "pronouns" -> "Местоимения"
        "numbers" -> "Числа"
        "abstract" -> "Абстрактные"
        else -> "Общее"
    }
}
