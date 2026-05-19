package com.wordmaster.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.wordmaster.app.ui.theme.CorrectGreen
import com.wordmaster.app.ui.theme.TextGray
import com.wordmaster.app.ui.theme.TextMuted
import com.wordmaster.app.ui.theme.TextWhite
import com.wordmaster.app.ui.theme.WrongRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnedSentencesScreen(
    sentences: List<SentenceEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onMarkAsUnlearned: (Int) -> Unit,
    onResetAll: () -> Unit,
    onBack: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var expandedId by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Изученные предложения (${sentences.size})",
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
                if (sentences.isNotEmpty()) {
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
            placeholder = { Text("Поиск предложения...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextGray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Filled.Clear, null, tint = TextGray)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ButtonPurple,
                unfocusedBorderColor = TextMuted,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                cursorColor = ButtonPurple
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (sentences.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📜", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (searchQuery.isNotEmpty()) "Ничего не найдено"
                        else "Пока нет изученных предложений",
                        fontSize = 17.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                    if (searchQuery.isEmpty()) {
                        Text(
                            "Начните учить предложения!",
                            fontSize = 13.sp,
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
                items(sentences, key = { it.id }) { sentence ->
                    LearnedSentenceItem(
                        sentence = sentence,
                        isExpanded = expandedId == sentence.id,
                        onToggleExpand = {
                            expandedId = if (expandedId == sentence.id) -1 else sentence.id
                        },
                        onMarkAsUnlearned = { onMarkAsUnlearned(sentence.id) }
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
                    "Все изученные предложения будут возвращены на изучение. Вы уверены?",
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
private fun LearnedSentenceItem(
    sentence: SentenceEntity,
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
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sentence.english,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = sentence.russian,
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }
                SpeakerButton(text = sentence.english, tint = ButtonBlue, size = 32.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(20.dp)
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Divider(color = TextMuted.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row {
                        Text("✓${sentence.correctCount}", fontSize = 12.sp, color = CorrectGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("✗${sentence.wrongCount}", fontSize = 12.sp, color = WrongRed)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Серия: ${sentence.streak}", fontSize = 12.sp, color = TextGray)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
