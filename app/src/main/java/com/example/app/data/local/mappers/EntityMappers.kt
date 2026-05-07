package com.example.app.data.local.mappers

import com.example.app.data.local.entities.ExpenseEntity
import com.example.app.data.local.entities.OrderEntity
import com.example.app.data.local.entities.QuoteSettingsEntity
import com.example.app.models.DesignType
import com.example.app.models.Expense
import com.example.app.models.ExpenseCategory
import com.example.app.models.FilamentType
import com.example.app.models.FinishType
import com.example.app.models.Order
import com.example.app.models.OrderStatus
import com.example.app.models.PaymentType
import com.example.app.models.PrinterSettings
import com.example.app.models.PrinterType
import com.example.app.models.QuoteSettings

/*************** Helpers para leer enums de forma segura ***************/
private inline fun <reified T : Enum<T>> enumValueOrDefault(
    value: String,
    default: T
): T {
    return try {
        enumValueOf<T>(value)
    } catch (e: Exception) {
        default
    }
}

/*************** Order -> Entity ***************/
fun Order.toEntity(): OrderEntity {
    return OrderEntity(
        id = id,

        title = title,
        clientName = clientName,

        filament = filament.name,
        printer = printer.name,

        printTimeHours = printTimeHours,
        printTimeMinutes = printTimeMinutes,
        weightGramsPerUnit = weightGramsPerUnit,
        quantity = quantity,

        designType = designType.name,
        color = color,

        isFriend = isFriend,

        status = status.name,
        createdAt = createdAt,

        totalComputed = totalComputed,

        quoteDate = quoteDate,
        validityDays = validityDays,
        deliveryBusinessDays = deliveryBusinessDays,
        deliveryDate = deliveryDate,

        paymentType = paymentType.name,
        depositPercentage = depositPercentage,

        finishType = finishType.name,
        notes = notes,

        subtotal = subtotal,
        margenMonto = margenMonto,
        descuentoCantidad = descuentoCantidad,
        descuentoAmigo = descuentoAmigo,
        totalFinal = totalFinal
    )
}

/*************** Entity -> Order ***************/
fun OrderEntity.toDomain(): Order {
    return Order(
        id = id,

        title = title,
        clientName = clientName,

        filament = enumValueOrDefault(
            value = filament,
            default = FilamentType.PLA
        ),

        printer = enumValueOrDefault(
            value = printer,
            default = PrinterType.A1_COMBO
        ),

        printTimeHours = printTimeHours,
        printTimeMinutes = printTimeMinutes,
        weightGramsPerUnit = weightGramsPerUnit,
        quantity = quantity,

        designType = enumValueOrDefault(
            value = designType,
            default = DesignType.EXTERNAL
        ),

        color = color,

        isFriend = isFriend,

        status = enumValueOrDefault(
            value = status,
            default = OrderStatus.PENDING
        ),

        createdAt = createdAt,

        totalComputed = totalComputed,

        quoteDate = quoteDate,
        validityDays = validityDays,
        deliveryBusinessDays = deliveryBusinessDays,
        deliveryDate = deliveryDate,

        paymentType = enumValueOrDefault(
            value = paymentType,
            default = PaymentType.FULL
        ),

        depositPercentage = depositPercentage,

        finishType = enumValueOrDefault(
            value = finishType,
            default = FinishType.VISIBLE_LINES
        ),

        notes = notes,

        subtotal = subtotal,
        margenMonto = margenMonto,
        descuentoCantidad = descuentoCantidad,
        descuentoAmigo = descuentoAmigo,
        totalFinal = totalFinal
    )
}

/*************** Expense -> Entity ***************/
fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        title = title,
        amount = amount,
        date = date,
        category = category.name
    )
}

/*************** Entity -> Expense ***************/
fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        title = title,
        amount = amount,
        date = date,
        category = enumValueOrDefault(
            value = category,
            default = ExpenseCategory.OTHER
        )
    )
}

/*************** QuoteSettings -> Entity ***************/
fun QuoteSettings.toEntity(): QuoteSettingsEntity {
    return QuoteSettingsEntity(
        id = 1,

        filamentPlaCost = filamentCost[FilamentType.PLA] ?: 0.0,
        filamentPetgCost = filamentCost[FilamentType.PETG] ?: 0.0,
        filamentFlexCost = filamentCost[FilamentType.FLEX] ?: 0.0,

        energyRate = energyRate,
        marginPercentage = marginPercentage,
        friendDiscountPercentage = friendDiscountPercentage,

        a1ComboPrice = printers[PrinterType.A1_COMBO]?.price ?: 0.0,
        a1ComboLifespanHours = printers[PrinterType.A1_COMBO]?.lifespanHours ?: 0,
        a1ComboPowerKw = printers[PrinterType.A1_COMBO]?.powerKw ?: 0.0,

        a1MiniPrice = printers[PrinterType.A1_MINI]?.price ?: 0.0,
        a1MiniLifespanHours = printers[PrinterType.A1_MINI]?.lifespanHours ?: 0,
        a1MiniPowerKw = printers[PrinterType.A1_MINI]?.powerKw ?: 0.0,

        discount25 = quantityDiscounts[25] ?: 0.0,
        discount50 = quantityDiscounts[50] ?: 0.0,
        discount75 = quantityDiscounts[75] ?: 0.0,
        discount100 = quantityDiscounts[100] ?: 0.0,

        designExternalCost = designCosts[DesignType.EXTERNAL] ?: 0.0,
        designOwnCost = designCosts[DesignType.OWN] ?: 0.0,
        designDetailsCost = designCosts[DesignType.DETAILS] ?: 0.0,
        designModificationsCost = designCosts[DesignType.MODIFICATIONS] ?: 0.0
    )
}

/*************** Entity -> QuoteSettings ***************/
fun QuoteSettingsEntity.toDomain(): QuoteSettings {
    return QuoteSettings(
        filamentCost = mutableMapOf(
            FilamentType.PLA to filamentPlaCost,
            FilamentType.PETG to filamentPetgCost,
            FilamentType.FLEX to filamentFlexCost
        ),

        energyRate = energyRate,

        printers = mutableMapOf(
            PrinterType.A1_COMBO to PrinterSettings(
                price = a1ComboPrice,
                lifespanHours = a1ComboLifespanHours,
                powerKw = a1ComboPowerKw
            ),
            PrinterType.A1_MINI to PrinterSettings(
                price = a1MiniPrice,
                lifespanHours = a1MiniLifespanHours,
                powerKw = a1MiniPowerKw
            )
        ),

        marginPercentage = marginPercentage,

        quantityDiscounts = mutableMapOf(
            25 to discount25,
            50 to discount50,
            75 to discount75,
            100 to discount100
        ),

        friendDiscountPercentage = friendDiscountPercentage,

        designCosts = mutableMapOf(
            DesignType.EXTERNAL to designExternalCost,
            DesignType.OWN to designOwnCost,
            DesignType.DETAILS to designDetailsCost,
            DesignType.MODIFICATIONS to designModificationsCost
        )
    )
}