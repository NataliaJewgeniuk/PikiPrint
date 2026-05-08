package com.example.app.models

/*************** Estados de pedido ***************/
/*
    Estos estados equivalen a los usados en la app web:
    pending, printing, done y canceled.
*/
enum class OrderStatus(val label: String) {
    PENDING("Pendiente"),
    PRINTING("En marcha"),
    DONE("Terminado"),
    CANCELED("Cancelado")
}

/*************** Tipos de filamento ***************/
/*
    Se iguala con la app web.
    La web actualmente trabaja con PLA, PETG y FLEX.
*/
enum class FilamentType(val label: String) {
    PLA("PLA"),
    PETG("PETG"),
    FLEX("FLEX")
}

/*************** Tipos de impresora heredados ***************/
/*
    Estos valores se mantienen para compatibilidad con las pantallas actuales.

    A partir de la Etapa 3.1, la flota real se maneja con PrinterProfile.
    En la Etapa 3.2 la UI dejará de depender de PrinterType.entries.
*/
enum class PrinterType(val label: String) {
    A1_COMBO("A1 Combo"),
    A1_MINI("A1 Mini")
}

/*************** ID heredado de impresora ***************/
val PrinterType.defaultProfileId: String
    get() {
        return when (this) {
            PrinterType.A1_COMBO -> "printer_a1_combo"
            PrinterType.A1_MINI -> "printer_a1_mini"
        }
    }

/*************** Tipos de diseño ***************/
/*
    Estos valores reemplazan BASIC / INTERMEDIATE / COMPLEX.

    La idea es copiar la lógica de la app web:
    - external: el modelo ya existe o lo trae el cliente.
    - own: hay que diseñarlo desde cero.
    - details: se agregan detalles menores.
    - modifications: hay modificaciones o edición.
*/
enum class DesignType(val label: String) {
    EXTERNAL("Externo (ya hecho)"),
    OWN("Diseño propio"),
    DETAILS("Detalles agregados"),
    MODIFICATIONS("Modificaciones")
}

/*************** Tipo de pago ***************/
/*
    Permite indicar si el pedido se paga completo o con seña.
*/
enum class PaymentType(val label: String) {
    FULL("Pago completo"),
    DEPOSIT("Con seña")
}

/*************** Tipo de acabado ***************/
/*
    Campo agregado para reflejar el tipo de terminación del trabajo.
*/
enum class FinishType(val label: String) {
    VISIBLE_LINES("Con líneas visibles"),
    PAINTED("Pintado")
}

/*************** Parámetros heredados de impresora ***************/
/*
    Se mantiene para compatibilidad con el código actual.

    En la flota nueva usamos PrinterProfile, que además tiene:
    - id
    - name
    - isActive
*/
data class PrinterSettings(
    var price: Double,
    var lifespanHours: Int,
    var powerKw: Double
)

/*************** Perfil dinámico de impresora ***************/
/*
    Este es el nuevo modelo para poder agregar, editar y desactivar impresoras.

    id:
        identificador estable. No debería cambiar aunque se renombre la impresora.

    name:
        nombre visible.

    isActive:
        si está activa aparece en nuevos presupuestos.
        Si está inactiva, no aparece para nuevos pedidos, pero los pedidos viejos
        siguen mostrando printerNameSnapshot.
*/
data class PrinterProfile(
    val id: String,
    val name: String,
    val price: Double,
    val lifespanHours: Int,
    val powerKw: Double,
    val isActive: Boolean = true
)

/*************** Convertir perfil dinámico a parámetros de cálculo ***************/
fun PrinterProfile.toPrinterSettings(): PrinterSettings {
    return PrinterSettings(
        price = price,
        lifespanHours = lifespanHours,
        powerKw = powerKw
    )
}

/*************** Mapa heredado por defecto ***************/
fun defaultLegacyPrinterSettings(): MutableMap<PrinterType, PrinterSettings> {
    return mutableMapOf(
        PrinterType.A1_COMBO to PrinterSettings(
            price = 1200000.0,
            lifespanHours = 5000,
            powerKw = 0.35
        ),
        PrinterType.A1_MINI to PrinterSettings(
            price = 800000.0,
            lifespanHours = 4000,
            powerKw = 0.25
        )
    )
}

