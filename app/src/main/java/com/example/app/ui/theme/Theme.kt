package com.example.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.app.R // ¡Importante! Asegurate de que coincida con el paquete de tu app

/*************** Familias Tipográficas ***************/
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

val Lexend = FontFamily(
    Font(R.font.lexend_regular, FontWeight.Normal),
    Font(R.font.lexend_medium, FontWeight.Medium),
    Font(R.font.lexend_bold, FontWeight.Bold)
)

/*************** Tipografía PikiPrint ***************/
/*
    Sobreescribimos la tipografía por defecto de Material 3.
    Así no tenemos que ir texto por texto cambiando la fuente.
*/
private val PikiTypography = Typography(
    // Títulos grandes y protagonistas (Plus Jakarta Sans)
    headlineLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),

    // Textos de lectura, descripciones y cuerpo (Lexend)
    bodyLarge = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),

    // Etiquetas, botones y pastillas (Plus Jakarta Sans)
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp // Un poquito de aire para que las pastillas se lean mejor
    )
)

/*************** Esquema de color PikiPrint ***************/
/*
    MaterialTheme ahora usa la misma jerarquía cromática que la web:
    - primary = celeste cielo
    - secondary = verde hoja
    - tertiary/outline = marrón craft
    - fondo = crema papel
*/
private val PikiLightColorScheme = lightColorScheme(
    primary = PikiSky,
    onPrimary = PikiWood,
    primaryContainer = PikiSkyLight,
    onPrimaryContainer = PikiSkyDark,

    secondary = PikiLeaf,
    onSecondary = PikiWood,
    secondaryContainer = PikiLeafLight,
    onSecondaryContainer = PikiLeafDark,

    tertiary = PikiClay,
    onTertiary = PikiWhite,
    tertiaryContainer = PikiClayLight,
    onTertiaryContainer = PikiWood,

    background = PikiPaper,
    onBackground = PikiWood,

    surface = PikiPaperLight,
    onSurface = PikiWood,

    surfaceVariant = PikiPaperDark,
    onSurfaceVariant = PikiMutedText,

    outline = PikiClay,

    error = PikiCanceledText,
    onError = PikiWhite,
    errorContainer = PikiCanceledBg,
    onErrorContainer = PikiCanceledText
)

/*************** Formas globales ***************/
/*
    Radios grandes para evitar sensación técnica o rígida.
*/
private val PikiShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

/*************** Tema Principal ***************/
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = PikiLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PikiTypography, // Acá inyectamos nuestras fuentes mapeadas
        shapes = PikiShapes,
        content = content
    )
}