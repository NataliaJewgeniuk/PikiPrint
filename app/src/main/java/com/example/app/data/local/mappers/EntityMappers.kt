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
import com.example.app.models.PrinterProfile
import com.example.app.models.PrinterSettings
import com.example.app.models.PrinterType
import com.example.app.models.QuoteSettings
import com.example.app.models.defaultPrinterProfiles
import com.example.app.models.defaultProfileId
import com.example.app.models.toPrinterSettings
import java.net.URLDecoder
import java.net.URLEncoder

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

/*************** Serialización simple de PrinterProfile ***************/
/*
    Evitamos agregar dependencias JSON en esta etapa.

    Formato por impresora:
    id|nameCodificado|price|lifespan|power|active

    Separador de impresoras:
    ;;
*/
private fun serializePrinterProfiles(
    profiles: List<PrinterProfile>
): String {
    return profiles.joinToString(";;") { profile ->
        val encodedName =
            URLEncoder.encode(profile.name, "UTF-8")

        listOf(
            profile.id,
            encodedName,
            profile.price.toString(),
            profile.lifespanHours.toString(),
            profile.powerKw.toString(),
            profile.isActive.toString()
        ).joinToString("|")
    }
}

private fun deserializePrinterProfiles(
    value: String
): MutableList<PrinterProfile> {
    if (value.isBlank()) {
        return mutableListOf()
    }

    return value
        .split(";;")
        .mapNotNull { rawProfile ->
            val parts = rawProfile.split("|")

            if (parts.size < 6) {
                null
            } else {
                try {
                    PrinterProfile(
                        id = parts[0],
                        name = URLDecoder.decode(parts[1], "UTF-8"),
                        price = parts[2].toDoubleOrNull() ?: 0.0,
                        lifespanHours = parts[3].toIntOrNull() ?: 0,
                        powerKw = parts[4].toDoubleOrNull() ?: 0.0,
                        isActive = parts[5].toBooleanStrictOrNull() ?: true
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }
        .toMutableList()
}

/*************** Order -> Entity ***************/
fun Order.toEntity(): OrderEntity {
    return OrderEntity(
        id = id,

        title = title,
        clientName = clientName,

        filament = filament.name,

        /*************** Campo heredado ***************/
        printer = printer.name,

        /*************** Campos dinámicos ***************/
        printerId = printerId,
        printerNameSnapshot = printerNameSnapshot.ifBlank {
            printer.label
        },

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
    val legacyPrinter =
        enumValueOrDefault(
            value = printer,
            default = PrinterType.A1_COMBO
        )

    val resolvedPrinterId =
        printerId.ifBlank {
            legacyPrinter.defaultProfileId
        }

    val resolvedPrinterNameSnapshot =
        printerNameSnapshot.ifBlank {
            legacyPrinter.label
        }

    return Order(
        id = id,

        title = title,
        clientName = clientName,

        filament = enumValueOrDefault(
            value = filament,
            default = FilamentType.PLA
        ),

        printer = legacyPrinter,

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
        totalFinal = totalFinal,

        printerId = resolvedPrinterId,
        printerNameSnapshot = resolvedPrinterNameSnapshot
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
    val profilesToSave =
        if (printerProfiles.isEmpty()) {
            defaultPrinterProfiles(printers)
        } else {
            printerProfiles
        }

    val a1ComboSettings =
        profilesToSave
            .firstOrNull { it.id == PrinterType.A1_COMBO.defaultProfileId }
            ?.toPrinterSettings()
            ?: printers[PrinterType.A1_COMBO]
            ?: PrinterSettings(
                price = 0.0,
                lifespanHours = 0,
                powerKw = 0.0
            )

    val a1MiniSettings =
        profilesToSave
            .firstOrNull { it.id == PrinterType.A1_MINI.defaultProfileId }
            ?.toPrinterSettings()
            ?: printers[PrinterType.A1_MINI]
            ?: PrinterSettings(
                price = 0.0,
                lifespanHours = 0,
                powerKw = 0.0
            )

    return QuoteSettingsEntity(
        id = 1,

        filamentPlaCost = filamentCost[FilamentType.PLA] ?: 0.0,
        filamentPetgCost = filamentCost[FilamentType.PETG] ?: 0.0,
        filamentFlexCost = filamentCost[FilamentType.FLEX] ?: 0.0,

        energyRate = energyRate,
        marginPercentage = marginPercentage,
        friendDiscountPercentage = friendDiscountPercentage,

        a1ComboPrice = a1ComboSettings.price,
        a1ComboLifespanHours = a1ComboSettings.lifespanHours,
        a1ComboPowerKw = a1ComboSettings.powerKw,

        a1MiniPrice = a1MiniSettings.price,
        a1MiniLifespanHours = a1MiniSettings.lifespanHours,
        a1MiniPowerKw = a1MiniSettings.powerKw,

        printerProfilesSerialized = serializePrinterProfiles(profilesToSave),

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
    val legacyPrinters =
        mutableMapOf(
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
        )

    val parsedProfiles =
        deserializePrinterProfiles(printerProfilesSerialized)

    val resolvedProfiles =
        if (parsedProfiles.isNotEmpty()) {
            parsedProfiles
        } else {
            defaultPrinterProfiles(legacyPrinters)
        }

    return QuoteSettings(
        filamentCost = mutableMapOf(
            FilamentType.PLA to filamentPlaCost,
            FilamentType.PETG to filamentPetgCost,
            FilamentType.FLEX to filamentFlexCost
        ),

        energyRate = energyRate,

        printers = legacyPrinters,

        printerProfiles = resolvedProfiles,

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