package com.practicum.playlistmaker.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.navigation.NavigationGuard

class MainHostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host) // Устанавливаем layout с NavHost и BottomNavigationView

        // Получаем ссылку на NavHostFragment, через который управляется навигация
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController // Контроллер навигации

        // Получаем ссылку на нижнюю панель навигации
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Привязываем нижнюю панель к навигационному контроллеру — иконки начинают работать
        NavigationUI.setupWithNavController(bottomNav, navController)

        // Обрабатываем нажатия на элементы нижней навигации вручную
        bottomNav.setOnItemSelectedListener { item ->
            // Получаем текущий отображаемый фрагмент (тот, что в данный момент на экране)
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()

            // Если фрагмент реализует NavigationGuard и блокирует навигацию (например, из-за незавершённого ввода)
            if (currentFragment is NavigationGuard && currentFragment.shouldBlockNavigation()) {
                // Показываем диалог подтверждения выхода (например, при незаполненном плейлисте)
                currentFragment.requestExitConfirmation {
                    // Если пользователь подтвердил — делаем переход через стандартный механизм NavigationUI
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
                false // Навигация пока блокируется — возвращаем false
            } else {
                // Фрагмент не блокирует навигацию — выполняем переход
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }
}