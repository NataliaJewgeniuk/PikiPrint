package com.example.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.models.Order
import com.example.app.models.OrderStatus
import com.example.app.models.PaymentType
import com.example.app.models.QuoteSettings
import com.example.app.ui.components.PikiOutlinedButton
import com.example.app.ui.components.PikiPill
import com.example.app.ui.components.PikiPrimaryButton
import com.example.app.ui.components.PikiSecondaryButton
import com.example.app.ui.components.PikiStatusPill
import com.example.app.ui.components.PikiTextField
import com.example.app.ui.theme.PikiCanceledBg
import com.example.app.ui.theme.PikiCanceledText
import com.example.app.ui.theme.PikiClay
import com.example.app.ui.theme.PikiCreamLine
import com.example.app.ui.theme.PikiDoneBg
import com.example.app.ui.theme.PikiDoneText
import com.example.app.ui.theme.PikiLeaf
import com.example.app.ui.theme.PikiLeafDark
import com.example.app.ui.theme.PikiMutedText
import com.example.app.ui.theme.PikiPaper
import com.example.app.ui.theme.PikiPaperDark
import com.example.app.ui.theme.PikiPaperLight
import com.example.app.ui.theme.PikiPendingBg
import com.example.app.ui.theme.PikiPendingText
import com.example.app.ui.theme.PikiPrintingBg
import com.example.app.ui.theme.PikiPrintingText
import com.example.app.ui.theme.PikiSky
import com.example.app.ui.theme.PikiSkyDark
import com.example.app.ui.theme.PikiWhite
import com.example.app.ui.theme.PikiWood
import com.example.app.utils.Calculator
import com.example.app.utils.shareQuoteImage
import com.example.app.viewmodels.AppViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/*************** Formato argentino visible ***************/
/*
    Las fechas se guardan internamente como yyyy-MM-dd,
    pero se muestran como dd/MM/yyyy.
*/
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

    /*************** Filtrado de pedidos ***************/
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

    /*************** Pantalla principal ***************/
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 12.dp)
    ) {
        OrdersHeader(
            totalVisible = filteredOrders.size,
            totalOrders = state.orders.size,
            onNewOrder = {
                viewModel.setEditingOrder(null)
                onGoToNewQuote()
            },
            onSettings = onGoToSettings
        )

        Spacer(modifier = Modifier.height(14.dp))

        PikiSearchField(
            value = search,
            onValueChange = { search = it },
            onClear = { search = "" }
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatusFilterRow(
            selectedStatus = statusFilter,
            onStatusSelected = { status ->
                statusFilter = status
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(
                    items = filteredOrders,
                    key = { it.id }
                ) { order ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500)) + expandVertically(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkshopOrderCard(
                            order = order,
                            onClick = {
                                selectedOrder = order
                            }
                        )
                    }
                }
            }
        }
    }

    /*************** Detalle en BottomSheet ***************/
    selectedOrder?.let { order ->
        ModalBottomSheet(
            onDismissRequest = {
                selectedOrder = null
            },
            sheetState = sheetState,
            containerColor = PikiPaperLight,
            tonalElevation = 8.dp,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .width(56.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PikiCreamLine)
                )
            }
        ) {
            OrderDetailBottomSheet(
                order = order,
                settings = state.quoteSettings,
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

    /*************** Confirmación de eliminación ***************/
    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = {
                orderToDelete = null
            },
            containerColor = PikiPaperLight,
            shape = RoundedCornerShape(30.dp),
            title = {
                Text(
                    text = "Eliminar pedido",
                    color = PikiWood,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Text(
                    text = "¿Seguro que querés eliminar “${order.title}”? Esta acción no se puede deshacer.",
                    color = PikiMutedText,
                    fontWeight = FontWeight.SemiBold
                )
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
                        color = PikiCanceledText,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        orderToDelete = null
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = PikiWood,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    /*************** Etiqueta generada ***************/
    labelOrder?.let { order ->
        AlertDialog(
            onDismissRequest = {
                labelOrder = null
            },
            containerColor = PikiPaperLight,
            shape = RoundedCornerShape(30.dp),
            title = {
                Text(
                    text = "Etiqueta generada",
                    color = PikiWood,
                    fontWeight = FontWeight.ExtraBold
                )
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
                    Text(
                        text = "Cerrar",
                        color = PikiLeafDark,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        )
    }
}

/*************** Encabezado del mural ***************/
@Composable
private fun OrdersHeader(
    totalVisible: Int,
    totalOrders: Int,
    onNewOrder: () -> Unit,
    onSettings: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = PikiPaperLight),
        border = BorderStroke(2.dp, PikiCreamLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mural del Taller 🧺",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiWood
                )

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = PikiLeaf.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (totalOrders == 0) {
                            "Sin pedidos guardados"
                        } else {
                            " $totalVisible de $totalOrders pedidos "
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = PikiLeafDark,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier
                        .size(44.dp)
                        .background(PikiPaperDark, RoundedCornerShape(14.dp))
                ) {
                    Text("⚙️", fontSize = 20.sp)
                }

                Button(
                    onClick = onNewOrder,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PikiLeafDark,
                        contentColor = PikiWhite
                    ),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = "Nuevo",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

/*************** Filtros de estado ***************/
@Composable
private fun StatusFilterRow(
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedStatus == null,
            onClick = {
                onStatusSelected(null)
            },
            label = {
                Text(
                    text = "Todos",
                    fontWeight = FontWeight.ExtraBold
                )
            },
            shape = RoundedCornerShape(999.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PikiWood,
                selectedLabelColor = PikiWhite,
                containerColor = PikiPaperLight,
                labelColor = PikiMutedText
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedStatus == null,
                borderColor = PikiCreamLine,
                selectedBorderColor = PikiWood,
                borderWidth = 2.dp,
                selectedBorderWidth = 2.dp
            )
        )

        OrderStatus.values().forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = {
                    onStatusSelected(status)
                },
                label = {
                    Text(
                        text = statusLabelWithIcon(status),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                shape = RoundedCornerShape(999.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = statusBackgroundColor(status),
                    selectedLabelColor = statusTextColor(status),
                    containerColor = PikiPaperLight,
                    labelColor = PikiMutedText
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedStatus == status,
                    borderColor = PikiCreamLine,
                    selectedBorderColor = statusBackgroundColor(status),
                    borderWidth = 2.dp,
                    selectedBorderWidth = 2.dp
                )
            )
        }
    }
}

/*************** Ficha de inventario del pedido ***************/
@Composable
private fun WorkshopOrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = PikiWood.copy(alpha = 0.1f),
                spotColor = PikiLeafDark.copy(alpha = 0.2f)
            )
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiPaperLight
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiCreamLine
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            /*************** Header: REF y Estado ***************/
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PEDIDO #${obtenerRefPedido(order)}",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiClay
                )

                PikiStatusPill(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            /*************** Título y cliente ***************/
            Text(
                text = order.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWood,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "👤",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.clientName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PikiMutedText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            /*************** Info técnica en cápsulas ***************/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PikiPill(
                    text = "${order.quantity} un.",
                    background = PikiPaperDark,
                    foreground = PikiWood
                )

                PikiPill(
                    text = order.filament.label,
                    background = PikiSky.copy(alpha = 0.55f),
                    foreground = PikiSkyDark
                )

                /*************** Impresora del pedido ***************/
                PikiPill(
                    text = order.printer.label,
                    background = PikiLeaf.copy(alpha = 0.45f),
                    foreground = PikiLeafDark
                )

                PikiPill(
                    text = formatearTiempoImpresion(
                        hours = order.printTimeHours,
                        minutes = order.printTimeMinutes
                    ),
                    background = PikiPaperDark,
                    foreground = PikiWood
                )
            }

            /*************** Footer: Entrega y Total ***************/
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Entrega estimada",
                        style = MaterialTheme.typography.labelSmall,
                        color = PikiMutedText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatearFechaArgentina(order.deliveryDate.ifBlank { order.createdAt }),
                        style = MaterialTheme.typography.bodySmall,
                        color = PikiWood,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = PikiMutedText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ARS ${"%.2f".format(obtenerTotalHistorico(order))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiLeafDark
                    )
                }
            }
        }
    }
}

