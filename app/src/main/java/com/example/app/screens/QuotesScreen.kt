package com.example.app.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app.models.DesignType
import com.example.app.models.FilamentType
import com.example.app.models.FinishType
import com.example.app.models.Order
import com.example.app.models.OrderStatus
import com.example.app.models.PaymentType
import com.example.app.models.PrinterType
import com.example.app.utils.Calculator
import com.example.app.utils.QuoteBreakdown
import com.example.app.viewmodels.AppViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

/*************** Formato argentino visible ***************/
/*
    Este formato es el que ve la usuaria en pantalla.
    Ejemplo: 06/05/2026.
*/
@RequiresApi(Build.VERSION_CODES.O)
private val formatoFechaArgentina: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy")



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    viewModel: AppViewModel,
    onGoToOrders: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val editingOrder by viewModel.editingOrder.collectAsState()
    

    /*************** Estados del formulario ***************/
    var title by remember(editingOrder) {
        mutableStateOf(editingOrder?.title ?: "")
    }

    var clientName by remember(editingOrder) {
        mutableStateOf(editingOrder?.clientName ?: "")
    }

    var printHours by remember(editingOrder) {
        mutableStateOf(editingOrder?.printTimeHours?.toString() ?: "2")
    }

    var printMinutes by remember(editingOrder) {
        mutableStateOf(editingOrder?.printTimeMinutes?.toString() ?: "30")
    }

    var quantity by remember(editingOrder) {
        mutableStateOf(editingOrder?.quantity?.toString() ?: "1")
    }

    var weight by remember(editingOrder) {
        mutableStateOf(editingOrder?.weightGramsPerUnit?.toString() ?: "100")
    }

    var color by remember(editingOrder) {
        mutableStateOf(editingOrder?.color ?: "Blanco")
    }

    var selectedFilament by remember(editingOrder) {
        mutableStateOf(editingOrder?.filament ?: FilamentType.PLA)
    }

    var selectedPrinter by remember(editingOrder) {
        mutableStateOf(editingOrder?.printer ?: PrinterType.A1_COMBO)
    }

    var selectedDesign by remember(editingOrder) {
        mutableStateOf(editingOrder?.designType ?: DesignType.EXTERNAL)
    }

    var isFriend by remember(editingOrder) {
        mutableStateOf(editingOrder?.isFriend ?: false)
    }

    /*************** Campos comerciales ***************/
    /*
        La fecha se guarda internamente como LocalDate.
        Cuando se crea el Order, se pasa a String ISO yyyy-MM-dd.
        Para mostrarla al usuario, se usa dd/MM/yyyy.
    */
    var quoteDate by remember(editingOrder) {
        mutableStateOf(
            parseIsoDateOrNull(editingOrder?.quoteDate.orEmpty())
                ?: LocalDate.now()
        )
    }

    var showQuoteDatePicker by remember {
        mutableStateOf(false)
    }

    var validityDaysText by remember(editingOrder) {
        mutableStateOf((editingOrder?.validityDays ?: 15).toString())
    }

    var deliveryBusinessDaysText by remember(editingOrder) {
        mutableStateOf((editingOrder?.deliveryBusinessDays ?: 7).toString())
    }

    var paymentType by remember(editingOrder) {
        mutableStateOf(editingOrder?.paymentType ?: PaymentType.FULL)
    }

    var depositPercentageText by remember(editingOrder) {
        mutableStateOf((editingOrder?.depositPercentage ?: 50).toString())
    }

    var finishType by remember(editingOrder) {
        mutableStateOf(editingOrder?.finishType ?: FinishType.VISIBLE_LINES)
    }

    var notes by remember(editingOrder) {
        mutableStateOf(editingOrder?.notes ?: "")
    }

    /*************** Fecha estimada de entrega ***************/
    val calculatedDeliveryDate = remember(
        quoteDate,
        deliveryBusinessDaysText
    ) {
        val businessDays = deliveryBusinessDaysText.toIntOrNull() ?: 0

        addBusinessDays(
            startDate = quoteDate,
            businessDays = businessDays
        )
    }

    /*************** Resultado del cálculo ***************/
    var breakdown by remember {
        mutableStateOf<QuoteBreakdown?>(null)
    }

    var currentOrderData by remember {
        mutableStateOf<Order?>(null)
    }

    /*************** Calendario para fecha del presupuesto ***************/
    if (showQuoteDatePicker) {
        CalendarioFechaDialog(
            fechaInicial = quoteDate,
            onCancelar = {
                showQuoteDatePicker = false
            },
            onAceptar = { nuevaFecha ->
                quoteDate = nuevaFecha
                showQuoteDatePicker = false
            }
        )
    }

    /*************** Pantalla de resultado ***************/
    if (breakdown != null) {
        val b = breakdown!!
        val order = currentOrderData

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFB584E8),
                                    Color(0xFFFF8BA7),
                                    Color(0xFFFFB347)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "✨ Presupuesto Final",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF5F7)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Total: ARS ${"%.2f".format(b.totalFinal)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text("Material: ARS ${"%.2f".format(b.costoMaterial)}")
                    Text("Energía: ARS ${"%.2f".format(b.costoEnergia)}")
                    Text("Desgaste: ARS ${"%.2f".format(b.costoDegradamiento)}")
                    Text("Diseño: ARS ${"%.2f".format(b.costoDiseno)}")
                    Text("Subtotal: ARS ${"%.2f".format(b.subtotal)}")
                    Text("Margen: ARS ${"%.2f".format(b.margenMonto)}")
                    Text("Desc. cantidad: -ARS ${"%.2f".format(b.descuentoCantidad)}")
                    Text("Desc. amigo: -ARS ${"%.2f".format(b.descuentoAmigo)}")

                    if (order != null) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Text("Fecha presupuesto: ${formatearFechaArgentina(order.quoteDate)}")
                        Text("Validez: ${order.validityDays} días")
                        Text("Plazo entrega: ${order.deliveryBusinessDays} días hábiles")
                        Text("Entrega estimada: ${formatearFechaArgentina(order.deliveryDate)}")
                        Text("Pago: ${order.paymentType.label}")

                        if (order.paymentType == PaymentType.DEPOSIT) {
                            Text("Seña: ${order.depositPercentage}%")
                        }

                        Text("Acabado: ${order.finishType.label}")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    currentOrderData?.let { orderToSave ->
                        if (editingOrder == null) {
                            viewModel.addOrder(orderToSave)
                        } else {
                            viewModel.updateOrder(orderToSave)
                        }
                    }

                    viewModel.setEditingOrder(null)
                    onGoToOrders()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (editingOrder == null) {
                        "Guardar Pedido 🌸"
                    } else {
                        "Guardar Cambios 🌸"
                    }
                )
            }

            OutlinedButton(
                onClick = {
                    breakdown = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }

        return
    }

    /*************** Formulario de presupuesto ***************/
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = if (editingOrder == null) {
                    "🌸 Calculadora PikiPrint 3D"
                } else {
                    "🌸 Editar Presupuesto"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        /*************** Datos técnicos ***************/
        item {
            Text(
                text = "⚙️ Datos técnicos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título/Proyecto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        item {
            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Cliente") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        item {
            DropdownEnum(
                label = "Material",
                selected = selectedFilament,
                textForValue = { it.label },
                onSelect = { selectedFilament = it }
            )
        }

        item {
            DropdownEnum(
                label = "Impresora",
                selected = selectedPrinter,
                textForValue = { it.label },
                onSelect = { selectedPrinter = it }
            )
        }

        item {
            DropdownEnum(
                label = "Diseño",
                selected = selectedDesign,
                textForValue = { it.label },
                onSelect = { selectedDesign = it }
            )
        }

        item {
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        item {
            DropdownEnum(
                label = "Tipo de acabado",
                selected = finishType,
                textForValue = { it.label },
                onSelect = { finishType = it }
            )
        }

        item {
            OutlinedTextField(
                value = printHours,
                onValueChange = { printHours = it },
                label = { Text("Horas") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        item {
            OutlinedTextField(
                value = printMinutes,
                onValueChange = { printMinutes = it },
                label = { Text("Minutos") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        item {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Peso por unidad (g)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        item {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        /*************** Datos comerciales ***************/
        item {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "🛍️ Datos comerciales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            CampoFechaArgentina(
                label = "Fecha presupuesto",
                fecha = quoteDate,
                onClick = {
                    showQuoteDatePicker = true
                }
            )
        }

        item {
            OutlinedTextField(
                value = validityDaysText,
                onValueChange = { validityDaysText = it },
                label = { Text("Validez del presupuesto (días)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        item {
            OutlinedTextField(
                value = deliveryBusinessDaysText,
                onValueChange = { deliveryBusinessDaysText = it },
                label = { Text("Plazo de entrega (días hábiles)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0FFF4)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Entrega estimada: ${calculatedDeliveryDate.format(formatoFechaArgentina)}",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D5A27)
                )
            }
        }

        item {
            DropdownEnum(
                label = "Forma de pago",
                selected = paymentType,
                textForValue = { it.label },
                onSelect = { paymentType = it }
            )
        }

        if (paymentType == PaymentType.DEPOSIT) {
            item {
                OutlinedTextField(
                    value = depositPercentageText,
                    onValueChange = { depositPercentageText = it },
                    label = { Text("Porcentaje de seña (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        item {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isFriend,
                    onCheckedChange = { isFriend = it }
                )

                Text("Aplicar Descuento Amigo")
            }
        }

        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                minLines = 2
            )
        }

        /*************** Botón calcular ***************/
        item {
            Button(
                onClick = {
                    val deliveryBusinessDays =
                        deliveryBusinessDaysText.toIntOrNull() ?: 0

                    val deliveryDate = addBusinessDays(
                        startDate = quoteDate,
                        businessDays = deliveryBusinessDays
                    )

                    val depositPercentage =
                        depositPercentageText.toIntOrNull() ?: 0

                    val order = Order(
                        id = editingOrder?.id ?: UUID.randomUUID().toString(),

                        title = title,
                        clientName = clientName,

                        filament = selectedFilament,
                        printer = selectedPrinter,

                        printTimeHours = printHours.toIntOrNull() ?: 0,
                        printTimeMinutes = printMinutes.toIntOrNull() ?: 0,
                        weightGramsPerUnit = weight.toIntOrNull() ?: 0,
                        quantity = quantity.toIntOrNull() ?: 1,

                        designType = selectedDesign,
                        color = color,

                        isFriend = isFriend,

                        status = editingOrder?.status ?: OrderStatus.PENDING,

                        /*
                            Si estamos editando, se conserva createdAt.
                        */
                        createdAt = editingOrder?.createdAt ?: LocalDate.now().toString(),

                        /*
                            Se guardan internamente en ISO yyyy-MM-dd.
                            Se muestran en pantalla como dd/MM/yyyy.
                        */
                        quoteDate = quoteDate.toString(),
                        validityDays = validityDaysText.toIntOrNull() ?: 15,
                        deliveryBusinessDays = deliveryBusinessDays,
                        deliveryDate = deliveryDate.toString(),

                        paymentType = paymentType,
                        depositPercentage = depositPercentage,

                        finishType = finishType,
                        notes = notes
                    )

                    val result = Calculator.calculateQuote(
                        data = order,
                        settings = state.quoteSettings
                    )

                    order.totalComputed = result.totalFinal

                    currentOrderData = order
                    breakdown = result
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcular ✨")
            }
        }
    }
}

/*************** Campo de fecha argentino ***************/
/*
    Campo visual no editable por teclado.
    Al tocarlo, se abre el calendario.
*/
@Composable
private fun CampoFechaArgentina(
    label: String,
    fecha: LocalDate,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = fecha.format(formatoFechaArgentina),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = Color.Transparent
        )
    )
}

/*************** Diálogo con calendario ***************/
/*
    Este diálogo muestra el calendario Material3.
    La fecha seleccionada vuelve como LocalDate.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarioFechaDialog(
    fechaInicial: LocalDate,
    onCancelar: () -> Unit,
    onAceptar: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = localDateToUtcMillis(fechaInicial)
    )

    DatePickerDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis

                    if (millis != null) {
                        onAceptar(utcMillisToLocalDate(millis))
                    } else {
                        onCancelar()
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelar
            ) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false
        )
    }
}

/*************** Parseo simple de fecha ISO ***************/
/*
    Espera formato interno yyyy-MM-dd.
*/
private fun parseIsoDateOrNull(value: String): LocalDate? {
    return try {
        LocalDate.parse(value)
    } catch (e: Exception) {
        null
    }
}

/*************** Formatear fecha guardada para mostrar ***************/
/*
    Convierte yyyy-MM-dd a dd/MM/yyyy.
    Si algo viniera mal, devuelve el texto original.
*/
private fun formatearFechaArgentina(value: String): String {
    val date = parseIsoDateOrNull(value)
    return date?.format(formatoFechaArgentina) ?: value
}

/*************** Sumar días hábiles ***************/
/*
    Suma días hábiles a partir de una fecha inicial.

    No cuenta sábados ni domingos.

    Si businessDays es 0, devuelve la misma fecha inicial.
*/
private fun addBusinessDays(
    startDate: LocalDate,
    businessDays: Int
): LocalDate {
    var date = startDate
    var remainingDays = businessDays.coerceAtLeast(0)

    while (remainingDays > 0) {
        date = date.plusDays(1)

        val isWeekend =
            date.dayOfWeek == DayOfWeek.SATURDAY ||
                    date.dayOfWeek == DayOfWeek.SUNDAY

        if (!isWeekend) {
            remainingDays--
        }
    }

    return date
}

/*************** Conversión LocalDate -> millis UTC ***************/
/*
    El DatePicker trabaja con milisegundos.
    Usamos UTC para evitar corrimientos de día por zona horaria.
*/
private fun localDateToUtcMillis(date: LocalDate): Long {
    return date
        .atStartOfDay()
        .toInstant(ZoneOffset.UTC)
        .toEpochMilli()
}

/*************** Conversión millis UTC -> LocalDate ***************/
/*
    Convierte la fecha seleccionada en el calendario a LocalDate.
*/
private fun utcMillisToLocalDate(millis: Long): LocalDate {
    return Instant
        .ofEpochMilli(millis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}

/*************** Dropdown genérico para enums ***************/
/*
    Sirve para Material, Impresora, Diseño, Forma de pago y Acabado.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private inline fun <reified T : Enum<T>> DropdownEnum(
    label: String,
    selected: T,
    crossinline textForValue: (T) -> String,
    crossinline onSelect: (T) -> Unit
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
            value = textForValue(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
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
            enumValues<T>().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(textForValue(option))
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}