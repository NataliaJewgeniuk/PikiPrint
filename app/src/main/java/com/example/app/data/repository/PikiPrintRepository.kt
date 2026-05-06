package com.example.app.data.repository

import com.example.app.data.local.PikiPrintDatabase
import com.example.app.data.local.mappers.toDomain
import com.example.app.data.local.mappers.toEntity
import com.example.app.models.Expense
import com.example.app.models.Order
import com.example.app.models.QuoteSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*************** Repositorio principal ***************/
/*
    El repositorio es el puente entre Room y el ViewModel.

    Las pantallas no conocen Room.
    Solo hablan con AppViewModel.
*/
class PikiPrintRepository(
    private val database: PikiPrintDatabase
) {
    private val orderDao = database.orderDao()
    private val expenseDao = database.expenseDao()
    private val settingsDao = database.settingsDao()

    /*************** Flujos observables ***************/
    val ordersFlow: Flow<List<Order>> =
        orderDao.observeOrders().map { entities ->
            entities.map { it.toDomain() }
        }

    val expensesFlow: Flow<List<Expense>> =
        expenseDao.observeExpenses().map { entities ->
            entities.map { it.toDomain() }
        }

    val settingsFlow: Flow<QuoteSettings> =
        settingsDao.observeSettings().map { entity ->
            entity?.toDomain() ?: QuoteSettings()
        }

    /*************** Inicializar configuración por defecto ***************/
    /*
        Si todavía no existe configuración guardada, se escribe la default.
        Esto permite que SettingsScreen arranque con valores reales desde Room.
    */
    suspend fun ensureDefaultSettings() {
        val current = settingsDao.getSettingsOnce()

        if (current == null) {
            settingsDao.upsertSettings(
                QuoteSettings().toEntity()
            )
        }
    }

    /*************** Pedidos ***************/
    suspend fun addOrder(order: Order) {
        orderDao.upsertOrder(order.toEntity())
    }

    suspend fun updateOrder(order: Order) {
        orderDao.upsertOrder(order.toEntity())
    }

    suspend fun deleteOrder(id: String) {
        orderDao.deleteOrderById(id)
    }

    /*************** Gastos ***************/
    suspend fun addExpense(expense: Expense) {
        expenseDao.upsertExpense(expense.toEntity())
    }

    suspend fun deleteExpense(id: String) {
        expenseDao.deleteExpenseById(id)
    }

    /*************** Configuración ***************/
    suspend fun updateSettings(settings: QuoteSettings) {
        settingsDao.upsertSettings(settings.toEntity())
    }
}