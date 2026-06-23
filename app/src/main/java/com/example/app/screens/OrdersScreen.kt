package com.example.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalPrintshop
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import java.time.temporal.ChronoUnit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.outlined.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.app.ui.components.kawaiiShadow
import com.example.app.ui.theme.PikiCanceledBg
import com.example.app.ui.theme.PikiCanceledText
import com.example.app.ui.theme.PikiClay
import com.example.app.ui.theme.PikiCreamLine
import com.example.app.ui.theme.PikiLeaf
import com.example.app.ui.theme.PikiLeafDark
import com.example.app.ui.theme.PikiMutedText
import com.example.app.ui.theme.PikiPaper
import com.example.app.ui.theme.PikiPaperDark
import com.example.app.ui.theme.PikiPaperLight
import com.example.app.ui.theme.PikiPendingBg
import com.example.app.ui.theme.PikiSky
import com.example.app.ui.theme.PikiSkyDark
import com.example.app.ui.theme.PikiWhite
import com.example.app.ui.theme.PikiWood
import com.example.app.utils.Calculator
import com.example.app.utils.shareQuoteImage
import com.example.app.viewmodels.AppViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.app.R

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
                        obtenerNombreImpresora(order).contains(text, ignoreCase = true) ||
                        obtenerNombreImpresora(order).contains(text, ignoreCase = true) ||
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
            .background(PikiPaper)
    ) {
        PikiLightTopBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 18.dp, bottom = 12.dp)
        ) {
            PikiSearchField(
                value = search,
                onValueChange = {
                    search = it
                },
                onClear = {
                    search = ""
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatusFilterRow(
                selectedStatus = statusFilter,
                onStatusSelected = { status ->
                    statusFilter = status
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            if (state.orders.isEmpty()) {
                EmptyOrdersState()
            } else if (filteredOrders.isEmpty()) {
                EmptySearchState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                    contentPadding = PaddingValues(bottom = 28.dp)
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
                                },
                                onAccept = {
                                    viewModel.updateOrder(
                                        order.copy(status = OrderStatus.PRINTING)
                                    )
                                },
                                onCancel = {
                                    viewModel.updateOrder(
                                        order.copy(status = OrderStatus.CANCELED)
                                    )
                                }
                            )
                        }
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
            containerColor = PikiPaper,
            tonalElevation = 0.dp,
            shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
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

/*************** Top bar liviana estilo web ***************/
/*************** Cabecera con imagen personalizada ***************/
/*
    Esta cabecera reemplaza:
    - el texto "PikiPrint";
    - el ícono circular de persona/configuración.

    Ahora es solamente una imagen decorativa.
    No tiene acción de click porque Ajustes ya es accesible desde otro lugar.
*/
/*************** Cabecera con imagen personalizada ***************/
/*************** Cabecera con imagen personalizada ***************/
/*
    Esta cabecera reemplaza:
    - el texto "PikiPrint";
    - el ícono circular de persona/configuración.

    Ahora es solamente una imagen decorativa.
    No tiene acción de click porque Ajustes ya es accesible desde otro lugar.
*/
/*************** Cabecera con imagen completa ***************/
/*
    La imagen ocupa todo el topbar.

    ContentScale.Crop:
    - llena todo el ancho y alto disponibles;
    - no deja márgenes vacíos;
    - puede recortar un poco los bordes si la proporción no coincide.
*/
@Composable
private fun PikiLightTopBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PikiPaper,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(PikiPaper),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    //painter = painterResource(id = R.drawable.piki_header), //celular
                    painter = painterResource(id = R.drawable.piki_header_2), //tablet
                    contentDescription = "PikiPrint",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            HorizontalDivider(
                color = PikiClay.copy(alpha = 0.10f),
                thickness = 2.dp
            )
        }
    }
}

/*************** Filtros de estado tipo sticker ***************/
@Composable
private fun StatusFilterRow(
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PremiumFilterChip(
            text = "Todos",
            selected = selectedStatus == null,
            onClick = {
                onStatusSelected(null)
            }
        )

        OrderStatus.values().forEach { status ->
            PremiumFilterChip(
                text = statusLabelPlain(status),
                selected = selectedStatus == status,
                onClick = {
                    onStatusSelected(status)
                }
            )
        }
    }
}

