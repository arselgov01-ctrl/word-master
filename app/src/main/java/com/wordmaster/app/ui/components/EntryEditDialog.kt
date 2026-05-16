package com.wordmaster.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.ui.theme.BackgroundCard
import com.wordmaster.app.ui.theme.ButtonBlue
import com.wordmaster.app.ui.theme.TextGray
import com.wordmaster.app.ui.theme.TextWhite
import com.wordmaster.app.ui.theme.WrongRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryEditDialog(
    title: String,
    englishLabel: String = "Английский",
    russianLabel: String = "Русский",
    initialEnglish: String = "",
    initialRussian: String = "",
    initialCategory: String = "general",
    categories: List<Pair<String, String>>,
    submitLabel: String = "Сохранить",
    showDelete: Boolean = false,
    onDelete: (() -> Unit)? = null,
    onConfirm: (english: String, russian: String, category: String) -> Unit,
    onDismiss: () -> Unit
) {
    var english by remember { mutableStateOf(initialEnglish) }
    var russian by remember { mutableStateOf(initialRussian) }
    var category by remember { mutableStateOf(initialCategory) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundCard,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = title,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = english,
                    onValueChange = { english = it },
                    label = { Text(englishLabel) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = russian,
                    onValueChange = { russian = it },
                    label = { Text(russianLabel) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = categoryLabel(category, categories),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Категория") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    category = key
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (showDelete && onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text("Удалить", color = WrongRed)
                    }
                    Spacer(modifier = Modifier.height(0.dp).padding(end = 8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Отмена", color = TextGray)
                }
                TextButton(
                    onClick = {
                        if (english.isNotBlank() && russian.isNotBlank()) {
                            onConfirm(english, russian, category)
                        }
                    },
                    enabled = english.isNotBlank() && russian.isNotBlank()
                ) {
                    Text(submitLabel, color = ButtonBlue, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    disabledTextColor = TextGray,
    focusedContainerColor = BackgroundCard,
    unfocusedContainerColor = BackgroundCard,
    cursorColor = ButtonBlue,
    focusedLabelColor = ButtonBlue,
    unfocusedLabelColor = TextGray,
    focusedIndicatorColor = ButtonBlue,
    unfocusedIndicatorColor = TextGray
)

private fun categoryLabel(key: String, categories: List<Pair<String, String>>): String {
    return categories.firstOrNull { it.first == key }?.second ?: key
}
