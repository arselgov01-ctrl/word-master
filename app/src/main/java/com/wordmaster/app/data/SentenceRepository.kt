package com.wordmaster.app.data

import kotlinx.coroutines.flow.Flow

class SentenceRepository(private val sentenceDao: SentenceDao) {

    val allSentences: Flow<List<SentenceEntity>> = sentenceDao.getAllSentences()
    val learnedSentences: Flow<List<SentenceEntity>> = sentenceDao.getLearnedSentences()
    val unlearnedSentences: Flow<List<SentenceEntity>> = sentenceDao.getUnlearnedSentences()
    val totalCount: Flow<Int> = sentenceDao.getTotalCount()
    val learnedCount: Flow<Int> = sentenceDao.getLearnedCount()
    val totalCorrect: Flow<Int?> = sentenceDao.getTotalCorrectAnswers()
    val totalWrong: Flow<Int?> = sentenceDao.getTotalWrongAnswers()

    suspend fun initializeSentences() {
        if (sentenceDao.getCount() == 0) {
            sentenceDao.insertSentences(getInitialSentences())
        }
    }

    suspend fun getNextQuizSentence(): SentenceEntity? = sentenceDao.getNextQuizSentence()

    suspend fun getWrongAnswers(correct: SentenceEntity, count: Int = 3): List<SentenceEntity> {
        return sentenceDao.getRandomSentencesExcluding(correct.id, count)
    }

    suspend fun onCorrectAnswer(sentence: SentenceEntity) {
        val updated = sentence.copy(
            correctCount = sentence.correctCount + 1,
            streak = sentence.streak + 1,
            lastAnsweredAt = System.currentTimeMillis(),
            isLearned = (sentence.streak + 1) >= 3
        )
        sentenceDao.updateSentence(updated)
    }

    suspend fun onWrongAnswer(sentence: SentenceEntity) {
        val updated = sentence.copy(
            wrongCount = sentence.wrongCount + 1,
            streak = 0,
            lastAnsweredAt = System.currentTimeMillis()
        )
        sentenceDao.updateSentence(updated)
    }

    suspend fun markAsLearned(id: Int) = sentenceDao.markAsLearned(id)
    suspend fun markAsUnlearned(id: Int) = sentenceDao.markAsUnlearned(id)
    suspend fun resetAllLearned() = sentenceDao.resetAllLearned()

    // ===== CRUD =====

    suspend fun addUserSentence(english: String, russian: String, category: String = "general"): Long {
        return sentenceDao.insertSentence(
            SentenceEntity(
                english = english.trim(),
                russian = russian.trim(),
                category = category.ifBlank { "general" },
                isUserAdded = true
            )
        )
    }

    suspend fun updateUserSentence(sentence: SentenceEntity) {
        sentenceDao.updateSentence(sentence)
    }

    suspend fun deleteSentence(sentence: SentenceEntity) {
        sentenceDao.deleteSentence(sentence)
    }

    suspend fun getSentenceById(id: Int): SentenceEntity? = sentenceDao.getSentenceById(id)

