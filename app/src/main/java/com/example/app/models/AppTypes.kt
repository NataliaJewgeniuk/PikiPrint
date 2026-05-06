package com.example.app.models

enum class OrderStatus(val label: String) {
    PENDING("Pendiente"), PRINTING("En marcha"), DONE("Terminado"), CANCELED("Cancelado")
}

enum class FilamentType { PLA, PETG, ABS, TPU }
enum class PrinterType(val label: String) { A1_COMBO("A1 Combo"), A1_MINI("A1 Mini") }
enum class DesignType(val label: String) { BASIC("Básico"), INTERMEDIATE("Intermedio"), COMPLEX("Complejo") }

data class PrinterSettings(var price: Double, var lifespanHours: Int, var powerKw: Double)

data class QuoteSettings(
    val filamentCost: MutableMap<FilamentType, Double> = mutableMapOf(
        FilamentType.PLA to 22000.0,
        FilamentType.PETG to 26000.0,
        FilamentType.ABS to 24500.0,
        FilamentType.TPU to 38000.0
    ),
    var energyRate: Double = 120.0,
    val printers: MutableMap<PrinterType, PrinterSettings> = mutableMapOf(
        PrinterType.A1_COMBO to PrinterSettings(1200000.0, 5000, 0.35),
        PrinterType.A1_MINI to PrinterSettings(800000.0, 4000, 0.25)
    ),
    var marginPercentage: Double = 50.0,
    val quantityDiscounts: MutableMap<Int, Double> = mutableMapOf(25 to 5.0, 50 to 10.0, 75 to 15.0, 100 to 20.0),
    var friendDiscountPercentage: Double = 12.0,
    val designCosts: MutableMap<DesignType, Double> = mutableMapOf(
        DesignType.BASIC to 4000.0,
        DesignType.INTERMEDIATE to 9000.0,
        DesignType.COMPLEX to 18000.0
    )
)

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
    var totalComputed: Double = 0.0
)

enum class ExpenseCategory { FILAMENT, RESIN, MAINTENANCE, SHIPPING, OTHER }

data class Expense(
    val id: String,
    val title: String,
    val amount: Double,
    val date: String,
    val category: ExpenseCategory
)

data class AppState(
    val orders: List<Order> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val quoteSettings: QuoteSettings = QuoteSettings()
)