/*************** BottomSheet de detalle ***************/
/*************** BottomSheet de detalle ***************/
/*
    Hoja del pedido rediseñada.

    La estructura ahora es:
    - Hero visual del pedido
    - Producción
    - Comercial
    - Acciones

    Objetivo:
    que se sienta como una hoja de taller desplegada y no como una
    tabla plana de datos.
*/
/*************** BottomSheet de detalle ***************/
/*
    Versión inspirada en la web.

    Busca parecerse más al modal de OrdersView.tsx:
    - fondo crema;
    - tarjetas blancas;
    - bordes amplios;
    - título protagonista;
    - total destacado;
    - acciones grandes y blanditas.

    No se quita información: se reorganiza visualmente.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailBottomSheet(
    order: Order,
    settings: QuoteSettings,
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

    val breakdown = remember(order, settings) {
        Calculator.calculateQuote(order, settings)
    }

    val descuentosAplicados =
        order.descuentoCantidad + order.descuentoAmigo

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        /*************** Cabecera estilo modal web ***************/
        DetailTopBar(
            onClose = onClose
        )

        Spacer(modifier = Modifier.height(14.dp))

        /*************** Tarjeta principal del pedido ***************/
        OrderWebStyleHeroCard(
            order = order,
            selectedStatus = selectedStatus,
            onStatusSelected = { newStatus ->
                selectedStatus = newStatus
                onStatusChange(newStatus)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        /*************** Resumen del taller ***************/
        WebStyleInfoCard(
            title = "Resumen del taller",
            icon = "🛠️"
        ) {
            DetailRow(
                label = "Material",
                value = order.filament.label
            )

            DetailRow(
                label = "Peso estimado",
                value = "${order.weightGramsPerUnit} g x ${order.quantity}"
            )

            DetailRow(
                label = "Unidades",
                value = "${order.quantity}"
            )

            DetailRow(
                label = "Impresora",
                value = order.printer.label
            )

            DetailRow(
                label = "Tiempo estimado",
                value = formatearTiempoImpresion(
                    hours = order.printTimeHours,
                    minutes = order.printTimeMinutes
                )
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        /*************** Análisis de costos ***************/
        WebStyleInfoCard(
            title = "Análisis de costos",
            icon = "💰"
        ) {
            DetailRow(
                label = "Costo material",
                value = "ARS ${"%.2f".format(breakdown.costoMaterial)}"
            )

            DetailRow(
                label = "Costo energía",
                value = "ARS ${"%.2f".format(breakdown.costoEnergia)}"
            )

            DetailRow(
                label = "Costo desgaste",
                value = "ARS ${"%.2f".format(breakdown.costoDegradamiento)}"
            )

            DetailRow(
                label = "Costo diseño",
                value = "ARS ${"%.2f".format(breakdown.costoDiseno)}"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = PikiCreamLine.copy(alpha = 0.65f)
            )

            DetailRow(
                label = "Subtotal costos",
                value = "ARS ${"%.2f".format(order.subtotal.ifZeroUse(breakdown.subtotal))}"
            )

            DetailRow(
                label = "Margen de ganancia",
                value = "ARS ${"%.2f".format(order.margenMonto.ifZeroUse(breakdown.margenMonto))}",
                highlight = true
            )

            DetailRow(
                label = "Descuento por cantidad",
                value = "ARS ${"%.2f".format(order.descuentoCantidad.ifZeroUse(breakdown.descuentoCantidad))}"
            )

            DetailRow(
                label = "Descuento amigo",
                value = "ARS ${"%.2f".format(order.descuentoAmigo.ifZeroUse(breakdown.descuentoAmigo))}"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = PikiCreamLine.copy(alpha = 0.65f)
            )

            DetailRow(
                label = "Descuentos aplicados",
                value = "ARS ${"%.2f".format(
                    order.descuentoCantidad.ifZeroUse(breakdown.descuentoCantidad) +
                            order.descuentoAmigo.ifZeroUse(breakdown.descuentoAmigo)
                )}"
            )

            DetailRow(
                label = "Total final",
                value = "ARS ${"%.2f".format(obtenerTotalHistorico(order))}",
                highlight = true
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        /*************** Condiciones comerciales ***************/
        WebStyleInfoCard(
            title = "Condiciones del pedido",
            icon = "📒"
        ) {
            DetailRow(
                label = "Fecha presupuesto",
                value = formatearFechaArgentina(order.quoteDate)
            )

            DetailRow(
                label = "Validez de la oferta",
                value = "${order.validityDays} días"
            )

            DetailRow(
                label = "Plazo de entrega",
                value = "${order.deliveryBusinessDays} días hábiles"
            )

            DetailRow(
                label = "Entrega estimada",
                value = formatearFechaArgentina(order.deliveryDate),
                highlight = true
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = PikiCreamLine.copy(alpha = 0.65f)
            )

            DetailRow(
                label = "Forma de pago",
                value = order.paymentType.label
            )

            if (order.paymentType == PaymentType.DEPOSIT) {
                DetailRow(
                    label = "Seña requerida",
                    value = "${order.depositPercentage}%"
                )
            }

            DetailRow(
                label = "Descuento amigo",
                value = if (order.isFriend) {
                    "Aplicado"
                } else {
                    "No aplicado"
                }
            )

            if (order.notes.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = PikiCreamLine.copy(alpha = 0.65f)
                )

                NotesBubble(
                    notes = order.notes
                )
            }
        }


        Spacer(modifier = Modifier.height(18.dp))

        /*************** Total estilo web ***************/
        WebStyleTotalCard(
            total = obtenerTotalHistorico(order)
        )

        Spacer(modifier = Modifier.height(18.dp))

        /*************** Acciones ***************/
        WebStyleActions(
            onShareQuote = onShareQuote,
            onGenerateLabel = onGenerateLabel,
            onEdit = onEdit,
            onDelete = onDelete
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/*************** Barra superior del detalle ***************/
@Composable
private fun DetailTopBar(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Detalle del Pedido 🌿",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = PikiWood
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(PikiWhite)
                .border(
                    width = 2.dp,
                    color = PikiCreamLine,
                    shape = RoundedCornerShape(999.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = PikiWood
            )
        }
    }
}

/*************** Hero inspirado en el modal web ***************/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderWebStyleHeroCard(
    order: Order,
    selectedStatus: OrderStatus,
    onStatusSelected: (OrderStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiCreamLine
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Nombre del trabajo",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiMutedText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = order.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWood,
                lineHeight = 32.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PikiPaper,
                    border = BorderStroke(
                        width = 1.dp,
                        color = PikiCreamLine
                    )
                ) {
                    Text(
                        text = "REF ${obtenerRefPedido(order)}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiClay
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PikiSky.copy(alpha = 0.22f)
                ) {
                    Text(
                        text = "👤 ${order.clientName}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiWood
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /*************** Estado editable ***************/
            StatusDropdown(
                selectedStatus = selectedStatus,
                onStatusSelected = onStatusSelected
            )
        }
    }
}

/*************** Card interna estilo web ***************/
@Composable
private fun WebStyleInfoCard(
    title: String,
    icon: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiCreamLine
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PikiPaper
                ) {
                    Text(
                        text = icon,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiWood
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = DividerDefaults.Thickness,
                color = PikiCreamLine.copy(alpha = 0.65f)
            )

            content()
        }
    }
}

/*************** Burbuja de notas ***************/
@Composable
private fun NotesBubble(
    notes: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PikiPaper)
            .border(
                width = 1.dp,
                color = PikiCreamLine,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = "Notas",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiMutedText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = PikiWood
            )
        }
    }
}

