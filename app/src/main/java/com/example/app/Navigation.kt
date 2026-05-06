package com.example.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.screens.ExpensesScreen
import com.example.app.screens.OrdersScreen
import com.example.app.screens.QuotesScreen
import com.example.app.screens.SettingsScreen
import com.example.app.viewmodels.AppViewModel

enum class Screen(val title: String) {
    Orders("Pedidos"), Quotes("Presupuestos"), Settings("Ajustes"), Expenses("Gastos")
}

@Composable
fun Navigation(viewModel: AppViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(Screen.Orders) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Screen.values().forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = { Text(screen.title.take(1)) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Modifier.padding(paddingValues).let { mod ->
            when (currentScreen) {
                Screen.Orders -> OrdersScreen(
                    viewModel,
                    onGoToNewQuote = { currentScreen = Screen.Quotes },
                    onGoToSettings = { currentScreen = Screen.Settings }
                )
                Screen.Quotes -> QuotesScreen(
                    viewModel,
                    onGoToOrders = { currentScreen = Screen.Orders }
                )
                Screen.Settings -> SettingsScreen(viewModel)
                Screen.Expenses -> ExpensesScreen(viewModel)
            }
        }
    }
}