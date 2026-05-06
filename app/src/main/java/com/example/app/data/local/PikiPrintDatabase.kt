package com.example.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.app.data.local.dao.ExpenseDao
import com.example.app.data.local.dao.OrderDao
import com.example.app.data.local.dao.SettingsDao
import com.example.app.data.local.entities.ExpenseEntity
import com.example.app.data.local.entities.OrderEntity
import com.example.app.data.local.entities.QuoteSettingsEntity

/*************** Base de datos Room ***************/
/*
    version = 1 porque es el primer esquema persistente de la app.

    Cuando cambiemos columnas o entidades en el futuro, vamos a subir
    la versión y crear migraciones.
*/
@Database(
    entities = [
        OrderEntity::class,
        ExpenseEntity::class,
        QuoteSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PikiPrintDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: PikiPrintDatabase? = null

        fun getDatabase(context: Context): PikiPrintDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PikiPrintDatabase::class.java,
                    "pikiprint_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}