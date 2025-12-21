package com.example.begood

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.activity.enableEdgeToEdge

class MainActivity : AppCompatActivity() {

    // Объявляем переменную для хранения настроек (память телефона)
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // Инициализируем "память". Файл будет называться "BeGoodPrefs"
        sharedPreferences = getSharedPreferences("BeGoodPrefs", MODE_PRIVATE)

        // 1. ПРОВЕРКА: Первый ли это вход?
        // Мы ищем запись "isFirstRun". Если её нет (первый раз), то возвращаем true.
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            // Если первый раз — показываем первый экран приветствия
            setHelloFrame1()
        } else {
            // Если уже заходили — сразу на главный
            setHomeFrame()
        }
    }

    // --- ЭКРАНЫ ПРИВЕТСТВИЯ (ONBOARDING) ---

    private fun setHelloFrame1() {
        setContentView(R.layout.hello_frame1)

        val nextButton = findViewById<ImageButton>(R.id.button_next_right1)
        val skipButton = findViewById<TextView>(R.id.btnSkip1)

        nextButton.setOnClickListener {
            setHelloFrame2()
        }

        skipButton.setOnClickListener {
            completeOnboarding() // Завершаем обучение и идем домой
        }
    }

    private fun setHelloFrame2() {
        setContentView(R.layout.hello_frame2)

        val nextButton = findViewById<ImageButton>(R.id.button_next_right2)
        val backButton = findViewById<ImageButton>(R.id.button_next_left2)
        val skipButton = findViewById<TextView>(R.id.btnSkip2)

        nextButton.setOnClickListener {
            setHelloFrame3()
        }

        backButton.setOnClickListener {
            setHelloFrame1()
        }

        skipButton.setOnClickListener {
            completeOnboarding()
        }
    }

    private fun setHelloFrame3() {
        setContentView(R.layout.hello_frame3)

        val backButton = findViewById<ImageButton>(R.id.button_next_left3)
        val startButton = findViewById<ImageButton>(R.id.button_next_right3) // Это кнопка Finish/Start
        val skipButton = findViewById<TextView>(R.id.btnSkip3)

        // Твоё условие: left3 должен кидать на frame3 (но логичнее вернуть назад на frame2, сделал frame2)
        backButton.setOnClickListener {
            setHelloFrame2()
        }

        startButton.setOnClickListener {
            setHomeFrame()
            completeOnboarding()
        }

        skipButton.setOnClickListener {
            completeOnboarding()
        }
    }

    // Эта функция записывает в память, что обучение пройдено, и открывает Home
    private fun completeOnboarding() {
        sharedPreferences.edit {
            putBoolean("isFirstRun", false) // Ставим метку "больше не первый раз"
        }
        setHomeFrame()
    }


    // --- ГЛАВНЫЙ ЭКРАН (HOME) ---

    private fun setHomeFrame() {
        setContentView(R.layout.home_frame)

        // 3. ЛОГИКА КАТЕГОРИЙ
        // Находим все кнопки категорий
        val categoryAll = findViewById<Button>(R.id.categoryAll)
        val categoryTrousers = findViewById<Button>(R.id.categoryTrousers)
        val categoryTshirts = findViewById<Button>(R.id.categoryTshirts)
        val categorySweatshirts = findViewById<Button>(R.id.categorySweatshirts)
        val categoryShoes = findViewById<Button>(R.id.categoryShoes)
        val categoryAccessories = findViewById<Button>(R.id.categoryAccessories)
        val categoryJewelry = findViewById<Button>(R.id.categoryJewelry)

        // Собираем их в список для удобства
        val allCategories = listOf(
            categoryAll, categoryTrousers, categoryTshirts,
            categorySweatshirts, categoryShoes, categoryAccessories, categoryJewelry
        )

        // Навешиваем слушатель нажатия на КАЖДУЮ кнопку
        allCategories.forEach { categoryButton ->
            categoryButton.setOnClickListener {
                // Когда нажали на кнопку, обновляем внешний вид всех кнопок
                updateCategoriesUI(selectedCategory = categoryButton, allCategories = allCategories)
            }
        }

        // По умолчанию выбираем "All" при старте
        updateCategoriesUI(categoryAll, allCategories)


        // 4. ЛОГИКА УВЕДОМЛЕНИЙ (Notification Dot)
        val notificationDot = findViewById<View>(R.id.notificationDot)

        // Имитация данных: true - есть новые, false - нет
        val hasNewNotifications = checkNotificationsFromServer()

        if (hasNewNotifications) {
            notificationDot.visibility = View.VISIBLE
        } else {
            notificationDot.visibility = View.GONE
        }
    }

    // Вспомогательная функция для покраски кнопок
    private fun updateCategoriesUI(selectedCategory: TextView, allCategories: List<TextView>) {
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val grayColor = ContextCompat.getColor(this, R.color.categories)

        for (button in allCategories) {
            if (button == selectedCategory) {
                // 1. Активируем состояние "selected" для селектора
                button.isSelected = true
                // 2. Меняем цвет текста
                button.setTextColor(whiteColor)
            } else {
                // 1. Деактивируем состояние
                button.isSelected = false
                // 2. Возвращаем серый цвет текста
                button.setTextColor(grayColor)
            }
        }
    }

    // Заглушка для проверки уведомлений
    private fun checkNotificationsFromServer(): Boolean {
        // Здесь в будущем будет логика проверки базы данных
        return true // Пока всегда возвращаем "да, есть уведомления" для теста
    }
}