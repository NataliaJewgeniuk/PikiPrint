package com.example.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.app.models.FinishType
import com.example.app.models.Order
import com.example.app.models.PaymentType
import com.example.app.models.QuoteSettings
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/*************** Compartir imagen de presupuesto ***************/
/*
    La imagen usa el desglose histórico guardado dentro del pedido.

    Ya no recalcula usando la configuración actual.
    Esto evita que un presupuesto viejo cambie si se modifican valores en Ajustes.
*/
@Suppress("UNUSED_PARAMETER")
fun shareQuoteImage(
    context: Context,
    order: Order,
    settings: QuoteSettings
) {
    val totalHistorico = obtenerTotalHistorico(order)

    val descuentosAplicados =
        order.descuentoCantidad +
                order.descuentoAmigo

    val bitmap = createQuoteBitmap(
        order = order,
        total = totalHistorico,
        descuentosAplicados = descuentosAplicados
    )

    val sharedDir = File(context.cacheDir, "shared_images")

    if (!sharedDir.exists()) {
        sharedDir.mkdirs()
    }

    val file = File(
        sharedDir,
        "presupuesto_${obtenerRefPedido(order)}.png"
    )

    FileOutputStream(file).use { output ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(
            Intent.EXTRA_SUBJECT,
            "Presupuesto ${obtenerRefPedido(order)}"
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(intent, "Compartir presupuesto")
    )
}

/*************** Crear bitmap del presupuesto ***************/
private fun createQuoteBitmap(
    order: Order,
    total: Double,
    descuentosAplicados: Double
): Bitmap {
    val width = 1200
    val outerPadding = 56
    val innerPadding = 48
    val contentWidth = width - (outerPadding * 2) - (innerPadding * 2)

    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FCFAFF")
    }

    val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFD8E2")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B584E8")
        textSize = 54f
        isFakeBoldText = true
    }

    val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#5D4E60")
        textSize = 34f
    }

    val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8A7A8C")
        textSize = 26f
        isFakeBoldText = true
    }

    val formatter = NumberFormat.getCurrencyInstance(
        Locale("es", "AR")
    ).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    /*************** Datos visibles en la imagen ***************/
    /*
        Se muestra "Descuentos aplicados" como suma de:
        - descuento por cantidad
        - descuento amigo

        No se distinguen ambos en la imagen final.
    */
    val lines = listOf(
        "REF: ${obtenerRefPedido(order)}",
        "Fecha: ${formatearFechaArgentina(order.quoteDate)}",
        "Pedido: ${order.title}",
        "Cliente: ${order.clientName}",
        "Total: ${formatter.format(total)}",
        "Unidades: ${order.quantity}",
        "Descuentos aplicados: ${formatter.format(descuentosAplicados)}",
        "Validez de la oferta: ${order.validityDays} días",
        "Plazo de entrega: ${order.deliveryBusinessDays} días hábiles",
        "Forma de pago: ${textoFormaPago(order)}",
        "Material: ${order.filament.label}",
        "Color: ${order.color}",
        "Acabado: ${textoAcabado(order.finishType)}"
    )

    val titleLayout = buildTextLayout(
        text = "Presupuesto",
        paint = titlePaint,
        width = contentWidth
    )

    val lineLayouts = lines.map {
        buildTextLayout(
            text = it,
            paint = bodyPaint,
            width = contentWidth
        )
    }

    val footerLayout = buildTextLayout(
        text = "PikiPrint 3D",
        paint = footerPaint,
        width = contentWidth
    )

    var totalHeight = 0
    totalHeight += outerPadding
    totalHeight += innerPadding
    totalHeight += titleLayout.height
    totalHeight += 24

    lineLayouts.forEach { layout ->
        totalHeight += layout.height
        totalHeight += 20
    }

    totalHeight += 24
    totalHeight += footerLayout.height
    totalHeight += innerPadding
    totalHeight += outerPadding

    val bitmap = Bitmap.createBitmap(
        width,
        totalHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    canvas.drawRect(
        0f,
        0f,
        width.toFloat(),
        totalHeight.toFloat(),
        backgroundPaint
    )

    val cardRect = RectF(
        outerPadding.toFloat(),
        outerPadding.toFloat(),
        (width - outerPadding).toFloat(),
        (totalHeight - outerPadding).toFloat()
    )

    canvas.drawRoundRect(cardRect, 36f, 36f, cardPaint)
    canvas.drawRoundRect(cardRect, 36f, 36f, borderPaint)

    var currentY = outerPadding + innerPadding

    drawLayout(
        canvas = canvas,
        layout = titleLayout,
        x = outerPadding + innerPadding.toFloat(),
        y = currentY.toFloat()
    )

    currentY += titleLayout.height + 24

    lineLayouts.forEach { layout ->
        drawLayout(
            canvas = canvas,
            layout = layout,
            x = outerPadding + innerPadding.toFloat(),
            y = currentY.toFloat()
        )

        currentY += layout.height + 20
    }

    currentY += 24

    drawLayout(
        canvas = canvas,
        layout = footerLayout,
        x = outerPadding + innerPadding.toFloat(),
        y = currentY.toFloat()
    )

    return bitmap
}

/*************** Crear layout de texto multilínea ***************/
private fun buildTextLayout(
    text: String,
    paint: TextPaint,
    width: Int
): StaticLayout {
    return StaticLayout.Builder
        .obtain(text, 0, text.length, paint, width)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setIncludePad(false)
        .build()
}

/*************** Dibujar layout ***************/
private fun drawLayout(
    canvas: Canvas,
    layout: StaticLayout,
    x: Float,
    y: Float
) {
    canvas.save()
    canvas.translate(x, y)
    layout.draw(canvas)
    canvas.restore()
}

/*************** Total histórico ***************/
private fun obtenerTotalHistorico(
    order: Order
): Double {
    return when {
        order.totalFinal > 0.0 -> order.totalFinal
        order.totalComputed > 0.0 -> order.totalComputed
        else -> 0.0
    }
}

/*************** REF visible ***************/
private fun obtenerRefPedido(
    order: Order
): String {
    return order.id
        .take(8)
        .uppercase()
}

/*************** Fecha dd/MM/yyyy ***************/
private fun formatearFechaArgentina(
    value: String
): String {
    if (value.isBlank()) {
        return "Sin fecha"
    }

    return try {
        LocalDate
            .parse(value)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        value
    }
}

/*************** Forma de pago amigable ***************/
private fun textoFormaPago(
    order: Order
): String {
    return when (order.paymentType) {
        PaymentType.FULL -> "Pago completo"
        PaymentType.DEPOSIT -> "Seña del ${order.depositPercentage}%"
    }
}

/*************** Texto amigable de acabado ***************/
private fun textoAcabado(
    finishType: FinishType
): String {
    return when (finishType) {
        FinishType.VISIBLE_LINES -> "Líneas de capa ligeramente visibles"
        FinishType.PAINTED -> "Pintado"
    }
}