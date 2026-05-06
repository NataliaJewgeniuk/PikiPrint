package com.example.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.local.PikiPrintDatabase
import com.example.app.data.repository.PikiPrintRepository
import com.example.app.models.AppState
import com.example.app.models.Expense
import com.example.app.models.Order
import com.example.app.models.QuoteSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*************** ViewModel principal ***************/
/*
    Antes este ViewModel guardaba todo en memoria.

    Ahora:
    - lee pedidos desde Room;
    - lee gastos desde Room;
    - lee configuración desde Room;
    - escribe cambios en Room.

    Las pantallas no necesitan saber que cambió la fuente de datos.
*/
class AppViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        PikiPrintDatabase.getDatabase(application)

    private val repository =
        PikiPrintRepository(database)

    private val _editingOrder = MutableStateFlow<Order?>(null)
    val editingOrder: StateFlow<Order?> = _editingOrder.asStateFlow()

    /*************** Estado global persistente ***************/
    /*
        Combina los flujos de Room en el AppState que ya usan las pantallas.
    */
    val state: StateFlow<AppState> =
        combine(
            repository.ordersFlow,
            repository.expensesFlow,
            repository.settingsFlow
        ) { orders, expenses, settings ->
            AppState(
                orders = orders,
                expenses = expenses,
                quoteSettings = settings
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppState()
        )

    init {
        viewModelScope.launch {
            repository.ensureDefaultSettings()
        }
    }

    /*************** Pedidos ***************/
    fun addOrder(order: Order) {
        viewModelScope.launch {
            repository.addOrder(order)
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            repository.updateOrder(order)
        }
    }

    fun deleteOrder(id: String) {
        viewModelScope.launch {
            repository.deleteOrder(id)
        }
    }

    /*************** Gastos ***************/
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    /*************** Configuración ***************/
    fun updateSettings(settings: QuoteSettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }

    /*************** Edición temporal ***************/
    /*
        Esto sigue siendo temporal.
        No hace falta persistir el pedido en edición porque solo indica
        qué pedido está abierto para modificar.
    */
    fun setEditingOrder(order: Order?) {
        _editingOrder.value = order
    }
}