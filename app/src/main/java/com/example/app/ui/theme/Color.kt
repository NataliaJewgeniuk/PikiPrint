package com.example.app.ui.theme

import androidx.compose.ui.graphics.Color

/*************** Paleta PikiPrint 3D - Cozy Village ***************/
/*
    Nueva estética:
    - papel artesanal
    - taller cálido
    - naturaleza suave
    - elementos puffy / infladitos
*/

val PikiPaper = Color(0xFFFFF7E6)
val PikiPaperLight = Color(0xFFFFFDF8)
val PikiPaperDark = Color(0xFFF4EEDC)

val PikiLeaf = Color(0xFFA8D5A2)
val PikiLeafDark = Color(0xFF6E9A6A)
val PikiLeafLight = Color(0xFFD8EFCF)

val PikiSky = Color(0xFFB7DFF0)
val PikiSkyDark = Color(0xFF4B7285)
val PikiSkyLight = Color(0xFFDCEFF8)

val PikiWood = Color(0xFF4A3B2A)
val PikiWoodSoft = Color(0xFF8B6B4A)
val PikiClay = Color(0xFFB98B5F)
val PikiClayLight = Color(0xFFE8C69F)

val PikiCreamLine = Color(0xFFE9E1D2)
val PikiMutedText = Color(0xFF7D7468)
val PikiWhite = Color(0xFFFFFEFB)

/*************** Estados ***************/
val PikiPendingBg = Color(0xFFFFEBC4)
val PikiPendingText = Color(0xFF8B6B24)

val PikiPrintingBg = Color(0xFFDCEFF8)
val PikiPrintingText = Color(0xFF3F6C82)

val PikiDoneBg = Color(0xFFD8EFCF)
val PikiDoneText = Color(0xFF4F7D4C)

val PikiCanceledBg = Color(0xFFFFD8D2)
val PikiCanceledText = Color(0xFF9C3F32)

/*************** Alias de compatibilidad ***************/
/*
    Estos nombres estaban en la versión anterior.

    Los dejamos para que las pantallas existentes no se rompan mientras
    migramos visualmente etapa por etapa.
*/
val BrandLilac = PikiLeaf
val BrandCoral = PikiClay
val BrandOrange = PikiClayLight
val BrandGreen = PikiLeaf
val BrandBlue = PikiSky
val BrandPink = PikiPaperDark
val BrandText = PikiWood
val BrandTextMuted = PikiMutedText
val BrandBg = PikiPaper
val BrandBorder = PikiCreamLine
val White = PikiWhite