package com.example.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*************** Entidad Room: pedido ***************/
/*
    Guarda el pedido completo y su desglose histórico.

    Los enum heredados se guardan como String usando .name.

    Nuevos campos:
    - printerId
    - printerNameSnapshot

    Esto permite que la app use impresoras dinámicas sin perder el historial.
*/
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,

    val title: String,
    val clientName: String,

    val filament: String,

    /*************** Campo heredado ***************/
    val printer: String,

    /*************** Campos dinámicos ***************/
    val printerId: String,
    val printerNameSnapshot: String,

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
    val notes: String,

    /*************** Desglose histórico ***************/
    val subtotal: Double,
    val margenMonto: Double,
    val descuentoCantidad: Double,
    val descuentoAmigo: Double,
    val totalFinal: Double
)