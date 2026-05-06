package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.models.DesignType
import com.example.app.models.FilamentType
import com.example.app.models.Order
import com.example.app.models.OrderStatus
import com.example.app.models.PaymentType
import com.example.app.models.PrinterType
import com.example.app.utils.Calculator
import com.example.app.utils.QuoteBreakdown
import com.example.app.viewmodels.AppViewModel
import java.util.UUID

@Composable
fun QuotesScreen(viewModel: AppViewModel, onGoToOrders: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val editingOrder by viewModel.editingOrder.collectAsState()
    
    var title by remember { mutableStateOf(editingOrder?.title ?: "") }
    var clientName by remember { mutableStateOf(editingOrder?.clientName ?: "") }
    var printHours by remember { mutableStateOf(editingOrder?.printTimeHours?.toString() ?: "2") }
    var printMinutes by remember { mutableStateOf(editingOrder?.printTimeMinutes?.toString() ?: "30") }
    var quantity by remember { mutableStateOf(editingOrder?.quantity?.toString() ?: "1") }
    var weight by remember { mutableStateOf(editingOrder?.weightGramsPerUnit?.toString() ?: "100") }

    var breakdown by remember { mutableStateOf<QuoteBreakdown?>(null) }
    var currentOrderData by remember { mutableStateOf<Order?>(null) }

    if (breakdown != null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Presupuesto Final", style = MaterialTheme.typography.headlineMedium)
            Text("Total: $${breakdown!!.totalFinal}", style = MaterialTheme.typography.displaySmall)
            Button(onClick = { 
                currentOrderData?.let { 
                    if(editingOrder == null) viewModel.addOrder(it) else viewModel.updateOrder(it)
                }
                viewModel.setEditingOrder(null)
                onGoToOrders()
            }) { Text("Guardar Pedido") }
            Button(onClick = { breakdown = null }) { Text("Volver") }
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Nuevo Presupuesto", style = MaterialTheme.typography.headlineMedium) }
        item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Trabajo") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Cliente") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = printHours, onValueChange = { printHours = it }, label = { Text("Horas") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = printMinutes, onValueChange = { printMinutes = it }, label = { Text("Minutos") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Peso Unidad (g)") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth()) }
        item {
            Button(onClick = {
                val order = Order(
                    id = editingOrder?.id ?: UUID.randomUUID().toString(),
                    title = title, clientName = clientName, filament = FilamentType.PLA, printer = PrinterType.A1_COMBO,
                    printTimeHours = printHours.toIntOrNull() ?: 0, printTimeMinutes = printMinutes.toIntOrNull() ?: 0,
                    weightGramsPerUnit = weight.toIntOrNull() ?: 0, quantity = quantity.toIntOrNull() ?: 1,
                    designType = DesignType.EXTERNAL, color = "", quoteDate = "", validityDays = 15, deliveryDate = "",
                    paymentType = PaymentType.FULL, depositPercentage = 0.0, finishType = "", isFriend = false,
                    status = OrderStatus.PENDING, createdAt = ""
                )
                val res = Calculator.calculateQuote(order, state.quoteSettings)
                order.totalComputed = res.totalFinal
                currentOrderData = order
                breakdown = res
            }, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Text("Calcular")
            }
        }
    }
}