package com.example.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app.models.Order
import com.example.app.models.OrderStatus
import com.example.app.models.PaymentType
import com.example.app.viewmodels.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: AppViewModel,
    onGoToNewQuote: () -> Unit,
    onGoToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var selectedOrder by remember {
        mutableStateOf<Order?>(null)
    }

    var search by remember {
        mutableStateOf("")
    }

    var statusFilter by remember {
        mutableStateOf("Todos")
    }

    val filtered = state.orders.filter { order ->
        val matchesStatus =
            statusFilter == "Todos" ||
                    order.status.label == statusFilter

        val matchesSearch =
            order.title.contains(search, ignoreCase = true) ||
                    order.clientName.contains(search, ignoreCase = true) ||
                    order.filament.label.contains(search, ignoreCase = true)

        matchesStatus && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📦 Pedidos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row {
                Button(
                    onClick = onGoToSettings
                ) {
                    Text("Ajustes")
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.setEditingOrder(null)
                        onGoToNewQuote()
                    }
                ) {
                    Text("Nuevo")
                }
            }
        }

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = {
                Text("Buscar por título, cliente o material")
            },
            shape = RoundedCornerShape(20.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Todos",
                "Pendiente",
                "En marcha",
                "Terminado",
                "Cancelado"
            ).forEach { status ->
                FilterChip(
                    selected = statusFilter == status,
                    onClick = {
                        statusFilter = status
                    },
                    label = {
                        Text(status)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedOrder = order
                        },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = order.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text("Cliente: ${order.clientName}")
                        Text("Entrega: ${order.deliveryDate.ifBlank { "Sin fecha" }}")
                        Text("Total: ARS ${"%.2f".format(order.totalComputed)}")

                        AssistChip(
                            onClick = {},
                            label = {
                                Text(order.status.label)
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = statusColor(order.status)
                            )
                        )
                    }
                }
            }
        }
    }

    selectedOrder?.let { order ->
        AlertDialog(
            onDismissRequest = {
                selectedOrder = null
            },
            title = {
                Text(order.title)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Cliente: ${order.clientName}")
                    Text("Material: ${order.filament.label}")
                    Text("Color: ${order.color}")
                    Text("Acabado: ${order.finishType.label}")
                    Text("Cantidad: ${order.quantity}")
                    Text("Fecha presupuesto: ${order.quoteDate}")
                    Text("Validez: ${order.validityDays} días")
                    Text("Plazo entrega: ${order.deliveryBusinessDays} días hábiles")
                    Text("Entrega estimada: ${order.deliveryDate}")
                    Text("Pago: ${order.paymentType.label}")

                    if (order.paymentType == PaymentType.DEPOSIT) {
                        Text("Seña: ${order.depositPercentage}%")
                    }

                    if (order.notes.isNotBlank()) {
                        Text("Notas: ${order.notes}")
                    }

                    Text("Total: ARS ${"%.2f".format(order.totalComputed)}")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setEditingOrder(order)
                        selectedOrder = null
                        onGoToNewQuote()
                    }
                ) {
                    Text("Editar")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            val next = when (order.status) {
                                OrderStatus.PENDING -> OrderStatus.PRINTING
                                OrderStatus.PRINTING -> OrderStatus.DONE
                                OrderStatus.DONE -> OrderStatus.CANCELED
                                OrderStatus.CANCELED -> OrderStatus.PENDING
                            }

                            viewModel.updateOrder(
                                order.copy(status = next)
                            )

                            selectedOrder = null
                        }
                    ) {
                        Text("Cambiar Estado")
                    }

                    TextButton(
                        onClick = {
                            selectedOrder = null
                        }
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        )
    }
}

private fun statusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.PENDING -> Color(0xFFE5E7EB)
        OrderStatus.PRINTING -> Color(0xFFFFE4C7)
        OrderStatus.DONE -> Color(0xFFD1FAE5)
        OrderStatus.CANCELED -> Color(0xFFFECACA)
    }
}