    private fun getInitialSentences(): List<SentenceEntity> = listOf(
        // === ПРИВЕТСТВИЯ ===
        SentenceEntity(english = "Hello, how are you?", russian = "Привет, как дела?", category = "greetings"),
        SentenceEntity(english = "Good morning!", russian = "Доброе утро!", category = "greetings"),
        SentenceEntity(english = "Good afternoon!", russian = "Добрый день!", category = "greetings"),
        SentenceEntity(english = "Good evening!", russian = "Добрый вечер!", category = "greetings"),
        SentenceEntity(english = "Good night!", russian = "Спокойной ночи!", category = "greetings"),
        SentenceEntity(english = "Nice to meet you.", russian = "Приятно познакомиться.", category = "greetings"),
        SentenceEntity(english = "How is it going?", russian = "Как поживаешь?", category = "greetings"),
        SentenceEntity(english = "See you later!", russian = "Увидимся позже!", category = "greetings"),
        SentenceEntity(english = "See you tomorrow!", russian = "Увидимся завтра!", category = "greetings"),
        SentenceEntity(english = "Take care!", russian = "Береги себя!", category = "greetings"),
        SentenceEntity(english = "Have a nice day!", russian = "Хорошего дня!", category = "greetings"),
        SentenceEntity(english = "Welcome!", russian = "Добро пожаловать!", category = "greetings"),
        SentenceEntity(english = "Goodbye!", russian = "До свидания!", category = "greetings"),

        // === ВЕЖЛИВОСТЬ ===
        SentenceEntity(english = "Thank you very much.", russian = "Большое спасибо.", category = "polite"),
        SentenceEntity(english = "You are welcome.", russian = "Пожалуйста (в ответ).", category = "polite"),
        SentenceEntity(english = "I am sorry.", russian = "Извините.", category = "polite"),
        SentenceEntity(english = "Excuse me, please.", russian = "Извините, пожалуйста.", category = "polite"),
        SentenceEntity(english = "No problem.", russian = "Без проблем.", category = "polite"),
        SentenceEntity(english = "It does not matter.", russian = "Это не важно.", category = "polite"),
        SentenceEntity(english = "Could you help me, please?", russian = "Не могли бы вы мне помочь?", category = "polite"),
        SentenceEntity(english = "May I ask you something?", russian = "Могу я кое-что спросить?", category = "polite"),

        // === ПОВСЕДНЕВНЫЕ ===
        SentenceEntity(english = "What is your name?", russian = "Как тебя зовут?", category = "everyday"),
        SentenceEntity(english = "My name is John.", russian = "Меня зовут Джон.", category = "everyday"),
        SentenceEntity(english = "Where are you from?", russian = "Откуда ты?", category = "everyday"),
        SentenceEntity(english = "I am from Russia.", russian = "Я из России.", category = "everyday"),
        SentenceEntity(english = "How old are you?", russian = "Сколько тебе лет?", category = "everyday"),
        SentenceEntity(english = "I am twenty years old.", russian = "Мне двадцать лет.", category = "everyday"),
        SentenceEntity(english = "What do you do?", russian = "Чем ты занимаешься?", category = "everyday"),
        SentenceEntity(english = "I am a student.", russian = "Я студент.", category = "everyday"),
        SentenceEntity(english = "I do not understand.", russian = "Я не понимаю.", category = "everyday"),
        SentenceEntity(english = "Could you repeat that?", russian = "Не могли бы вы повторить?", category = "everyday"),
        SentenceEntity(english = "Speak more slowly, please.", russian = "Говорите медленнее, пожалуйста.", category = "everyday"),
        SentenceEntity(english = "I do not speak English well.", russian = "Я плохо говорю по-английски.", category = "everyday"),
        SentenceEntity(english = "What time is it?", russian = "Сколько сейчас времени?", category = "everyday"),
        SentenceEntity(english = "It is three o'clock.", russian = "Сейчас три часа.", category = "everyday"),
        SentenceEntity(english = "I am hungry.", russian = "Я голоден.", category = "everyday"),
        SentenceEntity(english = "I am thirsty.", russian = "Я хочу пить.", category = "everyday"),
        SentenceEntity(english = "I am tired.", russian = "Я устал.", category = "everyday"),
        SentenceEntity(english = "I am happy.", russian = "Я счастлив.", category = "everyday"),
        SentenceEntity(english = "I love you.", russian = "Я тебя люблю.", category = "everyday"),
        SentenceEntity(english = "I miss you.", russian = "Я скучаю по тебе.", category = "everyday"),

        // === В ДОРОГЕ ===
        SentenceEntity(english = "How do I get to the station?", russian = "Как мне добраться до вокзала?", category = "travel"),
        SentenceEntity(english = "Where is the bus stop?", russian = "Где автобусная остановка?", category = "travel"),
        SentenceEntity(english = "How much does it cost?", russian = "Сколько это стоит?", category = "travel"),
        SentenceEntity(english = "I would like a ticket to London.", russian = "Я бы хотел билет до Лондона.", category = "travel"),
        SentenceEntity(english = "When does the train leave?", russian = "Когда отправляется поезд?", category = "travel"),
        SentenceEntity(english = "Is this seat taken?", russian = "Это место занято?", category = "travel"),
        SentenceEntity(english = "Turn left at the corner.", russian = "Поверните налево на углу.", category = "travel"),
        SentenceEntity(english = "Go straight ahead.", russian = "Идите прямо.", category = "travel"),
        SentenceEntity(english = "It is on the right.", russian = "Это справа.", category = "travel"),
        SentenceEntity(english = "I am lost.", russian = "Я заблудился.", category = "travel"),

        // === В МАГАЗИНЕ И РЕСТОРАНЕ ===
        SentenceEntity(english = "Can I have the menu, please?", russian = "Можно мне меню, пожалуйста?", category = "shopping"),
        SentenceEntity(english = "I would like a cup of coffee.", russian = "Я бы хотел чашку кофе.", category = "shopping"),
        SentenceEntity(english = "The bill, please.", russian = "Счёт, пожалуйста.", category = "shopping"),
        SentenceEntity(english = "Do you accept credit cards?", russian = "Вы принимаете кредитные карты?", category = "shopping"),
        SentenceEntity(english = "I am just looking, thanks.", russian = "Я просто смотрю, спасибо.", category = "shopping"),
        SentenceEntity(english = "Do you have a smaller size?", russian = "У вас есть размер поменьше?", category = "shopping"),
        SentenceEntity(english = "It is too expensive.", russian = "Это слишком дорого.", category = "shopping"),
        SentenceEntity(english = "Where can I find the bathroom?", russian = "Где находится туалет?", category = "shopping"),

        // === РАБОТА И УЧЁБА ===
        SentenceEntity(english = "I am working on a project.", russian = "Я работаю над проектом.", category = "work"),
        SentenceEntity(english = "Let us schedule a meeting.", russian = "Давайте назначим встречу.", category = "work"),
        SentenceEntity(english = "Could you send me the file?", russian = "Не могли бы вы прислать мне файл?", category = "work"),
        SentenceEntity(english = "I have a question.", russian = "У меня есть вопрос.", category = "work"),
        SentenceEntity(english = "I need more time.", russian = "Мне нужно больше времени.", category = "work"),
        SentenceEntity(english = "I will do my best.", russian = "Я постараюсь.", category = "work"),
        SentenceEntity(english = "I agree with you.", russian = "Я согласен с тобой.", category = "work"),
        SentenceEntity(english = "I do not agree.", russian = "Я не согласен.", category = "work"),

        // === ПОГОДА ===
        SentenceEntity(english = "It is sunny today.", russian = "Сегодня солнечно.", category = "weather"),
        SentenceEntity(english = "It is raining.", russian = "Идёт дождь.", category = "weather"),
        SentenceEntity(english = "It is very cold.", russian = "Очень холодно.", category = "weather"),
        SentenceEntity(english = "It is hot outside.", russian = "На улице жарко.", category = "weather"),
        SentenceEntity(english = "The weather is nice.", russian = "Погода хорошая.", category = "weather"),

        // === ЭМОЦИИ И МНЕНИЯ ===
        SentenceEntity(english = "I think you are right.", russian = "Я думаю, ты прав.", category = "opinion"),
        SentenceEntity(english = "I am not sure.", russian = "Я не уверен.", category = "opinion"),
        SentenceEntity(english = "That sounds great.", russian = "Звучит отлично.", category = "opinion"),
        SentenceEntity(english = "I do not mind.", russian = "Я не против.", category = "opinion"),
        SentenceEntity(english = "What do you think?", russian = "Что ты думаешь?", category = "opinion"),
        SentenceEntity(english = "It depends.", russian = "Это зависит от обстоятельств.", category = "opinion"),
        SentenceEntity(english = "I am looking forward to it.", russian = "Я с нетерпением жду этого.", category = "opinion"),
        SentenceEntity(english = "It was a pleasure.", russian = "Было приятно.", category = "opinion")
    )
}
