package com.example.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*************** Entidad Room: pedido ***************/
/*
    Esta tabla guarda los pedidos/presupuestos.

    Los enum se guardan como String usando name:
    - FilamentType.PLA -> "PLA"
    - OrderStatus.PENDING -> "PENDING"

    Después se reconstruyen en los mappers.
*/
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,

    val title: String,
    val clientName: String,

    val filament: String,
    val printer: String,

    val printTimeHours: Int,
    val printTimeMinutes: Int,
    val weightGramsPerUnit: Int,
    val quantity: Int,

    val designType: String,
    val color: String,

    val isFriend: Boolean,

    val status: String,
    val createdAt: String,

    val totalComputed: Double,

    val quoteDate: String,
    val validityDays: Int,
    val deliveryBusinessDays: Int,
    val deliveryDate: String,

    val paymentType: String,
    val depositPercentage: Int,

    val finishType: String,
    val notes: String
)