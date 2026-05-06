package com.example.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.models.Order
import com.example.app.viewmodels.AppViewModel

@Composable
fun OrdersScreen(viewModel: AppViewModel, onGoToNewQuote: () -> Unit, onGoToSettings: () -> Unit) {
    val state by viewModel.state.collectAsState()
    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Pedidos", style = MaterialTheme.typography.headlineMedium)
            Row {
                Button(onClick = onGoToSettings, modifier = Modifier.padding(end = 8.dp)) { Text("Ajustes") }
                Button(onClick = { viewModel.setEditingOrder(null); onGoToNewQuote() }) { Text("Nuevo") }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn { 
            items(state.orders) { order ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { selectedOrder = order }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(order.title, style = MaterialTheme.typography.titleMedium)
                        Text("Cliente: ${order.clientName}")
                        Text("Total: $${order.totalComputed}")
                        Text("Estado: ${order.status.label}")
                    }
                }
            }
        }
    }

    selectedOrder?.let {
        AlertDialog(
            onDismissRequest = { selectedOrder = null },
            title = { Text(it.title) },
            text = { Text("Detalles de ${it.clientName}\nTotal: $${it.totalComputed}") },
            confirmButton = {
                Button(onClick = { selectedOrder = null }) { Text("Cerrar") }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.deleteOrder(it.id)
                    selectedOrder = null
                }) { Text("Eliminar") }
            }
        )
    }
}