package com.wordmaster.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.data.SentenceEntity
import com.wordmaster.app.ui.components.EntryEditDialog
import com.wordmaster.app.ui.components.SpeakerButton
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

private val SENTENCE_CATEGORIES = listOf(
    "general" to "📝 Общее",
    "greetings" to "👋 Приветствия",
    "polite" to "🙏 Вежливость",
    "everyday" to "🌤️ Повседневные",
    "travel" to "✈️ В дороге",
    "shopping" to "🛍️ Магазин/Ресторан",
    "work" to "💼 Работа",
    "weather" to "⛅ Погода",
    "opinion" to "💭 Мнения"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentencesScreen(
    sentences: List<SentenceEntity>,
    searchQuery: String,
    showOnlyUserAdded: Boolean,
    learned: Int,
    total: Int,
    onSearchChange: (String) -> Unit,
    onToggleUserAddedFilter: () -> Unit,
    onStartQuiz: () -> Unit,
    onAdd: (english: String, russian: String, category: String) -> Unit,
    onUpdate: (SentenceEntity, String, String, String) -> Unit,
    onDelete: (SentenceEntity) -> Unit,
    onMarkAsUnlearned: (Int) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<SentenceEntity?>(null) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Предложения ($learned/$total)",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = TextWhite)
                    }
                },
                actions = {
                    IconButton(onClick = onToggleUserAddedFilter) {
                        Icon(
                            imageVector = if (showOnlyUserAdded) Icons.Filled.FilterAlt else Icons.Filled.FilterAltOff,
                            contentDescription = "Фильтр пользовательских",
                            tint = if (showOnlyUserAdded) GoldYellow else TextGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ButtonBlue,
                contentColor = TextWhite
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить предложение")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Учить предложения", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Поиск...", color = TextGray) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = BackgroundCard,
                    unfocusedContainerColor = BackgroundCard,
                    cursorColor = ButtonBlue,
                    focusedIndicatorColor = ButtonBlue,
                    unfocusedIndicatorColor = TextGray
                )
            )

            if (sentences.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📜", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "Нет предложений. Добавьте первое!"
                            else "Ничего не найдено",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(sentences, key = { it.id }) { sentence ->
                        SentenceRow(
                            sentence = sentence,
                            onEdit = { editing = sentence },
                            onDelete = { onDelete(sentence) },
                            onMarkAsUnlearned = { onMarkAsUnlearned(sentence.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        EntryEditDialog(
            title = "Добавить предложение",
            englishLabel = "Предложение (English)",
            russianLabel = "Перевод (Русский)",
            categories = SENTENCE_CATEGORIES,
            submitLabel = "Добавить",
            onConfirm = { en, ru, cat ->
                onAdd(en, ru, cat)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editing?.let { sentence ->
        EntryEditDialog(
            title = "Редактировать предложение",
            englishLabel = "Предложение (English)",
            russianLabel = "Перевод (Русский)",
            initialEnglish = sentence.english,
            initialRussian = sentence.russian,
            initialCategory = sentence.category,
            categories = SENTENCE_CATEGORIES,
            submitLabel = "Сохранить",
            showDelete = true,
            onDelete = {
                onDelete(sentence)
                editing = null
            },
            onConfirm = { en, ru, cat ->
                onUpdate(sentence, en, ru, cat)
                editing = null
            },
            onDismiss = { editing = null }
        )
    }
}

@Composable
private fun SentenceRow(
    sentence: SentenceEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkAsUnlearned: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sentence.english,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (sentence.isUserAdded) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("★", color = GoldYellow, fontSize = 13.sp)
                    }
                    if (sentence.isLearned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✓", color = CorrectGreen, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sentence.russian,
                    color = TextGray,
                    fontSize = 13.sp
                )
                if (sentence.isLearned) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Сбросить",
                        color = ButtonTeal,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickableNoRipple { onMarkAsUnlearned() }
                    )
                }
            }
            SpeakerButton(text = sentence.english, tint = ButtonTeal, size = 36.dp)
            IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Edit, contentDescription = "Редактировать", tint = ButtonBlue)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = WrongRed)
            }
        }
    }
}

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}
