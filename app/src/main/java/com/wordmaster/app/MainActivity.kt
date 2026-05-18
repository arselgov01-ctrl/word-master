package com.wordmaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wordmaster.app.settings.AppSettings
import com.wordmaster.app.ui.screens.DictionaryScreen
import com.wordmaster.app.ui.screens.LearnedSentencesScreen
import com.wordmaster.app.ui.screens.LearnedWordsScreen
import com.wordmaster.app.ui.screens.MainScreen
import com.wordmaster.app.ui.screens.QuizScreen
import com.wordmaster.app.ui.screens.ReviewScreen
import com.wordmaster.app.ui.screens.ReviewSentencesScreen
import com.wordmaster.app.ui.screens.SentenceQuizScreen
import com.wordmaster.app.ui.screens.SentencesScreen
import com.wordmaster.app.ui.screens.SettingsScreen
import com.wordmaster.app.ui.screens.StatsScreen
import com.wordmaster.app.ui.theme.BackgroundDark
import com.wordmaster.app.ui.theme.WordMasterTheme
import com.wordmaster.app.viewmodel.DictionaryViewModel
import com.wordmaster.app.viewmodel.LearnedSentencesViewModel
import com.wordmaster.app.viewmodel.LearnedWordsViewModel
import com.wordmaster.app.viewmodel.QuizViewModel
import com.wordmaster.app.viewmodel.SentenceQuizViewModel
import com.wordmaster.app.viewmodel.SentencesViewModel
import com.wordmaster.app.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState(initial = AppSettings())
            WordMasterTheme(themeMode = settings.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    WordMasterNavigation(
                        settings = settings,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        (application as? WordMasterApp)?.ttsManager?.stop()
        super.onDestroy()
    }
}

enum class Screen {
    Main,
    Quiz,
    LearnedWords,
    LearnedSentences,
    Stats,
    Review,
    ReviewSentences,
    Dictionary,
    Sentences,
    SentenceQuiz,
    Settings
}

