package com.wordmaster.app.ui.screens

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
import androidx.compose.material.icons.filled.Search
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
import com.wordmaster.app.data.WordEntity
import com.wordmaster.app.ui.components.EntryEditDialog
import com.wordmaster.app.ui.components.SpeakerButton
import com.wordmaster.app.ui.theme.BackgroundCard
import com.wordmaster.app.ui.theme.BackgroundDark
import com.wordmaster.app.ui.theme.ButtonBlue
import com.wordmaster.app.ui.theme.ButtonTeal
import com.wordmaster.app.ui.theme.CorrectGreen
import com.wordmaster.app.ui.theme.GoldYellow
import com.wordmaster.app.ui.theme.TextGray
import com.wordmaster.app.ui.theme.TextWhite
import com.wordmaster.app.ui.theme.WrongRed

private val WORD_CATEGORIES = listOf(
    "general" to "📝 Общее",
    "verbs" to "🏃 Глаголы",
    "nouns" to "📦 Существительные",
    "adjectives" to "🎨 Прилагательные",
    "adverbs" to "⚡ Наречия",
    "prepositions" to "🔗 Предлоги/Союзы",
    "pronouns" to "👤 Местоимения",
    "numbers" to "🔢 Числа",
    "abstract" to "💭 Абстрактные"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    words: List<WordEntity>,
    searchQuery: String,
    showOnlyUserAdded: Boolean,
    onSearchChange: (String) -> Unit,
    onToggleUserAddedFilter: () -> Unit,
    onAdd: (english: String, russian: String, category: String) -> Unit,
    onUpdate: (WordEntity, String, String, String) -> Unit,
    onDelete: (WordEntity) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<WordEntity?>(null) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Словарь (${words.size})",
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
                Icon(Icons.Filled.Add, contentDescription = "Добавить слово")
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

            if (showOnlyUserAdded) {
                Text(
                    text = "Показаны только пользовательские слова",
                    color = GoldYellow,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (words.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📚", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "Словарь пуст. Добавьте первое слово!"
                            else "Ничего не найдено",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                ) {
                    items(words, key = { it.id }) { word ->
                        WordRow(
                            word = word,
                            onEdit = { editingWord = word },
                            onDelete = { onDelete(word) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        EntryEditDialog(
            title = "Добавить слово",
            englishLabel = "Слово (English)",
            russianLabel = "Перевод (Русский)",
            categories = WORD_CATEGORIES,
            submitLabel = "Добавить",
            onConfirm = { en, ru, cat ->
                onAdd(en, ru, cat)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingWord?.let { word ->
        EntryEditDialog(
            title = "Редактировать слово",
            englishLabel = "Слово (English)",
            russianLabel = "Перевод (Русский)",
            initialEnglish = word.english,
            initialRussian = word.russian,
            initialCategory = word.category,
            categories = WORD_CATEGORIES,
            submitLabel = "Сохранить",
            showDelete = true,
            onDelete = {
                onDelete(word)
                editingWord = null
            },
            onConfirm = { en, ru, cat ->
                onUpdate(word, en, ru, cat)
                editingWord = null
            },
            onDismiss = { editingWord = null }
        )
    }
}

@Composable
private fun WordRow(
    word: WordEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                        text = word.english,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (word.isUserAdded) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("★", color = GoldYellow, fontSize = 14.sp)
                    }
                    if (word.isLearned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✓", color = CorrectGreen, fontSize = 14.sp)
                    }
                }
                Text(
                    text = word.russian,
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
            SpeakerButton(text = word.english, tint = ButtonTeal, size = 36.dp)
            IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Edit, contentDescription = "Редактировать", tint = ButtonBlue)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = WrongRed)
            }
        }
    }
}
