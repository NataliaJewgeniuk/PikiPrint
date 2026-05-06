package com.example.app.utils

import com.example.app.models.Order
import com.example.app.models.QuoteSettings

/*************** Desglose del presupuesto ***************/
/*
    Este modelo devuelve cada parte del cálculo para poder mostrar
    el resultado final igual que en la app web.
*/
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

/*************** Calculadora de presupuesto ***************/
/*
    Esta clase contiene lógica pura.
    No depende de Compose, Android UI ni ViewModel.

    Eso permite testear el cálculo aparte y mantener la misma lógica
    que tiene la app web en calculator.ts.
*/
object Calculator {

    /*************** Calcular presupuesto ***************/
    /*
        Fórmula general:

        1. Costo material
        2. Costo energía
        3. Costo degradamiento / uso de impresora
        4. Costo diseño
        5. Subtotal
        6. Margen de ganancia
        7. Descuento por cantidad
        8. Descuento amigo
        9. Total final
    */
    fun calculateQuote(
        data: Order,
        settings: QuoteSettings
    ): QuoteBreakdown {

        /*************** Tiempo total ***************/
        val minutosTotales = (data.printTimeHours * 60) + data.printTimeMinutes
        val horasTotales = minutosTotales / 60.0

        /*************** 1. Costo de material ***************/
        val kgUsados = data.weightGramsPerUnit / 1000.0
        val precioKgFilamento = settings.filamentCost[data.filament] ?: 0.0

        val costoMaterial =
            kgUsados *
                    precioKgFilamento *
                    data.quantity

        /*************** 2. Costo energético ***************/
        val printerParams = settings.printers[data.printer]

        val costoEnergia = if (printerParams != null) {
            val kWhEstimados = horasTotales * printerParams.powerKw

            kWhEstimados *
                    settings.energyRate *
                    data.quantity
        } else {
            0.0
        }

        /*************** 3. Degradamiento de impresora ***************/
        val costoDegradamiento = if (
            printerParams != null &&
            printerParams.lifespanHours > 0
        ) {
            val costoMinutoImpresora =
                printerParams.price /
                        (printerParams.lifespanHours * 60.0)

            costoMinutoImpresora *
                    minutosTotales *
                    data.quantity
        } else {
            0.0
        }

        /*************** 4. Costo de diseño ***************/
        /*
            El diseño se cobra una sola vez por pedido.
            No se multiplica por cantidad.
        */
        val costoDiseno = settings.designCosts[data.designType] ?: 0.0

        /*************** 5. Subtotal ***************/
        val subtotal =
            costoMaterial +
                    costoEnergia +
                    costoDegradamiento +
                    costoDiseno

        /*************** 6. Margen de ganancia ***************/
        val margenMonto =
            subtotal *
                    (settings.marginPercentage / 100.0)

        val baseConMargen =
            subtotal +
                    margenMonto

        /*************** 7. Descuento por cantidad ***************/
        val porcentajeDescuentoCantidad = when {
            data.quantity >= 100 -> settings.quantityDiscounts[100] ?: 0.0
            data.quantity >= 75 -> settings.quantityDiscounts[75] ?: 0.0
            data.quantity >= 50 -> settings.quantityDiscounts[50] ?: 0.0
            data.quantity >= 25 -> settings.quantityDiscounts[25] ?: 0.0
            else -> 0.0
        }

        val descuentoCantidad =
            baseConMargen *
                    (porcentajeDescuentoCantidad / 100.0)

        /*************** 8. Descuento amigo ***************/
        val descuentoAmigo = if (data.isFriend) {
            (baseConMargen - descuentoCantidad) *
                    (settings.friendDiscountPercentage / 100.0)
        } else {
            0.0
        }

        /*************** 9. Total final ***************/
        val totalFinal =
            baseConMargen -
                    descuentoCantidad -
                    descuentoAmigo

        return QuoteBreakdown(
            costoMaterial = costoMaterial,
            costoEnergia = costoEnergia,
            costoDegradamiento = costoDegradamiento,
            costoDiseno = costoDiseno,
            subtotal = subtotal,
            margenMonto = margenMonto,
            baseConMargen = baseConMargen,
            descuentoCantidad = descuentoCantidad,
            descuentoAmigo = descuentoAmigo,
            totalFinal = totalFinal
        )
    }
}