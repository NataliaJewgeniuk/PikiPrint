package com.example.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*************** Entidad Room: gasto ***************/
/*
    Aunque Gastos no sea prioridad visual, lo persistimos para no romper
    la pantalla existente y para que no se pierdan datos.
*/
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: Double,
    val date: String,
    val category: String
)