@Composable
fun WordMasterNavigation(
    settings: AppSettings,
    settingsViewModel: SettingsViewModel
) {
    val quizViewModel: QuizViewModel = viewModel()
    val learnedViewModel: LearnedWordsViewModel = viewModel()
    val dictionaryViewModel: DictionaryViewModel = viewModel()
    val sentencesViewModel: SentencesViewModel = viewModel()
    val sentenceQuizViewModel: SentenceQuizViewModel = viewModel()
    val learnedSentencesViewModel: LearnedSentencesViewModel = viewModel()

    var currentScreen by remember { mutableStateOf(Screen.Main) }

    val quizState by quizViewModel.state.collectAsState()
    val learnedCount by quizViewModel.learnedCount.collectAsState()
    val totalCount by quizViewModel.totalCount.collectAsState()
    val totalCorrect by quizViewModel.totalCorrect.collectAsState()
    val totalWrong by quizViewModel.totalWrong.collectAsState()

    val learnedWords by learnedViewModel.filteredWords.collectAsState()
    val searchQuery by learnedViewModel.searchQuery.collectAsState()
    val reviewState by learnedViewModel.reviewState.collectAsState()

    val dictionaryWords by dictionaryViewModel.words.collectAsState()
    val dictionarySearch by dictionaryViewModel.searchQuery.collectAsState()
    val dictionaryOnlyUserAdded by dictionaryViewModel.showOnlyUserAdded.collectAsState()

    val sentences by sentencesViewModel.sentences.collectAsState()
    val sentencesSearch by sentencesViewModel.searchQuery.collectAsState()
    val sentencesOnlyUserAdded by sentencesViewModel.showOnlyUserAdded.collectAsState()
    val sentencesTotal by sentencesViewModel.totalCount.collectAsState()
    val sentencesLearned by sentencesViewModel.learnedCount.collectAsState()

    val sentenceQuizState by sentenceQuizViewModel.state.collectAsState()
    val sentenceQuizTotal by sentenceQuizViewModel.totalCount.collectAsState()
    val sentenceQuizLearned by sentenceQuizViewModel.learnedCount.collectAsState()

    val learnedSentencesList by learnedSentencesViewModel.filteredSentences.collectAsState()
    val learnedSentencesSearch by learnedSentencesViewModel.searchQuery.collectAsState()
    val sentenceReviewState by learnedSentencesViewModel.reviewState.collectAsState()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith
                    slideOutHorizontally { it } + fadeOut()
            }
        },
        label = "navigation"
    ) { screen ->
        when (screen) {
            Screen.Main -> MainScreen(
                learned = learnedCount,
                total = totalCount,
                totalCorrect = totalCorrect,
                totalWrong = totalWrong,
                sentencesLearned = sentencesLearned,
                sentencesTotal = sentencesTotal,
                onStartWordQuiz = {
                    quizViewModel.loadNextWord()
                    currentScreen = Screen.Quiz
                },
                onStartSentenceQuiz = {
                    sentenceQuizViewModel.loadNext()
                    currentScreen = Screen.SentenceQuiz
                },
                onReviewWords = {
                    learnedViewModel.loadReviewWord()
                    currentScreen = Screen.Review
                },
                onReviewSentences = {
                    learnedSentencesViewModel.loadReviewSentence()
                    currentScreen = Screen.ReviewSentences
                },
                onShowWordDictionary = { currentScreen = Screen.Dictionary },
                onShowLearnedWords = { currentScreen = Screen.LearnedWords },
                onShowSentenceDictionary = { currentScreen = Screen.Sentences },
                onShowLearnedSentences = { currentScreen = Screen.LearnedSentences },
                onShowStats = { currentScreen = Screen.Stats },
                onShowSettings = { currentScreen = Screen.Settings }
            )

            Screen.Quiz -> QuizScreen(
                state = quizState,
                learnedCount = learnedCount,
                totalCount = totalCount,
                onAnswerSelected = { quizViewModel.selectAnswer(it) },
                onNextWord = { quizViewModel.loadNextWord() },
                onMarkLearned = { quizViewModel.markAsLearned() },
                onSkip = { quizViewModel.skipWord() },
                onBack = { currentScreen = Screen.Main }
            )

            Screen.LearnedWords -> LearnedWordsScreen(
                words = learnedWords,
                searchQuery = searchQuery,
                onSearchChange = { learnedViewModel.updateSearch(it) },
                onMarkAsUnlearned = { learnedViewModel.markAsUnlearned(it) },
                onResetAll = { learnedViewModel.resetAllLearned() },
                onBack = { currentScreen = Screen.Main }
            )

            Screen.LearnedSentences -> LearnedSentencesScreen(
                sentences = learnedSentencesList,
                searchQuery = learnedSentencesSearch,
                onSearchChange = { learnedSentencesViewModel.updateSearch(it) },
                onMarkAsUnlearned = { learnedSentencesViewModel.markAsUnlearned(it) },
                onResetAll = { learnedSentencesViewModel.resetAllLearned() },
                onBack = { currentScreen = Screen.Main }
            )

            Screen.Stats -> StatsScreen(
                totalWords = totalCount,
                learnedWords = learnedCount,
                totalCorrect = totalCorrect,
                totalWrong = totalWrong,
                onBack = { currentScreen = Screen.Main }
            )

            Screen.Review -> ReviewScreen(
                state = reviewState,
                onAnswerSelected = { learnedViewModel.selectReviewAnswer(it) },
                onNextWord = { learnedViewModel.loadReviewWord() },
                onBack = { currentScreen = Screen.Main }
            )

            Screen.ReviewSentences -> ReviewSentencesScreen(
                state = sentenceReviewState,
                onStart = { learnedSentencesViewModel.loadReviewSentence() },
                onAnswerSelected = { learnedSentencesViewModel.selectReviewAnswer(it) },
                onNext = { learnedSentencesViewModel.loadReviewSentence() },
                onBack = { currentScreen = Screen.Main }
            )

            Screen.Dictionary -> DictionaryScreen(
                words = dictionaryWords,
                searchQuery = dictionarySearch,
                showOnlyUserAdded = dictionaryOnlyUserAdded,
                onSearchChange = { dictionaryViewModel.updateSearch(it) },
                onToggleUserAddedFilter = { dictionaryViewModel.toggleOnlyUserAdded() },
                onAdd = { en, ru, cat -> dictionaryViewModel.addWord(en, ru, cat) },
                onUpdate = { w, en, ru, cat -> dictionaryViewModel.updateWord(w, en, ru, cat) },
                onDelete = { dictionaryViewModel.deleteWord(it) },
                onBack = { currentScreen = Screen.Main }
            )

            Screen.Sentences -> SentencesScreen(
                sentences = sentences,
                searchQuery = sentencesSearch,
                showOnlyUserAdded = sentencesOnlyUserAdded,
                learned = sentencesLearned,
                total = sentencesTotal,
                onSearchChange = { sentencesViewModel.updateSearch(it) },
                onToggleUserAddedFilter = { sentencesViewModel.toggleOnlyUserAdded() },
                onStartQuiz = {
                    sentenceQuizViewModel.loadNext()
                    currentScreen = Screen.SentenceQuiz
                },
                onAdd = { en, ru, cat -> sentencesViewModel.addSentence(en, ru, cat) },
                onUpdate = { s, en, ru, cat -> sentencesViewModel.updateSentence(s, en, ru, cat) },
                onDelete = { sentencesViewModel.deleteSentence(it) },
                onMarkAsUnlearned = { sentencesViewModel.markAsUnlearned(it) },
                onBack = { currentScreen = Screen.Main }
            )

            Screen.SentenceQuiz -> SentenceQuizScreen(
                state = sentenceQuizState,
                learnedCount = sentenceQuizLearned,
                totalCount = sentenceQuizTotal,
                onAnswerSelected = { sentenceQuizViewModel.selectAnswer(it) },
                onNextSentence = { sentenceQuizViewModel.loadNext() },
                onMarkLearned = { sentenceQuizViewModel.markAsLearned() },
                onSkip = { sentenceQuizViewModel.skip() },
                onBack = { currentScreen = Screen.Main }
            )

            Screen.Settings -> SettingsScreen(
                settings = settings,
                onThemeChange = { settingsViewModel.setThemeMode(it) },
                onAnswerCountChange = { settingsViewModel.setAnswerCount(it) },
                onTtsSpeedChange = { settingsViewModel.setTtsSpeed(it) },
                onBack = { currentScreen = Screen.Main }
            )
        }
    }
}
