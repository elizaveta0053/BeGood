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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Растягиваем контент под системные панели
        super.onCreate(savedInstanceState)
        
        sharedPreferences = getSharedPreferences("BeGoodPrefs", MODE_PRIVATE)

        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            setHelloFrame1()
        } else {
            setHomeFrame()
        }
    }

    // Универсальная функция для настройки цвета иконок статус-бара
    // isLight = true (темные иконки для белого фона), false (белые иконки для темного фона)
    private fun setStatusBarIcons(isLight: Boolean) {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = isLight
    }

    // --- ЭКРАНЫ ПРИВЕТСТВИЯ (ONBOARDING) ---

    private fun setHelloFrame1() {
        setContentView(R.layout.hello_frame1)
        setStatusBarIcons(false) // Фон зеленый (темный) -> иконки белые

        val nextButton = findViewById<ImageButton>(R.id.button_next_right1)
        val skipButton = findViewById<TextView>(R.id.btnSkip1)

        nextButton.setOnClickListener { setHelloFrame2() }
        skipButton.setOnClickListener { completeOnboarding() }
    }

    private fun setHelloFrame2() {
        setContentView(R.layout.hello_frame2)
        setStatusBarIcons(false) // Зеленый фон -> белые иконки

        val nextButton = findViewById<ImageButton>(R.id.button_next_right2)
        val backButton = findViewById<ImageButton>(R.id.button_next_left2)
        val skipButton = findViewById<TextView>(R.id.btnSkip2)

        nextButton.setOnClickListener { setHelloFrame3() }
        backButton.setOnClickListener { setHelloFrame1() }
        skipButton.setOnClickListener { completeOnboarding() }
    }

    private fun setHelloFrame3() {
        setContentView(R.layout.hello_frame3)
        setStatusBarIcons(false)

        val backButton = findViewById<ImageButton>(R.id.button_next_left3)
        val startButton = findViewById<ImageButton>(R.id.button_next_right3)
        val skipButton = findViewById<TextView>(R.id.btnSkip3)

        backButton.setOnClickListener { setHelloFrame2() }
        startButton.setOnClickListener {
            completeOnboarding()
        }
        skipButton.setOnClickListener { completeOnboarding() }
    }

    private fun completeOnboarding() {
        sharedPreferences.edit {
            putBoolean("isFirstRun", false)
        }
        setHomeFrame()
    }


    // --- ГЛАВНЫЙ ЭКРАН (HOME) ---

    private fun setHomeFrame() {

        setContentView(R.layout.home_frame)
        setStatusBarIcons(true) // Фон белый -> иконки темные

        // Настройка отступов для Home
        applySystemInsets(findViewById(R.id.homeRoot), findViewById(R.id.headerLayout), findViewById(R.id.layoutBottomNav))

        // Инициализация RecyclerView
        setupRecyclerView()

        val categoryAll = findViewById<Button>(R.id.categoryAll)
        val categoryTrousers = findViewById<Button>(R.id.categoryTrousers)
        val categoryTshirts = findViewById<Button>(R.id.categoryTshirts)
        val categorySweatshirts = findViewById<Button>(R.id.categorySweatshirts)
        val categoryShoes = findViewById<Button>(R.id.categoryShoes)
        val categoryAccessories = findViewById<Button>(R.id.categoryAccessories)
        val categoryJewelry = findViewById<Button>(R.id.categoryJewelry)

        val allCategories = listOf(
            categoryAll, categoryTrousers, categoryTshirts,
            categorySweatshirts, categoryShoes, categoryAccessories, categoryJewelry
        )

        allCategories.forEach { categoryButton ->
            categoryButton.setOnClickListener {
                updateCategoriesUI(selectedCategory = categoryButton, allCategories = allCategories)
            }
        }

        updateCategoriesUI(categoryAll, allCategories)

        val notificationDot = findViewById<View>(R.id.notificationDot)
        if (checkNotificationsFromServer()) {
            notificationDot.visibility = View.VISIBLE
        } else {
            notificationDot.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        val rvProducts = findViewById<RecyclerView>(R.id.rvProducts)

        // example
        val products = listOf(
            Product(1, "Classic T-Shirt", 4.5, "Jerusalem", "New", R.drawable.img_onboarding_1),
            Product(2, "Blue Jeans", 4.8, "Tel Aviv", "Used", R.drawable.img_onboarding_2, isFavorite = true),
            Product(3, "Hoodie", 4.2, "Haifa", "New", R.drawable.img_onboarding_3),
            Product(4, "Sneakers", 5.0, "Eilat", "Sale", R.drawable.img_onboarding_1),
            Product(2, "Blue Jeans", 4.8, "Tel Aviv", "Used", R.drawable.img_onboarding_2, isFavorite = true),
            Product(3, "Hoodie", 4.2, "Haifa", "New", R.drawable.img_onboarding_3),
            Product(4, "Sneakers", 5.0, "Eilat", "Sale", R.drawable.img_onboarding_1)

        )

        rvProducts.adapter = ProductAdapter(products)
    }

    // Функция для правильной обработки системных отступов (верх и низ)
    private fun applySystemInsets(root: View, header: View, footer: View) {
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Добавляем отступ сверху для хедера (чтобы время не накладывалось на иконки)
            header.setPadding(header.paddingLeft, systemBars.top, header.paddingRight, header.paddingBottom)
            
            // Добавляем отступ снизу для навигации (чтобы полоска жестов не закрывала кнопки)
            footer.setPadding(0, 0, 0, systemBars.bottom)
            
            insets
        }
    }

    private fun updateCategoriesUI(selectedCategory: TextView, allCategories: List<TextView>) {
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val grayColor = ContextCompat.getColor(this, R.color.categories)

        for (button in allCategories) {
            if (button == selectedCategory) {
                button.isSelected = true
                button.setTextColor(whiteColor)
            } else {
                button.isSelected = false
                button.setTextColor(grayColor)
            }
        }
    }

    private fun checkNotificationsFromServer(): Boolean {
        return true
    }
}