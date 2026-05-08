package com.example.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*************** Entidad Room: configuración ***************/
/*
    Guardamos una sola fila con id = 1.

    Se mantienen columnas heredadas para A1 Combo y A1 Mini,
    pero se suma printerProfilesSerialized para la flota dinámica.
*/
@Entity(tableName = "quote_settings")
data class QuoteSettingsEntity(
    @PrimaryKey
    val id: Int = 1,

    /*************** Filamentos ***************/
    val filamentPlaCost: Double,
    val filamentPetgCost: Double,
    val filamentFlexCost: Double,

    /*************** Energía y margen ***************/
    val energyRate: Double,
    val marginPercentage: Double,
    val friendDiscountPercentage: Double,

    /*************** A1 Combo heredada ***************/
    val a1ComboPrice: Double,
    val a1ComboLifespanHours: Int,
    val a1ComboPowerKw: Double,

    /*************** A1 Mini heredada ***************/
    val a1MiniPrice: Double,
    val a1MiniLifespanHours: Int,
    val a1MiniPowerKw: Double,

    /*************** Flota dinámica ***************/
    val printerProfilesSerialized: String,

    /*************** Descuentos por cantidad ***************/
    val discount25: Double,
    val discount50: Double,
    val discount75: Double,
    val discount100: Double,

    /*************** Costos de diseño ***************/
    val designExternalCost: Double,
    val designOwnCost: Double,
    val designDetailsCost: Double,
    val designModificationsCost: Double
)