/*************** Perfiles dinámicos por defecto ***************/
fun defaultPrinterProfiles(
    legacyPrinters: Map<PrinterType, PrinterSettings> = defaultLegacyPrinterSettings()
): MutableList<PrinterProfile> {
    val a1ComboSettings =
        legacyPrinters[PrinterType.A1_COMBO]
            ?: PrinterSettings(
                price = 1200000.0,
                lifespanHours = 5000,
                powerKw = 0.35
            )

    val a1MiniSettings =
        legacyPrinters[PrinterType.A1_MINI]
            ?: PrinterSettings(
                price = 800000.0,
                lifespanHours = 4000,
                powerKw = 0.25
            )

    return mutableListOf(
        PrinterProfile(
            id = PrinterType.A1_COMBO.defaultProfileId,
            name = PrinterType.A1_COMBO.label,
            price = a1ComboSettings.price,
            lifespanHours = a1ComboSettings.lifespanHours,
            powerKw = a1ComboSettings.powerKw,
            isActive = true
        ),
        PrinterProfile(
            id = PrinterType.A1_MINI.defaultProfileId,
            name = PrinterType.A1_MINI.label,
            price = a1MiniSettings.price,
            lifespanHours = a1MiniSettings.lifespanHours,
            powerKw = a1MiniSettings.powerKw,
            isActive = true
        )
    )
}

/*************** Configuración de presupuesto ***************/
/*
    Esta configuración contiene todos los valores base que usa la calculadora.

    printers:
        mapa heredado para compatibilidad con pantallas existentes.

    printerProfiles:
        nueva flota dinámica. En la etapa 3.2 la UI va a trabajar sobre esto.
*/
data class QuoteSettings(
    val filamentCost: MutableMap<FilamentType, Double> = mutableMapOf(
        FilamentType.PLA to 22000.0,
        FilamentType.PETG to 26000.0,
        FilamentType.FLEX to 38000.0
    ),

    var energyRate: Double = 120.0,

    val printers: MutableMap<PrinterType, PrinterSettings> =
        defaultLegacyPrinterSettings(),

    val printerProfiles: MutableList<PrinterProfile> =
        defaultPrinterProfiles(printers),

    var marginPercentage: Double = 50.0,

    val quantityDiscounts: MutableMap<Int, Double> = mutableMapOf(
        25 to 5.0,
        50 to 10.0,
        75 to 15.0,
        100 to 20.0
    ),

    var friendDiscountPercentage: Double = 15.0,

    val designCosts: MutableMap<DesignType, Double> = mutableMapOf(
        DesignType.EXTERNAL to 0.0,
        DesignType.OWN to 20000.0,
        DesignType.DETAILS to 5000.0,
        DesignType.MODIFICATIONS to 10000.0
    )
)

/*************** Pedido / Presupuesto guardado ***************/
/*
    printer:
        campo heredado para compatibilidad.

    printerId:
        nuevo identificador dinámico.

    printerNameSnapshot:
        nombre congelado al momento de crear el pedido.
        Sirve para que pedidos viejos sigan mostrando la impresora correcta,
        aunque luego se desactive o se renombre.
*/
data class Order(
    val id: String,
    val title: String,
    val clientName: String,

    val filament: FilamentType,
    val printer: PrinterType,

    val printTimeHours: Int,
    val printTimeMinutes: Int,
    val weightGramsPerUnit: Int,
    val quantity: Int,

    val designType: DesignType,
    val color: String,

    val isFriend: Boolean,

    var status: OrderStatus,
    val createdAt: String,

    /*************** Total visible usado por pantallas existentes ***************/
    var totalComputed: Double = 0.0,

    /*************** Campos comerciales del presupuesto ***************/
    val quoteDate: String = "",
    val validityDays: Int = 15,
    val deliveryBusinessDays: Int = 7,
    val deliveryDate: String = "",

    val paymentType: PaymentType = PaymentType.FULL,
    val depositPercentage: Int = 50,

    val finishType: FinishType = FinishType.VISIBLE_LINES,

    val notes: String = "",
    /*************** Imagen local del pedido ***************/
    /*
        Ruta local estable de la imagen copiada al almacenamiento interno.

        Puede estar vacía si el pedido no tiene imagen.
    */
    val imageUri: String = "",
    /*************** Desglose histórico del presupuesto ***************/
    val subtotal: Double = 0.0,
    val margenMonto: Double = 0.0,
    val descuentoCantidad: Double = 0.0,
    val descuentoAmigo: Double = 0.0,
    val totalFinal: Double = 0.0,

    /*************** Impresora dinámica ***************/
    val printerId: String = printer.defaultProfileId,
    val printerNameSnapshot: String = printer.label
)

/*************** Gastos ***************/
/*
    Módulo secundario.
    Existe en Android y en la web, pero no será prioridad en la versión final inicial.
*/
enum class ExpenseCategory {
    FILAMENT,
    RESIN,
    MAINTENANCE,
    SHIPPING,
    OTHER
}

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val date: String,
    val category: ExpenseCategory
)

/*************** Estado global de la app ***************/
data class AppState(
    val orders: List<Order> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val quoteSettings: QuoteSettings = QuoteSettings()
)