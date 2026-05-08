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

/*************** Modificadores estilo Tailwind ***************/
/*
    Equivalente a tu .kawaii-shadow de CSS:
    box-shadow: 0 10px 30px rgba(185, 139, 95, 0.15);
*/
fun Modifier.kawaiiShadow() = this.shadow(
    elevation = 12.dp,
    shape = RoundedCornerShape(32.dp),
    ambientColor = PikiClay.copy(alpha = 0.3f),
    spotColor = PikiClay.copy(alpha = 0.3f)
)

/*
    Equivalente a tu .sticker-border de CSS:
    border: 2.5px solid #B98B5F;
    Ideal para Boxes, Rows y Columns. Para botones usamos BorderStroke nativo.
*/
fun Modifier.stickerBorder(
    shape: RoundedCornerShape = RoundedCornerShape(999.dp),
    opacity: Float = 1f,
    width: Float = 2f
) = this.border(
    width = width.dp,
    color = PikiClay.copy(alpha = opacity),
    shape = shape
)

/*************** Pantalla base PikiPrint ***************/
/*
    Contenedor general. Fondo liso crema como el body de la web.
*/
@Composable
fun PikiScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PikiPaper)
    ) {
        // Se removió el patrón orgánico para respetar la limpieza del diseño web original.
        content()
    }
}

/*************** Card base (Equivalente a .soft-card) ***************/
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
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite // Borde blanco grueso
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}

/*************** Card de sección ***************/
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

/*************** Título de sección ***************/
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

/*************** Botón principal (bg-primary) ***************/
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
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(2.dp, PikiClay), // sticker-border
        colors = ButtonDefaults.buttonColors(
            containerColor = PikiSky,
            contentColor = PikiWood, // text-on-surface
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

/*************** Botón secundario (bg-secondary o bg-white) ***************/
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
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(2.dp, PikiClay),
        colors = ButtonDefaults.buttonColors(
            containerColor = PikiLeaf,
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

/*************** Botón con borde (Equivalente al botón blanco web) ***************/
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
            contentColor = PikiWood, // text-tertiary
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

/*************** Botón de texto cálido ***************/
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

/*************** Campo de texto Piki ***************/
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
        textStyle = LocalTextStyle.current.copy(
            color = PikiWood,
            fontWeight = FontWeight.SemiBold
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PikiClay, // focus:border-outline
            unfocusedBorderColor = PikiClay.copy(alpha = 0.2f), // border-outline/20
            focusedLabelColor = PikiClay,
            unfocusedLabelColor = PikiMutedText,
            focusedContainerColor = PikiWhite, // bg-white
            unfocusedContainerColor = PikiWhite,
            cursorColor = PikiWood,
            disabledBorderColor = PikiCreamLine,
            disabledContainerColor = PikiPaperDark,
            disabledTextColor = PikiMutedText
        )
    )
}

/*************** Campo numérico Piki ***************/
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

/*************** Pastilla genérica (Translúcida estilo web) ***************/
@Composable
fun PikiPill(
    text: String,
    modifier: Modifier = Modifier,
    background: Color,
    borderColor: Color,
    borderOpacity: Float = 0.3f
) {
    Box(
        modifier = modifier
            .stickerBorder(opacity = borderOpacity)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = PikiWood,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/*************** Pastilla de estado ***************/
@Composable
fun PikiStatusPill(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val background: Color
    val label: String

    when (status) {
        OrderStatus.PENDING -> {
            background = PikiPaperDark.copy(alpha = 0.8f) // bg-surface
            label = "Pendiente"
        }
        OrderStatus.PRINTING -> {
            background = PikiSky.copy(alpha = 0.3f) // bg-primary/20
            label = "En progreso"
        }
        OrderStatus.DONE -> {
            background = PikiLeaf.copy(alpha = 0.3f) // bg-secondary/20
            label = "Terminado"
        }
        OrderStatus.CANCELED -> {
            background = PikiCanceledBg.copy(alpha = 0.3f)
            label = "Cancelado"
        }
    }

    PikiPill(
        text = label,
        modifier = modifier,
        background = background,
        borderColor = PikiClay,
        borderOpacity = 0.5f // sticker-border border-opacity-50
    )
}

/*************** Banner informativo ***************/
@Composable
fun PikiInfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiSky.copy(alpha = 0.2f) // Fondo más suave
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiClay.copy(alpha = 0.4f) // Borde craft
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