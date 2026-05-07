package com.example.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app.models.OrderStatus
import com.example.app.ui.theme.PikiCanceledBg
import com.example.app.ui.theme.PikiCanceledText
import com.example.app.ui.theme.PikiCreamLine
import com.example.app.ui.theme.PikiDoneBg
import com.example.app.ui.theme.PikiDoneText
import com.example.app.ui.theme.PikiLeaf
import com.example.app.ui.theme.PikiLeafDark
import com.example.app.ui.theme.PikiMutedText
import com.example.app.ui.theme.PikiPaper
import com.example.app.ui.theme.PikiPaperDark
import com.example.app.ui.theme.PikiPaperLight
import com.example.app.ui.theme.PikiPendingBg
import com.example.app.ui.theme.PikiPendingText
import com.example.app.ui.theme.PikiPrintingBg
import com.example.app.ui.theme.PikiPrintingText
import com.example.app.ui.theme.PikiSky
import com.example.app.ui.theme.PikiSkyDark
import com.example.app.ui.theme.PikiWhite
import com.example.app.ui.theme.PikiWood

/*************** Pantalla base PikiPrint ***************/
/*
    Contenedor general con fondo crema papel y patrón decorativo sutil.

    Lo vamos a usar más adelante en Navigation.kt o en cada screen.
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
        PikiOrganicPattern(
            modifier = Modifier.matchParentSize()
        )

        content()
    }
}

/*************** Patrón decorativo orgánico ***************/
/*
    Puntos y chispitas muy sutiles.

    La idea es romper la planitud del fondo sin molestar la lectura.
*/
@Composable
fun PikiOrganicPattern(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        val dotColor = PikiCreamLine.copy(alpha = 0.55f)
        val sparkleColor = PikiLeaf.copy(alpha = 0.16f)

        val stepX = 64f
        val stepY = 58f

        var y = 24f

        while (y < size.height) {
            var x = 28f

            while (x < size.width) {
                drawCircle(
                    color = dotColor,
                    radius = 2.2f,
                    center = Offset(x, y)
                )

                if (((x + y).toInt() / 10) % 5 == 0) {
                    drawLine(
                        color = sparkleColor,
                        start = Offset(x - 5f, y),
                        end = Offset(x + 5f, y),
                        strokeWidth = 1.4f
                    )

                    drawLine(
                        color = sparkleColor,
                        start = Offset(x, y - 5f),
                        end = Offset(x, y + 5f),
                        strokeWidth = 1.4f
                    )
                }

                x += stepX
            }

            y += stepY
        }
    }
}

/*************** Card base tipo papel/taller ***************/
@Composable
fun PikiCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiPaperLight
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiCreamLine
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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

/*************** Botón principal ***************/
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
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PikiLeafDark,
            contentColor = PikiWhite,
            disabledContainerColor = PikiPaperDark,
            disabledContentColor = PikiMutedText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/*************** Botón secundario ***************/
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
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PikiSky,
            contentColor = PikiWood,
            disabledContainerColor = PikiPaperDark,
            disabledContentColor = PikiMutedText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 1.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/*************** Botón con borde ***************/
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
            .heightIn(min = 50.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 2.dp,
            color = PikiCreamLine
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PikiWood,
            containerColor = PikiPaperLight
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.ExtraBold
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
            contentColor = PikiLeafDark
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
        label = {
            Text(label)
        },
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
            focusedBorderColor = PikiLeafDark,
            unfocusedBorderColor = PikiCreamLine,
            focusedLabelColor = PikiLeafDark,
            unfocusedLabelColor = PikiMutedText,
            focusedContainerColor = PikiPaperLight,
            unfocusedContainerColor = PikiPaperLight,
            cursorColor = PikiLeafDark,
            disabledBorderColor = PikiCreamLine,
            disabledContainerColor = PikiPaperDark,
            disabledTextColor = PikiWood
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

/*************** Pastilla genérica ***************/
@Composable
fun PikiPill(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = PikiPaperDark,
    foreground: Color = PikiWood
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = foreground,
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
    val foreground: Color
    val label: String

    when (status) {
        OrderStatus.PENDING -> {
            background = PikiPendingBg
            foreground = PikiPendingText
            label = "⏳ Pendiente"
        }

        OrderStatus.PRINTING -> {
            background = PikiPrintingBg
            foreground = PikiPrintingText
            label = "🖨️ En marcha"
        }

        OrderStatus.DONE -> {
            background = PikiDoneBg
            foreground = PikiDoneText
            label = "✨ Terminado"
        }

        OrderStatus.CANCELED -> {
            background = PikiCanceledBg
            foreground = PikiCanceledText
            label = "❌ Cancelado"
        }
    }

    PikiPill(
        text = label,
        modifier = modifier,
        background = background,
        foreground = foreground
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
            containerColor = PikiSky.copy(alpha = 0.55f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = PikiSkyDark.copy(alpha = 0.25f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(14.dp),
            color = PikiWood,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}