/*************** Total estilo web / ticket ***************/
@Composable
private fun WebStyleTotalCard(
    total: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiLeafDark
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total a cobrar",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiWhite.copy(alpha = 0.85f)
                )

                Text(
                    text = "Importe final del pedido",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = PikiWhite.copy(alpha = 0.65f)
                )
            }

            Text(
                text = "ARS ${"%.2f".format(total)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWhite
            )
        }
    }
}

/*************** Acciones estilo web ***************/
@Composable
private fun WebStyleActions(
    onShareQuote: () -> Unit,
    onGenerateLabel: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onShareQuote,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PikiLeafDark,
                contentColor = PikiWhite
            )
        ) {
            Text(
                text = "📤 Compartir Presupuesto",
                fontWeight = FontWeight.ExtraBold
            )
        }

        Button(
            onClick = onGenerateLabel,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PikiSky,
                contentColor = PikiWood
            )
        ) {
            Text(
                text = "🏷️ Generar Etiqueta",
                fontWeight = FontWeight.ExtraBold
            )
        }

        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = 2.dp,
                color = PikiCreamLine
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PikiWhite,
                contentColor = PikiWood
            )
        ) {
            Text(
                text = "📝 Editar Presupuesto",
                fontWeight = FontWeight.ExtraBold
            )
        }

        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(
                width = 2.dp,
                color = PikiCanceledBg
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PikiPaperLight,
                contentColor = PikiCanceledText
            )
        ) {
            Text(
                text = "Eliminar Pedido",
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

/*************** Fallback para valores históricos ***************/
private fun Double.ifZeroUse(
    fallback: Double
): Double {
    return if (this > 0.0) {
        this
    } else {
        fallback
    }
}

/*************** Nueva Sección de Información ***************/
@Composable
private fun InfoSection(
    title: String,
    icon: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = PikiWood
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PikiWhite, RoundedCornerShape(24.dp))
                .border(2.dp, PikiCreamLine, RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}

/*************** Dropdown manual de estado ***************/
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
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PikiLeafDark,
                unfocusedBorderColor = PikiCreamLine,
                focusedLabelColor = PikiLeafDark,
                unfocusedLabelColor = PikiMutedText,
                focusedContainerColor = PikiPaperLight,
                unfocusedContainerColor = PikiPaperLight,
                cursorColor = PikiLeafDark
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            containerColor = PikiPaperLight
        ) {
            OrderStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = statusLabelWithIcon(status),
                            color = PikiWood,
                            fontWeight = FontWeight.Bold
                        )
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

/*************** Fila de detalle estilo web ***************/
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
            color = PikiMutedText,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = value,
            modifier = Modifier.weight(1.15f),
            fontWeight = if (highlight) {
                FontWeight.ExtraBold
            } else {
                FontWeight.Bold
            },
            color = if (highlight) {
                PikiLeafDark
            } else {
                PikiWood
            },
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/*************** Previsualización de etiqueta ***************/
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

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = DividerDefaults.Thickness,
                        color = Color.Gray
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
                            value = "ARS ${"%.2f".format(obtenerTotalHistorico(order))}"
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

/*************** Bloque pequeño de etiqueta ***************/
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

/*************** Campo de búsqueda estético ***************/
@Composable
private fun PikiSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(22.dp)),
        placeholder = {
            Text(
                text = "Buscar por REF, cliente, proyecto...",
                color = PikiMutedText,
                fontWeight = FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = PikiWood.copy(alpha = 0.6f)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar",
                        tint = PikiWood
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PikiLeafDark,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = PikiWhite,
            unfocusedContainerColor = PikiWhite,
            cursorColor = PikiLeafDark
        )
    )
}