/*************** Chip de filtro premium ***************/
@Composable
private fun PremiumFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(999.dp),
        color = if (selected) {
            PikiSky
        } else {
            PikiWhite
        },
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) {
                PikiClay
            } else {
                PikiClay.copy(alpha = 0.30f)
            }
        ),
        shadowElevation = if (selected) {
            3.dp
        } else {
            0.dp
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 9.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (selected) {
                PikiWood
            } else {
                PikiClay
            }
        )
    }
}

/*************** Card premium de pedido ***************/
/*************** Card de pedido según estado ***************/
@Composable
private fun WorkshopOrderCard(
    order: Order,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit
) {
    when (order.status) {
        OrderStatus.DONE -> {
            DoneOrderCard(
                order = order,
                onClick = onClick
            )
        }

        OrderStatus.PRINTING -> {
            PrintingOrderCard(
                order = order,
                onClick = onClick
            )
        }

        OrderStatus.PENDING -> {
            PendingOrderCard(
                order = order,
                onClick = onClick,
                onAccept = onAccept,
                onCancel = onCancel
            )
        }

        OrderStatus.CANCELED -> {
            CanceledOrderCard(
                order = order,
                onClick = onClick
            )
        }
    }
}

/*************** Pedido terminado ***************/
@Composable
private fun DoneOrderCard(
    order: Order,
    onClick: () -> Unit
) {
    OrderCardContainer(
        onClick = onClick
    ) {
        OrderIdentityHeader(
            order = order
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProductLargePreview(
            order = order
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrinterTimeStrip(
            order = order
        )

        Spacer(modifier = Modifier.height(18.dp))

        OrderFooter(
            total = obtenerTotalHistorico(order),
            buttonText = "Detalles",
            buttonBackground = PikiSky,
            onClick = onClick
        )
    }
}

/*************** Pedido en marcha ***************/
@Composable
private fun PrintingOrderCard(
    order: Order,
    onClick: () -> Unit
) {
    val progressPercent = calcularProgresoEntrega(order)

    OrderCardContainer(
        onClick = onClick
    ) {
        OrderIdentityHeader(
            order = order
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductSmallPreview(
                order = order,
                modifier = Modifier.size(86.dp)
            )

            PrintingProgressSummary(
                progressPercent = progressPercent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrinterTimeStrip(
            order = order
        )

        Spacer(modifier = Modifier.height(16.dp))

        DeliveryProgressBar(
            progressPercent = progressPercent
        )

        Spacer(modifier = Modifier.height(18.dp))

        OrderFooter(
            total = obtenerTotalHistorico(order),
            buttonText = "Gestionar",
            buttonBackground = PikiWhite,
            onClick = onClick
        )
    }
}

/*************** Pedido pendiente ***************/
/*************** Pedido pendiente ***************/
@Composable
private fun PendingOrderCard(
    order: Order,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit
) {
    OrderCardContainer(
        onClick = onClick
    ) {
        OrderIdentityHeader(
            order = order
        )

        /*************** Imagen del pedido ***************/
        /*
            Muestra la imagen cargada para el pedido.

            Si ProductLargePreview ya tiene fallback interno,
            cuando no haya imagen va a mostrar el placeholder que tengas definido.
        */
        Spacer(modifier = Modifier.height(16.dp))

        ProductLargePreview(
            order = order
        )

        if (order.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = order.notes,
                style = MaterialTheme.typography.bodyMedium,
                color = PikiClay,
                fontWeight = FontWeight.Medium,
                lineHeight = 21.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrinterTimeStrip(
            order = order
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ARS ${"%.2f".format(obtenerTotalHistorico(order))}",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = PikiClay,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        PendingActionRow(
            onAccept = onAccept,
            onCancel = onCancel
        )
    }
}

/*************** Pedido cancelado ***************/
@Composable
private fun CanceledOrderCard(
    order: Order,
    onClick: () -> Unit
) {
    OrderCardContainer(
        onClick = onClick
    ) {
        OrderIdentityHeader(
            order = order
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductSmallPreview(
                order = order,
                modifier = Modifier.size(86.dp)
            )

            PrinterTimeStrip(
                order = order,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        OrderFooter(
            total = obtenerTotalHistorico(order),
            buttonText = "Detalles",
            buttonBackground = PikiWhite,
            onClick = onClick
        )
    }
}

/*************** Bloque técnico tipo web ***************/
@Composable
private fun TechnicalWebGrid(
    order: Order
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TechnicalWebCell(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Inventory2,
                label = "Material",
                value = order.filament.label
            )

            TechnicalWebCell(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.LocalPrintshop,
                label = "Impresora",
                value = obtenerNombreImpresora(order)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TechnicalWebCell(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Straighten,
                label = "Peso",
                value = "${order.weightGramsPerUnit} g x ${order.quantity}"
            )

            TechnicalWebCell(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Schedule,
                label = "Tiempo",
                value = formatearTiempoImpresion(
                    hours = order.printTimeHours,
                    minutes = order.printTimeMinutes
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TechnicalWebCell(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Palette,
                label = "Color",
                value = order.color
            )

            TechnicalWebCell(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.ColorLens,
                label = "Acabado",
                value = order.finishType.label
            )
        }
    }
}

/*************** Celda técnica premium ***************/
@Composable
private fun TechnicalWebCell(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = PikiPaperDark.copy(alpha = 0.62f),
        border = BorderStroke(
            width = 1.dp,
            color = PikiCreamLine.copy(alpha = 0.75f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PikiClay.copy(alpha = 0.78f),
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PikiClay.copy(alpha = 0.62f),
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = PikiWood,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }
    }
}

/*************** Etiqueta de estado tipo botón ***************/
@Composable
private fun PremiumStatusPill(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> PikiPendingBg to PikiWood
        OrderStatus.PRINTING -> PikiSky to PikiWood
        OrderStatus.DONE -> PikiLeaf to PikiWood
        OrderStatus.CANCELED -> PikiCanceledBg to PikiCanceledText
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite.copy(alpha = 0.6f)
        )
    ) {
        Text(
            text = statusLabelPlain(status),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

/*************** BottomSheet de detalle ***************/
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

    val subtotalHistorico =
        order.subtotal.ifZeroUse(breakdown.subtotal)

    val margenHistorico =
        order.margenMonto.ifZeroUse(breakdown.margenMonto)

    val descuentoCantidadHistorico =
        order.descuentoCantidad.ifZeroUse(breakdown.descuentoCantidad)

    val descuentoAmigoHistorico =
        order.descuentoAmigo.ifZeroUse(breakdown.descuentoAmigo)

    val descuentosAplicados =
        descuentoCantidadHistorico + descuentoAmigoHistorico

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        DetailMockupTopBar(
            order = order,
            onClose = onClose
        )

        Spacer(modifier = Modifier.height(18.dp))

        ProductHeroPlaceholder(
            order = order
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProductMainCard(
            order = order,
            selectedStatus = selectedStatus,
            onStatusSelected = { newStatus ->
                selectedStatus = newStatus
                onStatusChange(newStatus)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TwoColumnInfoGrid(
            first = {
                MiniSpecCard(
                    label = "MATERIAL",
                    value = order.filament.label
                )
            },
            second = {
                MiniSpecCard(
                    label = "IMPRESORA",
                    value = obtenerNombreImpresora(order)
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        TwoColumnInfoGrid(
            first = {
                MiniSpecCard(
                    label = "PESO",
                    value = "${order.weightGramsPerUnit} g x ${order.quantity}"
                )
            },
            second = {
                MiniSpecCard(
                    label = "TIEMPO",
                    value = formatearTiempoImpresion(
                        hours = order.printTimeHours,
                        minutes = order.printTimeMinutes
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        VisualSectionCard(
            title = "Análisis de costos",
            icon = Icons.Outlined.Inventory2,
            accentColor = PikiClay
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
                value = "ARS ${"%.2f".format(subtotalHistorico)}",
                highlight = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        VisualSectionCard(
            title = "Precios y descuentos",
            icon = Icons.Outlined.ColorLens,
            accentColor = PikiLeafDark
        ) {
            DetailRow(
                label = "Margen de ganancia",
                value = "ARS ${"%.2f".format(margenHistorico)}",
                highlight = true
            )

            DetailRow(
                label = "Descuento por cantidad",
                value = "ARS ${"%.2f".format(descuentoCantidadHistorico)}"
            )

            DetailRow(
                label = "Descuento amigo",
                value = "ARS ${"%.2f".format(descuentoAmigoHistorico)}"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = PikiCreamLine.copy(alpha = 0.65f)
            )

            DetailRow(
                label = "Descuentos aplicados",
                value = "ARS ${"%.2f".format(descuentosAplicados)}",
                highlight = descuentosAplicados > 0.0
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GrandTotalCard(
            total = obtenerTotalHistorico(order)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TwoColumnInfoGrid(
            first = {
                MiniSpecCard(
                    label = "FECHA PRESUP.",
                    value = formatearFechaArgentina(order.quoteDate)
                )
            },
            second = {
                MiniSpecCard(
                    label = "ENTREGA",
                    value = formatearFechaArgentina(order.deliveryDate)
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        TwoColumnInfoGrid(
            first = {
                MiniSpecCard(
                    label = "VALIDEZ",
                    value = "${order.validityDays} días"
                )
            },
            second = {
                MiniSpecCard(
                    label = "PLAZO",
                    value = "${order.deliveryBusinessDays} días hábiles"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        VisualSectionCard(
            title = "Condiciones comerciales",
            icon = Icons.Outlined.Schedule,
            accentColor = PikiSkyDark
        ) {
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
        }

        if (order.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))

            SpecialNotesCard(
                notes = order.notes
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        VisualActionButtons(
            onShareQuote = onShareQuote,
            onGenerateLabel = onGenerateLabel,
            onEdit = onEdit,
            onDelete = onDelete
        )

        Spacer(modifier = Modifier.height(18.dp))
    }
}

/*************** Header superior del detalle ***************/
@Composable
private fun DetailMockupTopBar(
    order: Order,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(PikiWhite)
                .border(
                    width = 2.dp,
                    color = PikiClay.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(999.dp)
                )
        ) {
            Text(
                text = "←",
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Detalle del Pedido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWood
            )

            Text(
                text = "REF: #PK-${obtenerRefPedido(order)}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay
            )
        }
    }
}

/*************** Hero visual del producto ***************/
/*
    Se usa en el detalle del pedido.

    Si el pedido tiene imagen:
    - muestra la imagen real cargada.

    Si el pedido no tiene imagen:
    - muestra piki_order_placeholder.
*/
@Composable
private fun ProductHeroPlaceholder(
    order: Order
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PikiSky.copy(alpha = 0.28f),
                        PikiWhite,
                        PikiPaperDark
                    )
                )
            )
            .border(
                width = 4.dp,
                color = PikiWhite,
                shape = RoundedCornerShape(40.dp)
            )
            .kawaiiShadow(),
        contentAlignment = Alignment.Center
    ) {
        if (order.imageUri.isNotBlank()) {
            AsyncImage(
                model = order.imageUri,
                contentDescription = "Imagen del pedido",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = "Pedido sin imagen",
                tint = PikiClay.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

/*************** Card principal del pedido en detalle ***************/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductMainCard(
    order: Order,
    selectedStatus: OrderStatus,
    onStatusSelected: (OrderStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
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

            StatusDropdown(
                selectedStatus = selectedStatus,
                onStatusSelected = onStatusSelected
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = PikiSky.copy(alpha = 0.25f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = PikiClay,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = order.clientName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PikiMutedText,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VisualMiniChip(
                    icon = Icons.Outlined.Palette,
                    label = order.color
                )

                VisualMiniChip(
                    icon = Icons.Outlined.ColorLens,
                    label = order.finishType.label
                )

                VisualMiniChip(
                    icon = Icons.Outlined.Straighten,
                    label = order.designType.label
                )
            }
        }
    }
}

/*************** Contenedor base de card ***************/
@Composable
private fun OrderCardContainer(
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            content()
        }
    }
}

/*************** Header común: REF + estado ***************/
/*@Composable
private fun OrderHeaderRow(
    order: Order,
    refFirst: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        if (refFirst) {
            Text(
                text = "#${obtenerRefPedido(order)}",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay.copy(alpha = 0.68f)
            )

            PremiumStatusPill(
                status = order.status
            )
        } else {
            PremiumStatusPill(
                status = order.status
            )

            Text(
                text = "#${obtenerRefPedido(order)}",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay.copy(alpha = 0.68f)
            )
        }
    }
}

/*************** Título + cliente común ***************/
@Composable
private fun OrderTitleAndClient(
    order: Order
) {
    Text(
        text = order.title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold,
        color = PikiWood,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 30.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    OrderClientLine(
        clientName = order.clientName
    )
}*/

/*************** Línea de cliente ***************/
@Composable
private fun OrderClientLine(
    clientName: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(PikiSky.copy(alpha = 0.30f))
                .border(
                    width = 1.5.dp,
                    color = PikiClay.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(999.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = PikiClay,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Cliente: ",
            style = MaterialTheme.typography.bodyMedium,
            color = PikiClay,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = clientName,
            style = MaterialTheme.typography.bodyMedium,
            color = PikiWood,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/*************** Impresora + tiempo: siempre visible ***************/
@Composable
private fun PrinterTimeStrip(
    order: Order,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TechnicalMiniLine(
            icon = Icons.Outlined.LocalPrintshop,
            label = "Impresora",
            value = obtenerNombreImpresora(order)
        )

        TechnicalMiniLine(
            icon = Icons.Outlined.Schedule,
            label = "Tiempo",
            value = formatearTiempoImpresion(
                hours = order.printTimeHours,
                minutes = order.printTimeMinutes
            )
        )
    }
}

/*************** Línea técnica compacta ***************/
@Composable
private fun TechnicalMiniLine(
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = PikiPaperDark.copy(alpha = 0.62f),
        border = BorderStroke(
            width = 1.dp,
            color = PikiCreamLine.copy(alpha = 0.75f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PikiClay.copy(alpha = 0.78f),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = PikiClay.copy(alpha = 0.62f),
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = PikiWood,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/*************** Imagen grande / placeholder ***************/
/*
    Muestra la imagen real del pedido si existe.

    Si el pedido todavía no tiene imagen cargada,
    muestra una imagen placeholder personalizada.
*/
@Composable
private fun ProductLargePreview(
    order: Order
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(PikiWhite)
            .border(
                width = 2.dp,
                color = PikiClay.copy(alpha = 0.28f),
                shape = RoundedCornerShape(32.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (order.imageUri.isNotBlank()) {
            AsyncImage(
                model = order.imageUri,
                contentDescription = "Imagen del pedido",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = "Pedido sin imagen",
                tint = PikiClay.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/*************** Imagen pequeña / placeholder ***************/
/*
    Versión compacta para tarjetas donde la imagen acompaña
    otra información, como progreso o datos de impresora.
*/
@Composable
private fun ProductSmallPreview(
    order: Order,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(PikiWhite)
            .border(
                width = 1.5.dp,
                color = PikiClay.copy(alpha = 0.24f),
                shape = RoundedCornerShape(26.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (order.imageUri.isNotBlank()) {
            AsyncImage(
                model = order.imageUri,
                contentDescription = "Imagen del pedido",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = "Pedido sin imagen",
                tint = PikiClay.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/*************** Resumen visual de progreso ***************/
@Composable
private fun PrintingProgressSummary(
    progressPercent: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = PikiPaperDark.copy(alpha = 0.70f),
        border = BorderStroke(
            width = 1.dp,
            color = PikiCreamLine.copy(alpha = 0.80f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(PikiSky.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = PikiClay,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "En producción",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiWood
                )

                Text(
                    text = "$progressPercent% según fecha de entrega",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = PikiMutedText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/*************** Barra de progreso por fecha de entrega ***************/
@Composable
private fun DeliveryProgressBar(
    progressPercent: Int
) {
    val progressRatio = progressPercent.coerceIn(0, 100) / 100f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progreso",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay
            )

            Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWood
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(PikiPaperDark)
                .border(
                    width = 1.5.dp,
                    color = PikiClay.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(999.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressRatio)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(PikiLeaf)
            )
        }
    }
}

/*************** Footer común con precio y botón ***************/
@Composable
private fun OrderFooter(
    total: Double,
    buttonText: String,
    buttonBackground: Color,
    onClick: () -> Unit
) {
    HorizontalDivider(
        color = PikiCreamLine.copy(alpha = 0.70f),
        thickness = 1.dp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "Total",
                style = MaterialTheme.typography.labelSmall,
                color = PikiClay.copy(alpha = 0.72f),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "ARS ${"%.2f".format(total)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay
            )
        }

        Surface(
            modifier = Modifier
                .height(44.dp)
                .clip(RoundedCornerShape(999.dp))
                .clickable {
                    onClick()
                },
            shape = RoundedCornerShape(999.dp),
            color = buttonBackground,
            border = BorderStroke(
                width = 2.dp,
                color = PikiClay
            ),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiWood
                )

                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = PikiWood,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/*************** Acciones rápidas para pendiente ***************/
@Composable
private fun PendingActionRow(
    onAccept: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(999.dp))
                .clickable {
                    onCancel()
                },
            shape = RoundedCornerShape(999.dp),
            color = PikiWhite,
            border = BorderStroke(
                width = 2.dp,
                color = PikiClay.copy(alpha = 0.80f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Cancelar pedido",
                    tint = PikiClay,
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(999.dp))
                .clickable {
                    onAccept()
                },
            shape = RoundedCornerShape(999.dp),
            color = PikiLeaf,
            border = BorderStroke(
                width = 2.dp,
                color = PikiClay
            ),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = PikiWood,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Aceptar pedido",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiWood
                )
            }
        }
    }
}

/*************** Chip visual interno ***************/
@Composable
private fun VisualMiniChip(
    icon: ImageVector,
    label: String
) {
    Column(
        modifier = Modifier
            .widthIn(min = 92.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PikiPaper)
            .border(
                width = 1.dp,
                color = PikiCreamLine,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PikiClay,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PikiWood,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/*************** Grilla de dos columnas ***************/
@Composable
private fun TwoColumnInfoGrid(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            first()
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            second()
        }
    }
}

/*************** Mini card técnica ***************/
@Composable
private fun MiniSpecCard(
    label: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay.copy(alpha = 0.72f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWood,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/*************** Sección visual ***************/
@Composable
private fun VisualSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(21.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
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

/*************** Total protagonista ***************/
@Composable
private fun GrandTotalCard(
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiSky
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TOTAL A COBRAR",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWood.copy(alpha = 0.65f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ARS ${"%.2f".format(total)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = PikiWood
            )
        }
    }
}

/*************** Notas especiales ***************/
@Composable
private fun SpecialNotesCard(
    notes: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiPendingBg.copy(alpha = 0.65f)
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Notas especiales",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = PikiWood,
                lineHeight = 22.sp
            )
        }
    }
}

/*************** Botones de acción visuales ***************/
/*************** Botones de acción circulares ***************/
@Composable
private fun VisualActionButtons(
    onShareQuote: () -> Unit,
    onGenerateLabel: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundDetailActionButton(
            icon = Icons.Outlined.Share,
            contentDescription = "Compartir presupuesto",
            background = PikiSky,
            tint = PikiWood,
            onClick = onShareQuote
        )

        RoundDetailActionButton(
            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            contentDescription = "Generar etiqueta",
            background = PikiLeaf,
            tint = PikiWood,
            onClick = onGenerateLabel
        )

        RoundDetailActionButton(
            icon = Icons.Outlined.Edit,
            contentDescription = "Editar presupuesto",
            background = PikiPaperDark,
            tint = PikiClay,
            onClick = onEdit
        )

        RoundDetailActionButton(
            icon = Icons.Outlined.Delete,
            contentDescription = "Eliminar pedido",
            background = PikiCanceledBg,
            tint = PikiCanceledText,
            onClick = onDelete
        )
    }
}

/*************** Botón circular del detalle ***************/
@Composable
private fun RoundDetailActionButton(
    icon: ImageVector,
    contentDescription: String,
    background: Color,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(58.dp)
            .clip(RoundedCornerShape(999.dp))
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(999.dp),
        color = background,
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
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
            value = statusLabelPlain(selectedStatus),
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
                focusedBorderColor = PikiClay,
                unfocusedBorderColor = PikiClay.copy(alpha = 0.22f),
                focusedLabelColor = PikiClay,
                unfocusedLabelColor = PikiMutedText,
                focusedContainerColor = PikiWhite,
                unfocusedContainerColor = PikiWhite,
                cursorColor = PikiClay
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            containerColor = PikiWhite
        ) {
            OrderStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = statusLabelPlain(status),
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

/*************** Fila de detalle ***************/
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

/*************** Buscador blanco flotante ***************/
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
            .heightIn(min = 58.dp)
            .kawaiiShadow(),
        placeholder = {
            Text(
                text = "Buscar pedidos...",
                color = PikiClay.copy(alpha = 0.45f),
                fontWeight = FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = PikiClay,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = onClear
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Limpiar",
                        tint = PikiClay,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(999.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PikiClay,
            unfocusedBorderColor = PikiClay.copy(alpha = 0.20f),
            focusedContainerColor = PikiWhite,
            unfocusedContainerColor = PikiWhite,
            cursorColor = PikiClay
        )
    )
}

/*************** Estado vacío: sin pedidos ***************/
/*************** Estado vacío: sin pedidos ***************/
/*
    Se muestra cuando todavía no hay pedidos cargados.

    La imagen ya incluye:
    - ilustración;
    - título;
    - texto secundario.

    Por eso no repetimos textos en Compose.
*/
@Composable
private fun EmptyOrdersState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

            Image(
                painter = painterResource(id = R.drawable.piki_empty_orders),
                contentDescription = "Todavía no hay pedidos",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(30.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cuando guardes presupuestos,\nvan a aparecer en este mural.",
                textAlign = TextAlign.Center,
                color = PikiMutedText,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

    }
}

/*************** Estado vacío: búsqueda sin resultados ***************/
@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(PikiWhite)
                .border(
                    width = 2.dp,
                    color = PikiClay.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(36.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = PikiClay.copy(alpha = 0.65f),
                modifier = Modifier.size(48.dp)
            )
        }

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

/*************** Texto de estado sin emojis ***************/
private fun statusLabelPlain(
    status: OrderStatus
): String {
    return when (status) {
        OrderStatus.PENDING -> "Pendiente"
        OrderStatus.PRINTING -> "En marcha"
        OrderStatus.DONE -> "Terminado"
        OrderStatus.CANCELED -> "Cancelado"
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

/*************** Progreso calculado por fecha de entrega ***************/
/*
    El progreso se calcula usando:
    - inicio: fecha de presupuesto; si está vacía, fecha de creación.
    - fin: fecha de entrega; si está vacía, inicio + plazo en días hábiles.
*/
private fun calcularProgresoEntrega(
    order: Order
): Int {
    val today = LocalDate.now()

    val startDate =
        parseFechaOrNull(order.quoteDate)
            ?: parseFechaOrNull(order.createdAt)
            ?: today

    val endDate =
        parseFechaOrNull(order.deliveryDate)
            ?: startDate.plusDays(order.deliveryBusinessDays.toLong())

    val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toDouble()
    val elapsedDays = ChronoUnit.DAYS.between(startDate, today).toDouble()

    if (totalDays <= 0.0) {
        return 100
    }

    return ((elapsedDays / totalDays) * 100.0)
        .toInt()
        .coerceIn(0, 100)
}

/*************** Parse seguro de fecha yyyy-MM-dd ***************/
private fun parseFechaOrNull(
    value: String
): LocalDate? {
    if (value.isBlank()) {
        return null
    }

    return try {
        LocalDate.parse(value)
    } catch (e: Exception) {
        null
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

/*************** Nombre visible de impresora ***************/
/*
    Usa el nombre congelado del pedido.
    Si el pedido es viejo y no tiene snapshot, usa el enum heredado.
*/
private fun obtenerNombreImpresora(
    order: Order
): String {
    return order.printerNameSnapshot.ifBlank {
        obtenerNombreImpresora(order)
    }
}


/*************** Header visual del pedido ***************/
/*
    Estructura nueva de la tarjeta:

    Izquierda:
    - REF del pedido
    - nombre del pedido
    - cliente

    Derecha:
    - imagen del estado

    Así la imagen de estado no compite con el ID.
*/
@Composable
private fun OrderIdentityHeader(
    order: Order
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 14.dp)
        ) {
            Text(
                text = "#${obtenerRefPedido(order)}",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay.copy(alpha = 0.68f)
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = order.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWood,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            OrderClientLine(
                clientName = order.clientName
            )
        }

        PremiumStatusPill(
            status = order.status
        )
    }
}