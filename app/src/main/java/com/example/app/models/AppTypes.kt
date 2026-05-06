package com.example.app.models

enum class OrderStatus(val label: String) {
    PENDING("Pendiente"), PRINTING("En marcha"), DONE("Terminado"), CANCELED("Cancelado")
}

enum class FilamentType { PLA, PETG, FLEX }
enum class PrinterType(val label: String) { A1_COMBO("A1 Combo"), A1_MINI("A1 Mini") }
enum class DesignType { EXTERNAL, OWN, DETAILS, MODIFICATIONS }
enum class PaymentType { FULL, DEPOSIT }

data class PrinterSettings(var price: Double, var lifespanHours: Int, var powerKw: Double)

data class QuoteSettings(
    val filamentCost: MutableMap<FilamentType, Double> = mutableMapOf(FilamentType.PLA to 22.0, FilamentType.PETG to 25.0, FilamentType.FLEX to 40.0),
    var energyRate: Double = 0.15,
    val printers: MutableMap<PrinterType, PrinterSettings> = mutableMapOf(
        PrinterType.A1_COMBO to PrinterSettings(599.0, 5000, 0.35),
        PrinterType.A1_MINI to PrinterSettings(299.0, 4000, 0.25)
    ),
    var marginPercentage: Double = 50.0,
    val quantityDiscounts: MutableMap<Int, Double> = mutableMapOf(25 to 5.0, 50 to 10.0, 75 to 15.0, 100 to 20.0),
    var friendDiscountPercentage: Double = 15.0,
    val designCosts: MutableMap<DesignType, Double> = mutableMapOf(DesignType.EXTERNAL to 0.0, DesignType.OWN to 20.0, DesignType.DETAILS to 5.0, DesignType.MODIFICATIONS to 10.0)
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
    val quoteDate: String,
    val validityDays: Int,
    val deliveryDate: String,
    val paymentType: PaymentType,
    val depositPercentage: Double,
    val finishType: String,
    val isFriend: Boolean,
    val notes: String? = null,
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