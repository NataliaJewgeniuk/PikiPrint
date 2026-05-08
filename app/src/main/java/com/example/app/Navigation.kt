package com.example.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.screens.OrdersScreen
import com.example.app.screens.QuotesScreen
import com.example.app.screens.SettingsScreen
import com.example.app.ui.components.PikiScreen
import com.example.app.ui.components.kawaiiShadow
import com.example.app.ui.theme.PikiClay
import com.example.app.ui.theme.PikiCreamLine
import com.example.app.ui.theme.PikiMutedText
import com.example.app.ui.theme.PikiPaperDark
import com.example.app.ui.theme.PikiSky
import com.example.app.ui.theme.PikiWhite
import com.example.app.ui.theme.PikiWood
import com.example.app.viewmodels.AppViewModel

/*************** Pantallas principales ***************/
/*
    La app web tiene como flujo principal:
    - Pedidos
    - Nuevo pedido / Cotización
    - Ajustes

    Gastos queda fuera de la navegación principal por ahora.

    Ya no usamos emojis.
    Cada pantalla tiene un ícono real de Material Icons Extended.
*/
enum class Screen(
    val label: String,
    val icon: ImageVector
) {
    Orders(
        label = "Pedidos",
        icon = Icons.Outlined.ReceiptLong
    ),

    Quotes(
        label = "Calcular",
        icon = Icons.Outlined.Calculate
    ),

    Settings(
        label = "Ajustes",
        icon = Icons.Outlined.Settings
    )
}

/*************** Navegación principal ***************/
/*
    PikiScreen aplica:
    - fondo crema papel;
    - patrón orgánico sutil.

    Scaffold mantiene:
    - contenido con padding correcto;
    - barra inferior personalizada.
*/
@Composable
fun Navigation(
    appViewModel: AppViewModel = viewModel()
) {
    var currentScreen by remember {
        mutableStateOf(Screen.Orders)
    }

    PikiScreen {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                PikiBottomNavigation(
                    currentScreen = currentScreen,
                    onScreenSelected = { selectedScreen ->
                        currentScreen = selectedScreen
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.Orders -> OrdersScreen(
                        viewModel = appViewModel,
                        onGoToNewQuote = {
                            currentScreen = Screen.Quotes
                        },
                        onGoToSettings = {
                            currentScreen = Screen.Settings
                        }
                    )

                    Screen.Quotes -> QuotesScreen(
                        viewModel = appViewModel,
                        onGoToOrders = {
                            currentScreen = Screen.Orders
                        }
                    )

                    Screen.Settings -> SettingsScreen(
                        viewModel = appViewModel
                    )
                }
            }
        }
    }
}

/*************** Barra inferior Piki Premium ***************/
/*
    Barra tipo dock flotante:
    - fondo blanco limpio;
    - borde craft suave;
    - sombra cálida;
    - ítem activo tipo pill celeste;
    - íconos reales, sin emojis.
*/
@Composable
private fun PikiBottomNavigation(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .kawaiiShadow(),
            shape = RoundedCornerShape(32.dp),
            color = PikiWhite.copy(alpha = 0.96f),
            border = BorderStroke(
                width = 2.dp,
                color = PikiCreamLine.copy(alpha = 0.82f)
            ),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Screen.entries.forEach { screen ->
                    PikiBottomNavigationItem(
                        screen = screen,
                        selected = currentScreen == screen,
                        onClick = {
                            onScreenSelected(screen)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/*************** Ítem de navegación ***************/
/*
    Estado seleccionado:
    - pill celeste;
    - borde marrón craft;
    - texto fuerte.

    Estado inactivo:
    - fondo transparente;
    - ícono en círculo crema;
    - texto muted.
*/
@Composable
private fun PikiBottomNavigationItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemBackground =
        if (selected) {
            PikiSky
        } else {
            Color.Transparent
        }

    val itemBorder =
        if (selected) {
            BorderStroke(
                width = 2.dp,
                color = PikiClay
            )
        } else {
            null
        }

    val iconBackground =
        if (selected) {
            PikiWhite.copy(alpha = 0.90f)
        } else {
            PikiPaperDark.copy(alpha = 0.80f)
        }

    val iconTint =
        if (selected) {
            PikiClay
        } else {
            PikiMutedText
        }

    val textColor =
        if (selected) {
            PikiWood
        } else {
            PikiMutedText
        }

    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(26.dp),
        color = itemBackground,
        border = itemBorder,
        shadowElevation = if (selected) {
            2.dp
        } else {
            0.dp
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = RoundedCornerShape(999.dp),
                color = iconBackground,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (selected) {
                Text(
                    text = screen.label,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }
        }
    }
}