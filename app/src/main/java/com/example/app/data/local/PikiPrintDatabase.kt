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
    version = 3 porque se agregaron:
    - orders.printerId
    - orders.printerNameSnapshot
    - quote_settings.printerProfilesSerialized

    Esto permite soportar impresoras dinámicas.
*/
@Database(
    entities = [
        OrderEntity::class,
        ExpenseEntity::class,
        QuoteSettingsEntity::class
    ],
    version = 3,
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

        /*************** Migración 2 -> 3 ***************/
        /*
            Agrega soporte para impresoras dinámicas sin eliminar el campo heredado printer.
        */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE orders ADD COLUMN printerId TEXT NOT NULL DEFAULT ''"
                )

                database.execSQL(
                    "ALTER TABLE orders ADD COLUMN printerNameSnapshot TEXT NOT NULL DEFAULT ''"
                )

                database.execSQL(
                    """
                    UPDATE orders
                    SET printerId =
                        CASE printer
                            WHEN 'A1_COMBO' THEN 'printer_a1_combo'
                            WHEN 'A1_MINI' THEN 'printer_a1_mini'
                            ELSE 'printer_a1_combo'
                        END
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    UPDATE orders
                    SET printerNameSnapshot =
                        CASE printer
                            WHEN 'A1_COMBO' THEN 'A1 Combo'
                            WHEN 'A1_MINI' THEN 'A1 Mini'
                            ELSE 'A1 Combo'
                        END
                    """.trimIndent()
                )

                database.execSQL(
                    "ALTER TABLE quote_settings ADD COLUMN printerProfilesSerialized TEXT NOT NULL DEFAULT ''"
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
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}