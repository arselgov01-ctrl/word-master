package com.wordmaster.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordmaster.app.settings.AppSettings
import com.wordmaster.app.settings.SettingsManager
import com.wordmaster.app.settings.ThemeMode
import com.wordmaster.app.ui.theme.BackgroundCard
import com.wordmaster.app.ui.theme.BackgroundDark
import com.wordmaster.app.ui.theme.ButtonBlue
import com.wordmaster.app.ui.theme.ButtonPurple
import com.wordmaster.app.ui.theme.ButtonTeal
import com.wordmaster.app.ui.theme.GoldYellow
import com.wordmaster.app.ui.theme.TextGray
import com.wordmaster.app.ui.theme.TextMuted
import com.wordmaster.app.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onThemeChange: (ThemeMode) -> Unit,
    onAnswerCountChange: (Int) -> Unit,
    onTtsSpeedChange: (Float) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Text("Настройки", fontWeight = FontWeight.Bold, color = TextWhite)
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Назад", tint = TextWhite)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SectionCard(title = "Тема оформления", icon = "🎨") {
                ThemeOption(
                    title = "Системная",
                    description = "Следует настройкам устройства",
                    selected = settings.themeMode == ThemeMode.System,
                    onSelect = { onThemeChange(ThemeMode.System) }
                )
                ThemeOption(
                    title = "Светлая",
                    description = "Светлое оформление",
                    selected = settings.themeMode == ThemeMode.Light,
                    onSelect = { onThemeChange(ThemeMode.Light) }
                )
                ThemeOption(
                    title = "Тёмная",
                    description = "Тёмное оформление",
                    selected = settings.themeMode == ThemeMode.Dark,
                    onSelect = { onThemeChange(ThemeMode.Dark) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(title = "Варианты ответа в квизе", icon = "🧩") {
                Text(
                    "Сколько вариантов показывать при выборе перевода.",
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                AnswerCountOption(
                    count = 4,
                    selected = settings.answerCount == 4,
                    onSelect = { onAnswerCountChange(4) }
                )
                AnswerCountOption(
                    count = 6,
                    selected = settings.answerCount == 6,
                    onSelect = { onAnswerCountChange(6) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(title = "Скорость озвучки", icon = "🔊") {
                Text(
                    "Влияет на скорость воспроизведения слов и предложений.",
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatSpeed(settings.ttsSpeed),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.width(56.dp)
                    )
                    Slider(
                        value = settings.ttsSpeed,
                        onValueChange = onTtsSpeedChange,
                        valueRange = SettingsManager.MIN_TTS_SPEED..SettingsManager.MAX_TTS_SPEED,
                        steps = 14, // ~0.1 step between 0.5 and 2.0
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = ButtonBlue,
                            activeTrackColor = ButtonBlue,
                            inactiveTrackColor = TextMuted
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("медленно", fontSize = 11.sp, color = TextGray)
                    Text("нормально", fontSize = 11.sp, color = TextGray)
                    Text("быстро", fontSize = 11.sp, color = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(title = "О приложении", icon = "ℹ️") {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SettingsManager.PRIVACY_POLICY_URL))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        runCatching { context.startActivity(intent) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ButtonPurple.copy(alpha = 0.6f))
                ) {
                    Icon(
                        Icons.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = ButtonPurple
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Политика конфиденциальности",
                        color = ButtonPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "WordMaster · версия 1.1",
                    color = TextGray,
                    fontSize = 12.sp
                )
                Text(
                    text = "Данные сохраняются только на устройстве.",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    description: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = ButtonTeal,
                unselectedColor = TextMuted
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.padding(end = 8.dp)) {
            Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = TextGray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AnswerCountOption(
    count: Int,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val (title, description) = when (count) {
        4 -> "4 варианта" to "Проще — рекомендуется по умолчанию"
        6 -> "6 вариантов" to "Сложнее — больше отвлекающих ответов"
        else -> "$count вариантов" to ""
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = GoldYellow,
                unselectedColor = TextMuted
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = TextGray, fontSize = 11.sp)
        }
    }
}

private fun formatSpeed(value: Float): String {
    val rounded = (Math.round(value * 10f) / 10f)
    return "${"%.1f".format(rounded)}×"
}
