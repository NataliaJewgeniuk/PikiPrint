package com.example.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*************** Entidad Room: configuración ***************/
/*
    Guardamos una sola fila con id = 1.

    En vez de guardar mapas en Room, se guardan columnas simples.
    Esto hace que la base sea más clara y fácil de migrar después.
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

    /*************** A1 Combo ***************/
    val a1ComboPrice: Double,
    val a1ComboLifespanHours: Int,
    val a1ComboPowerKw: Double,

    /*************** A1 Mini ***************/
    val a1MiniPrice: Double,
    val a1MiniLifespanHours: Int,
    val a1MiniPowerKw: Double,

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