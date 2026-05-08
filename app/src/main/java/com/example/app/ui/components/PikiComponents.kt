package com.example.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app.models.OrderStatus
import com.example.app.ui.theme.*

// Equivalente a .kawaii-shadow de Tailwind
fun Modifier.kawaiiShadow() = this.shadow(
    elevation = 7.dp,
    shape = RoundedCornerShape(32.dp),
    ambientColor = PikiClay.copy(alpha = 0.14f),
    spotColor = PikiClay.copy(alpha = 0.18f)
)

// Equivalente a .sticker-border de Tailwind
fun Modifier.stickerBorder(
    shape: RoundedCornerShape = RoundedCornerShape(999.dp),
    opacity: Float = 1f,
    width: Float = 2.5f,
    color: Color = PikiClay // En tu HTML, el sticker-border siempre es #B98B5F
) = this.border(
    width = width.dp,
    color = color.copy(alpha = opacity),
    shape = shape
)

// Pantalla base
@Composable
fun PikiScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PikiPaper) // bg-background (#FFF7E6)
    ) {
        content()
    }
}

// Card base
@Composable
fun PikiCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = PikiWhite), // bg-white
        border = BorderStroke(2.dp, PikiWhite) // border-2 border-white
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}

// Card de sección
@Composable
fun PikiSectionCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    PikiCard(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiWood
        )

        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = PikiMutedText,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        content()
    }
}

// Título de sección
@Composable
fun PikiSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = PikiWood
    )
}

// Botón primario (bg-primary, borde marrón)
@Composable
fun PikiPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .stickerBorder(), // Borde PikiClay por defecto
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PikiSky, // bg-primary
            contentColor = PikiWood,  // text-on-surface
            disabledContainerColor = PikiPaperDark,
            disabledContentColor = PikiMutedText
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelLarge)
    }
}

// Botón secundario (blanco con borde)
@Composable
fun PikiSecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .stickerBorder(),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PikiWhite,
            contentColor = PikiWood,
            disabledContainerColor = PikiPaperDark,
            disabledContentColor = PikiMutedText
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// Botón con borde translúcido
@Composable
fun PikiOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(2.dp, PikiClay.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PikiWood,
            containerColor = PikiWhite
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// Botón de texto simple
@Composable
fun PikiTextButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = PikiWood
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// Campo de texto (focus:border-outline)
@Composable
fun PikiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        textStyle = LocalTextStyle.current.copy(color = PikiWood, fontWeight = FontWeight.SemiBold),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PikiClay, // focus:border-outline
            unfocusedBorderColor = PikiClay.copy(alpha = 0.2f), // border-outline/20
            focusedLabelColor = PikiClay,
            unfocusedLabelColor = PikiMutedText,
            focusedContainerColor = PikiWhite,
            unfocusedContainerColor = PikiWhite,
            cursorColor = PikiClay
        )
    )
}

// Campo numérico
@Composable
fun PikiNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    PikiTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        keyboardType = KeyboardType.Decimal
    )
}

// Pastilla genérica
@Composable
fun PikiPill(
    text: String,
    modifier: Modifier = Modifier,
    background: Color,
    foreground: Color = PikiWood,
    borderColor: Color = Color.Transparent,
    borderOpacity: Float = 1f
) {
    Box(
        modifier = modifier
            .border(1.dp, borderColor.copy(alpha = borderOpacity), RoundedCornerShape(999.dp))
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = foreground,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// Pastilla de estado estilo HTML
@Composable
fun PikiStatusPill(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val background: Color
    val borderAlpha: Float
    val label: String

    // Replicamos la lógica EXACTA de tu principal.html
    when (status) {
        OrderStatus.PENDING -> {
            background = PikiPaperDark // bg-surface
            borderAlpha = 0.2f
            label = "Pendiente"
        }
        OrderStatus.PRINTING -> {
            background = PikiSky.copy(alpha = 0.2f) // bg-primary/20
            borderAlpha = 0.5f
            label = "En progreso"
        }
        OrderStatus.DONE -> {
            background = PikiLeaf.copy(alpha = 0.2f) // bg-secondary/20
            borderAlpha = 0.5f
            label = "Terminado"
        }
        OrderStatus.CANCELED -> {
            background = PikiCanceledBg
            borderAlpha = 0.5f
            label = "Cancelado"
        }
    }

    Box(
        modifier = modifier
            .stickerBorder(opacity = borderAlpha, color = PikiClay)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = PikiClay, // text-tertiary
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// Banner informativo
@Composable
fun PikiInfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiSky.copy(alpha = 0.2f)
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiClay.copy(alpha = 0.4f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = PikiWood,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}