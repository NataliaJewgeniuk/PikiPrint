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

/*************** Tipos de impresora ***************/
/*
    Se mantienen las impresoras definidas en la app web.
*/
enum class PrinterType(val label: String) {
    A1_COMBO("A1 Combo"),
    A1_MINI("A1 Mini")
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

/*************** Parámetros de impresora ***************/
/*
    price:
        precio de compra de la impresora.

    lifespanHours:
        vida útil estimada en horas.

    powerKw:
        consumo eléctrico aproximado en kW.
*/
data class PrinterSettings(
    var price: Double,
    var lifespanHours: Int,
    var powerKw: Double
)

/*************** Configuración de presupuesto ***************/
/*
    Esta configuración contiene todos los valores base que usa la calculadora.
*/
data class QuoteSettings(
    val filamentCost: MutableMap<FilamentType, Double> = mutableMapOf(
        FilamentType.PLA to 22000.0,
        FilamentType.PETG to 26000.0,
        FilamentType.FLEX to 38000.0
    ),

    var energyRate: Double = 120.0,

    val printers: MutableMap<PrinterType, PrinterSettings> = mutableMapOf(
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
    ),

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
    Este modelo queda alineado con la app web.

    En la web hay una diferencia conceptual entre QuoteData y Order.
    Para esta etapa en Android usamos Order como modelo principal,
    pero ya contiene todos los campos necesarios para comportarse como
    presupuesto editable y como pedido guardado.
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

    var totalComputed: Double = 0.0,

    /*************** Campos comerciales del presupuesto ***************/
    val quoteDate: String = "",
    val validityDays: Int = 15,

    /*
        Plazo de entrega expresado en días hábiles.
        Sábado y domingo no cuentan.
    */
    val deliveryBusinessDays: Int = 7,

    /*
        Fecha estimada de entrega ya calculada.
        Se calcula desde quoteDate + deliveryBusinessDays.
    */
    val deliveryDate: String = "",

    val paymentType: PaymentType = PaymentType.FULL,
    val depositPercentage: Int = 50,

    val finishType: FinishType = FinishType.VISIBLE_LINES,

    val notes: String = ""
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
/*
    Por ahora este estado vive en memoria dentro del AppViewModel.
*/
data class AppState(
    val orders: List<Order> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val quoteSettings: QuoteSettings = QuoteSettings()
)