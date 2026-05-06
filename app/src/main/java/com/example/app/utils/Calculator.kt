package com.example.app.utils

import com.example.app.models.Order
import com.example.app.models.QuoteSettings

data class QuoteBreakdown(
    val costoMaterial: Double,
    val costoEnergia: Double,
    val costoDegradamiento: Double,
    val costoDiseno: Double,
    val subtotal: Double,
    val margenMonto: Double,
    val baseConMargen: Double,
    val descuentoCantidad: Double,
    val descuentoAmigo: Double,
    val totalFinal: Double
)

object Calculator {
    fun calculateQuote(data: Order, settings: QuoteSettings): QuoteBreakdown {
        val minutosTotales = (data.printTimeHours * 60) + data.printTimeMinutes
        val horasTotales = minutosTotales / 60.0

        val kgUsados = data.weightGramsPerUnit / 1000.0
        val precioKgFilamento = settings.filamentCost[data.filament] ?: 0.0
        val costoMaterial = kgUsados * precioKgFilamento * data.quantity

        val printerParams = settings.printers[data.printer]!!
        val potencia = printerParams.powerKw
        val kWhEstimados = horasTotales * potencia
        val costoEnergia = kWhEstimados * settings.energyRate * data.quantity

        var costoDegradamiento = 0.0
        if (printerParams.lifespanHours > 0) {
            val costoMinutoImpresora = printerParams.price / (printerParams.lifespanHours * 60)
            costoDegradamiento = costoMinutoImpresora * minutosTotales * data.quantity
        }

        val costoDiseno = settings.designCosts[data.designType] ?: 0.0

        val subtotal = costoMaterial + costoEnergia + costoDegradamiento + costoDiseno

        val margenMonto = subtotal * (settings.marginPercentage / 100.0)
        val baseConMargen = subtotal + margenMonto

        var dtoPct = 0.0
        if (data.quantity >= 100) dtoPct = settings.quantityDiscounts[100] ?: 0.0
        else if (data.quantity >= 75) dtoPct = settings.quantityDiscounts[75] ?: 0.0
        else if (data.quantity >= 50) dtoPct = settings.quantityDiscounts[50] ?: 0.0
        else if (data.quantity >= 25) dtoPct = settings.quantityDiscounts[25] ?: 0.0

        val descuentoCantidad = baseConMargen * (dtoPct / 100.0)

        var descuentoAmigo = 0.0
        if (data.isFriend) {
            descuentoAmigo = (baseConMargen - descuentoCantidad) * (settings.friendDiscountPercentage / 100.0)
        }

        val totalFinal = baseConMargen - descuentoCantidad - descuentoAmigo

        return QuoteBreakdown(
            costoMaterial, costoEnergia, costoDegradamiento, costoDiseno,
            subtotal, margenMonto, baseConMargen, descuentoCantidad, descuentoAmigo, totalFinal
        )
    }
}