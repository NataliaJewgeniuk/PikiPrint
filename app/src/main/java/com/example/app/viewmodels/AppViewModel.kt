package com.example.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.local.PikiPrintDatabase
import com.example.app.data.repository.PikiPrintRepository
import com.example.app.models.AppState
import com.example.app.models.Expense
import com.example.app.models.Order
import com.example.app.models.PrinterProfile
import com.example.app.models.QuoteSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/*************** ViewModel principal ***************/
/*
    Lee pedidos, gastos y configuración desde Room.
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

    /*************** Agregar impresora dinámica ***************/
    fun addPrinterProfile(
        name: String,
        price: Double,
        lifespanHours: Int,
        powerKw: Double
    ) {
        viewModelScope.launch {
            val currentSettings =
                state.value.quoteSettings

            val updatedProfiles =
                currentSettings.printerProfiles
                    .toMutableList()
                    .apply {
                        add(
                            PrinterProfile(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                price = price,
                                lifespanHours = lifespanHours,
                                powerKw = powerKw,
                                isActive = true
                            )
                        )
                    }

            repository.updateSettings(
                currentSettings.copy(
                    printerProfiles = updatedProfiles
                )
            )
        }
    }

    /*************** Actualizar impresora dinámica ***************/
    fun updatePrinterProfile(
        updatedProfile: PrinterProfile
    ) {
        viewModelScope.launch {
            val currentSettings =
                state.value.quoteSettings

            val updatedProfiles =
                currentSettings.printerProfiles
                    .map { profile ->
                        if (profile.id == updatedProfile.id) {
                            updatedProfile
                        } else {
                            profile
                        }
                    }
                    .toMutableList()

            repository.updateSettings(
                currentSettings.copy(
                    printerProfiles = updatedProfiles
                )
            )
        }
    }

    /*************** Desactivar impresora dinámica ***************/
    /*
        No se elimina físicamente para no romper pedidos históricos.
    */
    fun deactivatePrinterProfile(
        printerId: String
    ) {
        viewModelScope.launch {
            val currentSettings =
                state.value.quoteSettings

            val updatedProfiles =
                currentSettings.printerProfiles
                    .map { profile ->
                        if (profile.id == printerId) {
                            profile.copy(isActive = false)
                        } else {
                            profile
                        }
                    }
                    .toMutableList()

            repository.updateSettings(
                currentSettings.copy(
                    printerProfiles = updatedProfiles
                )
            )
        }
    }

    /*************** Reactivar impresora dinámica ***************/
    fun reactivatePrinterProfile(
        printerId: String
    ) {
        viewModelScope.launch {
            val currentSettings =
                state.value.quoteSettings

            val updatedProfiles =
                currentSettings.printerProfiles
                    .map { profile ->
                        if (profile.id == printerId) {
                            profile.copy(isActive = true)
                        } else {
                            profile
                        }
                    }
                    .toMutableList()

            repository.updateSettings(
                currentSettings.copy(
                    printerProfiles = updatedProfiles
                )
            )
        }
    }

    /*************** Edición temporal ***************/
    fun setEditingOrder(order: Order?) {
        _editingOrder.value = order
    }
}