/*************** Estado vacío: sin pedidos ***************/
@Composable
private fun EmptyOrdersState(
    onNewOrder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(140.dp)
                .background(
                    Brush.radialGradient(
                        listOf(PikiLeaf.copy(alpha = 0.2f), Color.Transparent)
                    )
                )
        ) {
            Text(
                text = "🧺",
                fontSize = 80.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡El mural está vacío!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = PikiWood
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Todavía no guardaste ningún pedido.\n¿Empezamos con un presupuesto?",
            textAlign = TextAlign.Center,
            color = PikiMutedText,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        PikiPrimaryButton(
            text = "Crear Nuevo Pedido 🌿",
            onClick = onNewOrder
        )
    }
}

/*************** Estado vacío: búsqueda sin resultados ***************/
@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🕵️‍♀️",
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No encontramos nada",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = PikiWood
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Probá con otras palabras clave\no borrá los filtros aplicados.",
            textAlign = TextAlign.Center,
            color = PikiMutedText,
            fontWeight = FontWeight.Bold
        )
    }
}

/*************** Texto de estado con ícono ***************/
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

/*************** Color de fondo de estado ***************/
private fun statusBackgroundColor(
    status: OrderStatus
): Color {
    return when (status) {
        OrderStatus.PENDING -> PikiPendingBg
        OrderStatus.PRINTING -> PikiPrintingBg
        OrderStatus.DONE -> PikiDoneBg
        OrderStatus.CANCELED -> PikiCanceledBg
    }
}

/*************** Color de texto de estado ***************/
private fun statusTextColor(
    status: OrderStatus
): Color {
    return when (status) {
        OrderStatus.PENDING -> PikiPendingText
        OrderStatus.PRINTING -> PikiPrintingText
        OrderStatus.DONE -> PikiDoneText
        OrderStatus.CANCELED -> PikiCanceledText
    }
}

/*************** REF visible del pedido ***************/
private fun obtenerRefPedido(
    order: Order
): String {
    return order.id
        .take(8)
        .uppercase()
}

/*************** Total histórico ***************/
private fun obtenerTotalHistorico(
    order: Order
): Double {
    return when {
        order.totalFinal > 0.0 -> order.totalFinal
        order.totalComputed > 0.0 -> order.totalComputed
        else -> 0.0
    }
}

/*************** Formatear tiempo de impresión ***************/
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

/*************** Formatear fecha argentina ***************/
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