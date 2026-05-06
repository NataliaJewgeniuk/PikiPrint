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

    /*************** Número inicial de pedido ***************/
    /*
        El primer pedido visible será el 159763.

        A partir de ahí, cada pedido nuevo toma el siguiente número:
        159763, 159764, 159765, etc.
    */
    private val firstOrderNumber = 159763

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val _editingOrder = MutableStateFlow<Order?>(null)
    val editingOrder: StateFlow<Order?> = _editingOrder.asStateFlow()

    /*************** Agregar pedido ***************/
    /*
        Si el pedido viene sin número visible, orderNumber = 0,
        se le asigna automáticamente el próximo número consecutivo.

        Esto evita depender del UUID, que sirve internamente pero no es lindo
        para mostrarle al cliente o para identificar pedidos.
    */
    fun addOrder(order: Order) {
        _state.update { currentState ->
            val nextNumber = getNextOrderNumber(currentState.orders)

            val orderWithNumber = if (order.orderNumber <= 0) {
                order.copy(orderNumber = nextNumber)
            } else {
                order
            }

            currentState.copy(
                orders = currentState.orders + orderWithNumber
            )
        }
    }

    /*************** Actualizar pedido ***************/
    /*
        Cuando se edita un pedido, se conserva su número original.

        Esto es importante porque QuotesScreen reconstruye el Order editado.
        Si el orderNumber viniera en 0 por compatibilidad, acá recuperamos
        el número anterior y evitamos pisarlo.
    */
    fun updateOrder(order: Order) {
        _state.update { currentState ->
            currentState.copy(
                orders = currentState.orders.map { existingOrder ->
                    if (existingOrder.id == order.id) {
                        if (order.orderNumber <= 0) {
                            order.copy(orderNumber = existingOrder.orderNumber)
                        } else {
                            order
                        }
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
            it.copy(expenses = it.expenses + expense)
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
            it.copy(quoteSettings = settings)
        }
    }

    fun setEditingOrder(order: Order?) {
        _editingOrder.value = order
    }

    /*************** Obtener próximo número de pedido ***************/
    /*
        Busca el número más alto ya usado.

        Si todavía no hay pedidos numerados, arranca en 159763.
    */
    private fun getNextOrderNumber(
        orders: List<Order>
    ): Int {
        val maxExistingNumber = orders.maxOfOrNull { it.orderNumber } ?: 0

        return if (maxExistingNumber < firstOrderNumber) {
            firstOrderNumber
        } else {
            maxExistingNumber + 1
        }
    }
}