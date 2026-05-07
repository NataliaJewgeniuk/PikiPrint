package com.example.app.screens

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.app.models.Order
import com.example.app.models.OrderStatus
import com.example.app.models.PaymentType
import com.example.app.viewmodels.AppViewModel
import com.example.app.utils.shareQuoteImage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoFechaArgentina: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: AppViewModel,
    onGoToNewQuote: () -> Unit,
    onGoToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var selectedOrder by remember {
        mutableStateOf<Order?>(null)
    }

    var orderToDelete by remember {
        mutableStateOf<Order?>(null)
    }

    var labelOrder by remember {
        mutableStateOf<Order?>(null)
    }

    var search by remember {
        mutableStateOf("")
    }

    var statusFilter by remember {
        mutableStateOf<OrderStatus?>(null)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val filteredOrders = state.orders
        .filter { order ->
            val text = search.trim()

            val matchesSearch =
                text.isBlank() ||
                        obtenerRefPedido(order).contains(text, ignoreCase = true) ||
                        order.title.contains(text, ignoreCase = true) ||
                        order.clientName.contains(text, ignoreCase = true) ||
                        order.filament.label.contains(text, ignoreCase = true) ||
                        order.printer.label.contains(text, ignoreCase = true) ||
                        order.color.contains(text, ignoreCase = true) ||
                        order.finishType.label.contains(text, ignoreCase = true) ||
                        order.status.label.contains(text, ignoreCase = true)

            val matchesStatus =
                statusFilter == null ||
                        order.status == statusFilter

            matchesSearch && matchesStatus
        }
        .sortedByDescending { it.createdAt }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 88.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "📝 Pedidos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "${filteredOrders.size} pedido(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onGoToSettings,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Ajustes")
                }

                Button(
                    onClick = {
                        viewModel.setEditingOrder(null)
                        onGoToNewQuote()
                    },
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Nuevo")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Buscar REF, pedido, cliente, material...")
            },
            singleLine = true,
            shape = RoundedCornerShape(22.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = statusFilter == null,
                onClick = {
                    statusFilter = null
                },
                label = {
                    Text("Todos")
                }
            )

            OrderStatus.values().forEach { status ->
                FilterChip(
                    selected = statusFilter == status,
                    onClick = {
                        statusFilter = status
                    },
                    label = {
                        Text(statusLabelWithIcon(status))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.orders.isEmpty()) {
            EmptyOrdersState(
                onNewOrder = {
                    viewModel.setEditingOrder(null)
                    onGoToNewQuote()
                }
            )
        } else if (filteredOrders.isEmpty()) {
            EmptySearchState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredOrders,
                    key = { it.id }
                ) { order ->
                    OrderCard(
                        order = order,
                        onClick = {
                            selectedOrder = order
                        }
                    )
                }
            }
        }
    }

    selectedOrder?.let { order ->
        ModalBottomSheet(
            onDismissRequest = {
                selectedOrder = null
            },
            sheetState = sheetState,
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {
            OrderDetailBottomSheet(
                order = order,
                onClose = {
                    selectedOrder = null
                },
                onEdit = {
                    viewModel.setEditingOrder(order)
                    selectedOrder = null
                    onGoToNewQuote()
                },
                onDelete = {
                    orderToDelete = order
                    selectedOrder = null
                },
                onGenerateLabel = {
                    labelOrder = order
                    selectedOrder = null
                },
                onShareQuote = {
                    shareQuoteImage(
                        context = context,
                        order = order,
                        settings = state.quoteSettings
                    )
                },
                onStatusChange = { newStatus ->
                    val updated = order.copy(status = newStatus)
                    viewModel.updateOrder(updated)
                    selectedOrder = updated
                }
            )
        }
    }

    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = {
                orderToDelete = null
            },
            title = {
                Text("Eliminar pedido")
            },
            text = {
                Text("¿Seguro que querés eliminar “${order.title}”? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOrder(order.id)
                        orderToDelete = null
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        orderToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    labelOrder?.let { order ->
        AlertDialog(
            onDismissRequest = {
                labelOrder = null
            },
            title = {
                Text("Etiqueta generada")
            },
            text = {
                PrintableLabelPreview(order = order)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        labelOrder = null
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 2.dp,
            color = Color(0xFFFFD1DC)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pedido REF ${obtenerRefPedido(order)}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF8D6E63)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = order.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "👤 ${order.clientName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "ARS ${"%.2f".format(order.totalComputed)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFB584E8)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusPill(status = order.status)

                SmallPill(
                    text = "${order.quantity} un."
                )

                SmallPill(
                    text = order.filament.label
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallPill(
                    text = order.printer.label
                )

                SmallPill(
                    text = formatearTiempoImpresion(
                        hours = order.printTimeHours,
                        minutes = order.printTimeMinutes
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Entrega: ${formatearFechaArgentina(order.deliveryDate.ifBlank { order.createdAt })}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailBottomSheet(
    order: Order,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onGenerateLabel: () -> Unit,
    onShareQuote: () -> Unit,
    onStatusChange: (OrderStatus) -> Unit
) {
    var selectedStatus by remember(order.id, order.status) {
        mutableStateOf(order.status)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Detalles Pedido 🌸",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = order.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "REF: ${obtenerRefPedido(order)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "👤 ${order.clientName}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = onClose
            ) {
                Text("Cerrar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Estado del pedido",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        StatusDropdown(
            selectedStatus = selectedStatus,
            onStatusSelected = { newStatus ->
                selectedStatus = newStatus
                onStatusChange(newStatus)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DetailCard {
            DetailRow(
                label = "Material",
                value = "${order.filament.label} - ${order.weightGramsPerUnit} g x ${order.quantity}"
            )

            DetailRow(
                label = "Impresora",
                value = order.printer.label
            )

            DetailRow(
                label = "Tiempo estimado",
                value = "${order.printTimeHours} h ${order.printTimeMinutes} min"
            )

            DetailRow(
                label = "Color",
                value = order.color
            )

            DetailRow(
                label = "Acabado",
                value = order.finishType.label
            )

            DetailRow(
                label = "Diseño",
                value = order.designType.label
            )

            Divider(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            DetailRow(
                label = "Fecha presupuesto",
                value = formatearFechaArgentina(order.quoteDate)
            )

            DetailRow(
                label = "Validez",
                value = "${order.validityDays} días"
            )

            DetailRow(
                label = "Plazo entrega",
                value = "${order.deliveryBusinessDays} días hábiles"
            )

            DetailRow(
                label = "Entrega estimada",
                value = formatearFechaArgentina(order.deliveryDate)
            )

            Divider(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            DetailRow(
                label = "Pago",
                value = order.paymentType.label
            )

            if (order.paymentType == PaymentType.DEPOSIT) {
                DetailRow(
                    label = "Seña",
                    value = "${order.depositPercentage}%"
                )
            }

            if (order.isFriend) {
                DetailRow(
                    label = "Descuento amigo",
                    value = "Aplicado"
                )
            }

            if (order.notes.isNotBlank()) {
                DetailRow(
                    label = "Notas",
                    value = order.notes
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            DetailRow(
                label = "TOTAL",
                value = "ARS ${"%.2f".format(order.totalComputed)}",
                highlight = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onShareQuote,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB584E8)
            )
        ) {
            Text("📤 Compartir Presupuesto")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onGenerateLabel,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF8BA7)
            )
        ) {
            Text("🏷️ Generar Etiqueta")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("📝 Editar Presupuesto")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Eliminar Pedido")
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusDropdown(
    selectedStatus: OrderStatus,
    onStatusSelected: (OrderStatus) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = statusLabelWithIcon(selectedStatus),
            onValueChange = {},
            readOnly = true,
            label = {
                Text("Estado")
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            OrderStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(statusLabelWithIcon(status))
                    },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF5F7)
        ),
        border = BorderStroke(
            width = 2.dp,
            color = Color(0xFFFFD1DC)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            modifier = Modifier.weight(1.2f),
            fontWeight = if (highlight) {
                FontWeight.ExtraBold
            } else {
                FontWeight.Bold
            },
            color = if (highlight) {
                Color(0xFFB584E8)
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun PrintableLabelPreview(
    order: Order
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(0.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = order.title,
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "Cliente: ${order.clientName}",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Entrega",
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Text(
                                text = formatearFechaArgentina(order.deliveryDate),
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Divider(
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LabelMiniBlock(
                            title = "Material",
                            value = order.filament.label
                        )

                        LabelMiniBlock(
                            title = "Color",
                            value = order.color
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LabelMiniBlock(
                            title = "Cantidad",
                            value = "${order.quantity} un."
                        )

                        LabelMiniBlock(
                            title = "Total",
                            value = "ARS ${"%.2f".format(order.totalComputed)}"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "PikiPrint 3D // PEDIDO REF: ${obtenerRefPedido(order)}",
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun LabelMiniBlock(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier.width(120.dp)
    ) {
        Text(
            text = title,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = value,
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyOrdersState(
    onNewOrder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🥺",
            style = MaterialTheme.typography.displayMedium
        )

        Text(
            text = "No hay pedidos todavía",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Creá tu primer presupuesto alegre 🌸",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNewOrder,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Nuevo Pedido")
        }
    }
}

@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🕵️‍♀️",
            style = MaterialTheme.typography.displayMedium
        )

        Text(
            text = "No se encontraron resultados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun StatusPill(
    status: OrderStatus
) {
    SmallPill(
        text = statusLabelWithIcon(status),
        background = statusBackgroundColor(status),
        foreground = statusTextColor(status)
    )
}

@Composable
private fun SmallPill(
    text: String,
    background: Color = Color(0xFFFFF5F7),
    foreground: Color = Color(0xFF5D4E60)
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = foreground,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun statusLabelWithIcon(
    status: OrderStatus
): String {
    return when (status) {
        OrderStatus.PENDING -> "⏳ Pendiente"
        OrderStatus.PRINTING -> "🖨️ En marcha"
        OrderStatus.DONE -> "✨ Terminado"
        OrderStatus.CANCELED -> "❌ Cancelado"
    }
}

private fun statusBackgroundColor(
    status: OrderStatus
): Color {
    return when (status) {
        OrderStatus.PENDING -> Color(0xFFFFF9E6)
        OrderStatus.PRINTING -> Color(0xFFF0F7FF)
        OrderStatus.DONE -> Color(0xFFF0FFF4)
        OrderStatus.CANCELED -> Color(0xFFFFF1F2)
    }
}

private fun statusTextColor(
    status: OrderStatus
): Color {
    return when (status) {
        OrderStatus.PENDING -> Color(0xFFB0892B)
        OrderStatus.PRINTING -> Color(0xFF2563EB)
        OrderStatus.DONE -> Color(0xFF2D5A27)
        OrderStatus.CANCELED -> Color(0xFFE11D48)
    }
}

private fun obtenerRefPedido(
    order: Order
): String {
    return order.id
        .take(8)
        .uppercase()
}

private fun formatearTiempoImpresion(
    hours: Int,
    minutes: Int
): String {
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        minutes > 0 -> "${minutes}m"
        else -> "0m"
    }
}

private fun formatearFechaArgentina(
    value: String
): String {
    if (value.isBlank()) {
        return "Sin fecha"
    }

    return try {
        LocalDate
            .parse(value)
            .format(formatoFechaArgentina)
    } catch (e: Exception) {
        value
    }
}