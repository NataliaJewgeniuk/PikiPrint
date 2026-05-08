package com.example.app.ui.theme

import androidx.compose.ui.graphics.Color

/*************** Paleta PikiPrint 3D - Kawaii Web ***************/
/*
    Sincronizado al 100% con los colores de Tailwind.
*/

// Background & Surface
val PikiPaper = Color(0xFFFDF5E4) // background / surface en web
val PikiPaperLight = Color(0xFFFFFDF8)
val PikiPaperDark = Color(0xFFFBF3E2) // surface-container en web

// Primary (Sky Blue)
val PikiSky = Color(0xFFB7DFF0) // primary en web
val PikiSkyDark = Color(0xFF2A4D5C) // on-primary-container
val PikiSkyLight = Color(0xFFDDF2FB) // primary-container

// Secondary (Leaf Green)
val PikiLeaf = Color(0xFFA8D5A2) // secondary en web (¡Corregido!)
val PikiLeafDark = Color(0xFF6E9A6A)
val PikiLeafLight = Color(0xFFD8EFCF)

// Tertiary / Outline (Wood Brown / Craft)
val PikiClay = Color(0xFFB98B5F) // tertiary / outline en web
val PikiClayLight = Color(0xFFE8C69F)

// Text & Accents
val PikiWood = Color(0xFF4B3D33) // on-surface en web
val PikiWoodSoft = Color(0xFF3D342B) // on-surface de detalle.html
val PikiMutedText = Color(0xFF6D5F52) // on-surface-variant en web

val PikiCreamLine = Color(0xFFE9E1D2)
val PikiWhite = Color(0xFFFFFFFF) // Blanco puro para las cards como en web

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