package com.example.app.viewmodels

import androidx.lifecycle.ViewModel
import com.example.app.models.AppState
import com.example.app.models.Expense
import com.example.app.models.Order
import com.example.app.models.QuoteSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val _editingOrder = MutableStateFlow<Order?>(null)
    val editingOrder: StateFlow<Order?> = _editingOrder.asStateFlow()

    fun addOrder(order: Order) {
        _state.update {
            it.copy(
                orders = it.orders + order
            )
        }
    }

    fun updateOrder(order: Order) {
        _state.update {
            it.copy(
                orders = it.orders.map { existingOrder ->
                    if (existingOrder.id == order.id) {
                        order
                    } else {
                        existingOrder
                    }
                }
            )
        }
    }

    fun deleteOrder(id: String) {
        _state.update {
            it.copy(
                orders = it.orders.filter { order -> order.id != id }
            )
        }
    }

    fun addExpense(expense: Expense) {
        _state.update {
            it.copy(
                expenses = it.expenses + expense
            )
        }
    }

    fun deleteExpense(id: String) {
        _state.update {
            it.copy(
                expenses = it.expenses.filter { expense -> expense.id != id }
            )
        }
    }

    fun updateSettings(settings: QuoteSettings) {
        _state.update {
            it.copy(
                quoteSettings = settings
            )
        }
    }

    fun setEditingOrder(order: Order?) {
        _editingOrder.value = order
    }
}