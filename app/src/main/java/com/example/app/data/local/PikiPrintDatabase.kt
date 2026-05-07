package com.example.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app.data.local.dao.ExpenseDao
import com.example.app.data.local.dao.OrderDao
import com.example.app.data.local.dao.SettingsDao
import com.example.app.data.local.entities.ExpenseEntity
import com.example.app.data.local.entities.OrderEntity
import com.example.app.data.local.entities.QuoteSettingsEntity

/*************** Base de datos Room ***************/
/*
    version = 2 porque se agregaron columnas de desglose histórico
    a la tabla orders.
*/
@Database(
    entities = [
        OrderEntity::class,
        ExpenseEntity::class,
        QuoteSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PikiPrintDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: PikiPrintDatabase? = null

        /*************** Migración 1 -> 2 ***************/
        /*
            Agrega los valores históricos del presupuesto.

            Para pedidos ya existentes:
            - subtotal = 0
            - margenMonto = 0
            - descuentos = 0
            - totalFinal = 0

            Los pedidos nuevos ya van a guardar estos valores correctamente.
        */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE orders ADD COLUMN subtotal REAL NOT NULL DEFAULT 0.0"
                )

                database.execSQL(
                    "ALTER TABLE orders ADD COLUMN margenMonto REAL NOT NULL DEFAULT 0.0"
                )

                database.execSQL(
                    "ALTER TABLE orders ADD COLUMN descuentoCantidad REAL NOT NULL DEFAULT 0.0"
                )

                database.execSQL(
                    "ALTER TABLE orders ADD COLUMN descuentoAmigo REAL NOT NULL DEFAULT 0.0"
                )

                database.execSQL(
                    "ALTER TABLE orders ADD COLUMN totalFinal REAL NOT NULL DEFAULT 0.0"
                )
            }
        }

        fun getDatabase(context: Context): PikiPrintDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PikiPrintDatabase::class.java,
                    "pikiprint_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}