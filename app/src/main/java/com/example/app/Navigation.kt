package com.example.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.screens.OrdersScreen
import com.example.app.screens.QuotesScreen
import com.example.app.screens.SettingsScreen
import com.example.app.ui.components.PikiScreen
import com.example.app.ui.theme.PikiCreamLine
import com.example.app.ui.theme.PikiLeaf
import com.example.app.ui.theme.PikiLeafDark
import com.example.app.ui.theme.PikiMutedText
import com.example.app.ui.theme.PikiPaperDark
import com.example.app.ui.theme.PikiPaperLight
import com.example.app.ui.theme.PikiSky
import com.example.app.ui.theme.PikiSkyDark
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
*/
enum class Screen(
    val label: String,
    val icon: String
) {
    Orders(
        label = "Pedidos",
        icon = "📋"
    ),

    Quotes(
        label = "N. Pedido",
        icon = "✨"
    ),

    Settings(
        label = "Ajustes",
        icon = "⚙️"
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

/*************** Barra inferior Piki ***************/
/*
    Barra tipo muelle de papel:
    - crema claro;
    - borde suave;
    - radios grandes;
    - ítem activo tipo burbuja.
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
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(34.dp))
                .background(PikiPaperLight.copy(alpha = 0.96f))
                .border(
                    width = 2.dp,
                    color = PikiCreamLine,
                    shape = RoundedCornerShape(34.dp)
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Screen.values().forEach { screen ->
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

/*************** Ítem de navegación ***************/
/*
    El ítem activo usa una burbuja verde hoja.
    El ítem inactivo queda más liviano, como texto sobre papel.
*/
@Composable
private fun PikiBottomNavigationItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (selected) {
            PikiLeaf
        } else {
            Color.Transparent
        }

    val textColor =
        if (selected) {
            PikiWood
        } else {
            PikiMutedText
        }

    val iconBackground =
        if (selected) {
            PikiLeafDark
        } else {
            PikiPaperDark
        }

    val iconColor =
        if (selected) {
            PikiWhite
        } else {
            PikiSkyDark
        }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(backgroundColor)
            .clickable {
                onClick()
            }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(iconBackground)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = screen.icon,
                color = iconColor,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = screen.label,
            color = textColor,
            fontWeight = if (selected) {
                FontWeight.ExtraBold
            } else {
                FontWeight.Bold
            }
        )
    }
}