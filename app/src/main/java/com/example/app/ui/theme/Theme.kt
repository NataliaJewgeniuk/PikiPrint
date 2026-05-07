package com.example.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/*************** Esquema de color PikiPrint ***************/
/*
    MaterialTheme ahora usa la nueva estética:
    - fondo crema papel
    - texto marrón madera
    - acción principal verde hoja
    - acción secundaria celeste cielo
*/
private val PikiLightColorScheme = lightColorScheme(
    primary = PikiLeafDark,
    onPrimary = PikiWhite,
    primaryContainer = PikiLeaf,
    onPrimaryContainer = PikiWood,

    secondary = PikiSkyDark,
    onSecondary = PikiWhite,
    secondaryContainer = PikiSky,
    onSecondaryContainer = PikiWood,

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

    outline = PikiCreamLine,

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

/*************** Tipografía ***************/
/*
    Por ahora usamos la tipografía default de Material3.

    Más adelante podemos sumar Nunito o una fuente local.
    La estética se refuerza con FontWeight.Bold / ExtraBold en componentes.
*/
private val PikiTypography = Typography()

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PikiLightColorScheme,
        typography = PikiTypography,
        shapes = PikiShapes,
        content = content
    )
}