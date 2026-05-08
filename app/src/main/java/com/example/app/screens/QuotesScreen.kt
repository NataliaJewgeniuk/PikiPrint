package com.example.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocalPrintshop
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.models.DesignType
import com.example.app.models.FilamentType
import com.example.app.models.FinishType
import com.example.app.models.Order
import com.example.app.models.OrderStatus
import com.example.app.models.PaymentType
import com.example.app.models.PrinterProfile
import com.example.app.models.PrinterType
import com.example.app.models.defaultProfileId
import com.example.app.ui.components.kawaiiShadow
import com.example.app.ui.theme.PikiClay
import com.example.app.ui.theme.PikiCreamLine
import com.example.app.ui.theme.PikiLeaf
import com.example.app.ui.theme.PikiLeafDark
import com.example.app.ui.theme.PikiMutedText
import com.example.app.ui.theme.PikiPaper
import com.example.app.ui.theme.PikiPaperDark
import com.example.app.ui.theme.PikiSky
import com.example.app.ui.theme.PikiWhite
import com.example.app.ui.theme.PikiWood
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

    Internamente se sigue guardando como ISO:
    yyyy-MM-dd
*/
private val formatoFechaArgentina: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy")

/*************** Colores locales de cotización ***************/
/*
    Ajustados a la estética de la web:
    - crema papel;
    - marrón craft;
    - celeste botón;
    - rosa resultado;
    - inputs crema suave.
*/
private val QuoteText = Color(0xFF231727)
private val QuoteSurfaceLow = Color(0xFFF5F0E6)
private val QuotePink = Color(0xFFFE8AA6)
private val QuotePinkSoft = Color(0xFFFFDCE5)
private val QuoteWoodSoft = Color(0xFFB98B5F)
private val QuotePurple = Color(0xFFB584E8)
private val QuoteOrange = Color(0xFFFFB95A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    viewModel: AppViewModel,
    onGoToOrders: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val editingOrder by viewModel.editingOrder.collectAsState()

    /*************** Estados del formulario técnico ***************/
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

    /*************** Impresoras dinámicas ***************/
    val allPrinterProfiles =
        state.quoteSettings.printerProfiles

    val selectablePrinterProfiles =
        remember(
            allPrinterProfiles,
            editingOrder?.id,
            editingOrder?.printerId,
            editingOrder?.printerNameSnapshot
        ) {
            val activeProfiles =
                allPrinterProfiles.filter { profile ->
                    profile.isActive
                }

            val editingProfile =
                editingOrder?.let { order ->
                    allPrinterProfiles.firstOrNull { profile ->
                        profile.id == order.printerId
                    } ?: PrinterProfile(
                        id = order.printerId,
                        name = order.printerNameSnapshot.ifBlank {
                            order.printer.label
                        },
                        price = 0.0,
                        lifespanHours = 0,
                        powerKw = 0.0,
                        isActive = false
                    )
                }

            val result =
                activeProfiles.toMutableList()

            if (
                editingProfile != null &&
                result.none { profile -> profile.id == editingProfile.id }
            ) {
                result.add(0, editingProfile)
            }

            result
        }

    var selectedPrinterId by remember(editingOrder?.id) {
        mutableStateOf(
            editingOrder?.printerId.orEmpty()
        )
    }

    LaunchedEffect(
        selectablePrinterProfiles,
        editingOrder?.id
    ) {
        if (
            selectedPrinterId.isBlank() ||
            selectablePrinterProfiles.none { profile -> profile.id == selectedPrinterId }
        ) {
            selectedPrinterId =
                selectablePrinterProfiles.firstOrNull()?.id
                    ?: PrinterType.A1_COMBO.defaultProfileId
        }
    }

    val selectedPrinterProfile =
        selectablePrinterProfiles.firstOrNull { profile ->
            profile.id == selectedPrinterId
        } ?: selectablePrinterProfiles.firstOrNull()

    var selectedDesign by remember(editingOrder) {
        mutableStateOf(editingOrder?.designType ?: DesignType.EXTERNAL)
    }

    var finishType by remember(editingOrder) {
        mutableStateOf(editingOrder?.finishType ?: FinishType.VISIBLE_LINES)
    }

    var isFriend by remember(editingOrder) {
        mutableStateOf(editingOrder?.isFriend ?: false)
    }

    /*************** Estados del formulario comercial ***************/
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

    /*************** Calcular presupuesto ***************/
    fun calcularPresupuesto() {
        val deliveryBusinessDays =
            deliveryBusinessDaysText.toIntOrNull() ?: 0

        val deliveryDate =
            addBusinessDays(
                startDate = quoteDate,
                businessDays = deliveryBusinessDays
            )

        val depositPercentage =
            depositPercentageText.toIntOrNull() ?: 0

        val resolvedPrinterProfile =
            selectedPrinterProfile

        val legacyPrinter =
            legacyPrinterFromProfileId(
                resolvedPrinterProfile?.id
                    ?: selectedPrinterId.ifBlank {
                        PrinterType.A1_COMBO.defaultProfileId
                    }
            )

        val order = Order(
            id = editingOrder?.id ?: UUID.randomUUID().toString(),

            title = title,
            clientName = clientName,

            filament = selectedFilament,

            /*************** Compatibilidad heredada ***************/
            printer = legacyPrinter,

            /*************** Impresora dinámica ***************/
            printerId =
                resolvedPrinterProfile?.id
                    ?: legacyPrinter.defaultProfileId,

            printerNameSnapshot =
                resolvedPrinterProfile?.name
                    ?: legacyPrinter.label,

            printTimeHours = printHours.toIntOrNull() ?: 0,
            printTimeMinutes = printMinutes.toIntOrNull() ?: 0,
            weightGramsPerUnit = weight.toIntOrNull() ?: 0,
            quantity = quantity.toIntOrNull() ?: 1,

            designType = selectedDesign,
            color = color,

            isFriend = isFriend,

            status = editingOrder?.status ?: OrderStatus.PENDING,

            createdAt = editingOrder?.createdAt ?: LocalDate.now().toString(),

            quoteDate = quoteDate.toString(),
            validityDays = validityDaysText.toIntOrNull() ?: 15,
            deliveryBusinessDays = deliveryBusinessDays,
            deliveryDate = deliveryDate.toString(),

            paymentType = paymentType,
            depositPercentage = depositPercentage,

            finishType = finishType,
            notes = notes
        )

        val result =
            Calculator.calculateQuote(
                data = order,
                settings = state.quoteSettings
            )

        val orderWithBreakdown =
            order.copy(
                totalComputed = result.totalFinal,

                subtotal = result.subtotal,
                margenMonto = result.margenMonto,
                descuentoCantidad = result.descuentoCantidad,
                descuentoAmigo = result.descuentoAmigo,
                totalFinal = result.totalFinal
            )

        currentOrderData = orderWithBreakdown
        breakdown = result
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
        QuoteResultScreen(
            breakdown = breakdown!!,
            order = currentOrderData,
            isEditing = editingOrder != null,
            onSave = {
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
            onBack = {
                breakdown = null
            }
        )

        return
    }

    /*************** Pantalla de formulario ***************/
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PikiPaper)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 28.dp,
                    bottom = 22.dp
                )
            ) {
                item {
                    QuoteHeader(
                        editing = editingOrder != null
                    )
                }

                item {
                    ProjectDetailsCard(
                        title = title,
                        onTitleChange = {
                            title = it
                        },
                        clientName = clientName,
                        onClientNameChange = {
                            clientName = it
                        },
                        selectedPrinterProfile = selectedPrinterProfile,
                        selectablePrinterProfiles = selectablePrinterProfiles,
                        onPrinterSelected = { profile ->
                            selectedPrinterId = profile.id
                        },
                        selectedFilament = selectedFilament,
                        onFilamentSelected = {
                            selectedFilament = it
                        },
                        color = color,
                        onColorChange = {
                            color = it
                        },
                        finishType = finishType,
                        onFinishTypeSelected = {
                            finishType = it
                        }
                    )
                }

                item {
                    TechnicalSpecsCard(
                        printHours = printHours,
                        onPrintHoursChange = {
                            printHours = it
                        },
                        printMinutes = printMinutes,
                        onPrintMinutesChange = {
                            printMinutes = it
                        },
                        weight = weight,
                        onWeightChange = {
                            weight = it
                        },
                        quantity = quantity,
                        onQuantityChange = {
                            quantity = it
                        },
                        selectedDesign = selectedDesign,
                        onDesignSelected = {
                            selectedDesign = it
                        },
                        isFriend = isFriend,
                        onFriendChange = {
                            isFriend = it
                        }
                    )
                }

                item {
                    CommercialDataCard(
                        quoteDate = quoteDate,
                        onQuoteDateClick = {
                            showQuoteDatePicker = true
                        },
                        validityDaysText = validityDaysText,
                        onValidityDaysChange = {
                            validityDaysText = it
                        },
                        deliveryBusinessDaysText = deliveryBusinessDaysText,
                        onDeliveryBusinessDaysChange = {
                            deliveryBusinessDaysText = it
                        },
                        calculatedDeliveryDate = calculatedDeliveryDate,
                        paymentType = paymentType,
                        onPaymentTypeSelected = {
                            paymentType = it
                        },
                        depositPercentageText = depositPercentageText,
                        onDepositPercentageChange = {
                            depositPercentageText = it
                        },
                        notes = notes,
                        onNotesChange = {
                            notes = it
                        }
                    )
                }

                item {
                    PrinterStatusCard(
                        selectedPrinterProfile = selectedPrinterProfile
                    )
                }
            }

            CalculateButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 18.dp),
                onClick = {
                    calcularPresupuesto()
                }
            )
        }
    }
}

/*************** Header ***************/
@Composable
private fun QuoteHeader(
    editing: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (editing) {
                "Editar Cotización"
            } else {
                "Nueva Cotización"
            },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = PikiClay,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (editing) {
                "Ajustá los datos del pedido y recalculá el presupuesto."
            } else {
                "Calculá el costo perfecto para tu próxima creación."
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = PikiMutedText,
            lineHeight = 20.sp
        )
    }
}

/*************** Card: detalles del proyecto ***************/
@Composable
private fun ProjectDetailsCard(
    title: String,
    onTitleChange: (String) -> Unit,
    clientName: String,
    onClientNameChange: (String) -> Unit,
    selectedPrinterProfile: PrinterProfile?,
    selectablePrinterProfiles: List<PrinterProfile>,
    onPrinterSelected: (PrinterProfile) -> Unit,
    selectedFilament: FilamentType,
    onFilamentSelected: (FilamentType) -> Unit,
    color: String,
    onColorChange: (String) -> Unit,
    finishType: FinishType,
    onFinishTypeSelected: (FinishType) -> Unit
) {
    QuoteFormCard(
        title = "Detalles del Proyecto",
        icon = Icons.Outlined.EditNote
    ) {
        PikiInputField(
            label = "Título del Proyecto",
            value = title,
            onValueChange = onTitleChange,
            placeholder = "Ej: Dragón Articulado",
            icon = Icons.Outlined.Edit
        )

        PikiInputField(
            label = "Nombre del Cliente",
            value = clientName,
            onValueChange = onClientNameChange,
            placeholder = "Ej: Juan Pérez",
            icon = Icons.Outlined.Person
        )

        PrinterProfileDropdown(
            label = "Impresora",
            profiles = selectablePrinterProfiles,
            selectedProfile = selectedPrinterProfile,
            onSelect = onPrinterSelected
        )

        DropdownEnum(
            label = "Material",
            selected = selectedFilament,
            textForValue = { it.label },
            onSelect = onFilamentSelected
        )

        ColorInputRow(
            color = color,
            onColorChange = onColorChange
        )

        DropdownEnum(
            label = "Tipo de acabado",
            selected = finishType,
            textForValue = { it.label },
            onSelect = onFinishTypeSelected
        )
    }
}

/*************** Card: especificaciones técnicas ***************/
@Composable
private fun TechnicalSpecsCard(
    printHours: String,
    onPrintHoursChange: (String) -> Unit,
    printMinutes: String,
    onPrintMinutesChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    selectedDesign: DesignType,
    onDesignSelected: (DesignType) -> Unit,
    isFriend: Boolean,
    onFriendChange: (Boolean) -> Unit
) {
    QuoteFormCard(
        title = "Especificaciones Técnicas",
        icon = Icons.Outlined.Tune
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PikiInputField(
                modifier = Modifier.weight(1f),
                label = "Horas",
                value = printHours,
                onValueChange = onPrintHoursChange,
                placeholder = "04",
                icon = Icons.Outlined.Schedule,
                keyboardType = KeyboardType.Number
            )

            PikiInputField(
                modifier = Modifier.weight(1f),
                label = "Minutos",
                value = printMinutes,
                onValueChange = onPrintMinutesChange,
                placeholder = "30",
                icon = Icons.Outlined.Schedule,
                keyboardType = KeyboardType.Number
            )
        }

        PikiInputField(
            label = "Peso por unidad (gramos)",
            value = weight,
            onValueChange = onWeightChange,
            placeholder = "150",
            icon = Icons.Outlined.Straighten,
            keyboardType = KeyboardType.Number
        )

        PikiInputField(
            label = "Cantidad",
            value = quantity,
            onValueChange = onQuantityChange,
            placeholder = "1",
            icon = Icons.Outlined.Inventory2,
            keyboardType = KeyboardType.Number
        )

        DesignSelector(
            selectedDesign = selectedDesign,
            onDesignSelected = onDesignSelected
        )

        FriendDiscountRow(
            checked = isFriend,
            onCheckedChange = onFriendChange
        )
    }
}

/*************** Card: datos comerciales ***************/
@Composable
private fun CommercialDataCard(
    quoteDate: LocalDate,
    onQuoteDateClick: () -> Unit,
    validityDaysText: String,
    onValidityDaysChange: (String) -> Unit,
    deliveryBusinessDaysText: String,
    onDeliveryBusinessDaysChange: (String) -> Unit,
    calculatedDeliveryDate: LocalDate,
    paymentType: PaymentType,
    onPaymentTypeSelected: (PaymentType) -> Unit,
    depositPercentageText: String,
    onDepositPercentageChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit
) {
    QuoteFormCard(
        title = "Datos Comerciales",
        icon = Icons.Outlined.ReceiptLong
    ) {
        PikiDateField(
            label = "Fecha presupuesto",
            value = quoteDate.format(formatoFechaArgentina),
            onClick = onQuoteDateClick
        )

        PikiInputField(
            label = "Validez del presupuesto (días)",
            value = validityDaysText,
            onValueChange = onValidityDaysChange,
            placeholder = "15",
            icon = Icons.Outlined.CalendarMonth,
            keyboardType = KeyboardType.Number
        )

        PikiInputField(
            label = "Plazo de entrega (días hábiles)",
            value = deliveryBusinessDaysText,
            onValueChange = onDeliveryBusinessDaysChange,
            placeholder = "7",
            icon = Icons.Outlined.Schedule,
            keyboardType = KeyboardType.Number
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = PikiLeaf.copy(alpha = 0.32f),
            border = BorderStroke(
                width = 1.5.dp,
                color = PikiLeaf.copy(alpha = 0.60f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = PikiClay,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Entrega estimada",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiClay
                    )

                    Text(
                        text = calculatedDeliveryDate.format(formatoFechaArgentina),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiWood
                    )
                }
            }
        }

        DropdownEnum(
            label = "Forma de pago",
            selected = paymentType,
            textForValue = { it.label },
            onSelect = onPaymentTypeSelected
        )

        if (paymentType == PaymentType.DEPOSIT) {
            PikiInputField(
                label = "Porcentaje de seña (%)",
                value = depositPercentageText,
                onValueChange = onDepositPercentageChange,
                placeholder = "50",
                icon = Icons.Outlined.Percent,
                keyboardType = KeyboardType.Number
            )
        }

        PikiInputField(
            label = "Notas",
            value = notes,
            onValueChange = onNotesChange,
            placeholder = "Indicaciones especiales para el pedido...",
            icon = Icons.Outlined.EditNote,
            minHeight = 86.dp,
            singleLine = false
        )
    }
}

/*************** Card base del formulario ***************/
@Composable
private fun QuoteFormCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 3.dp,
            color = PikiClay
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PikiClay,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiClay
                )
            }

            content()
        }
    }
}

/*************** Campo de texto estilo web ***************/
@Composable
private fun PikiInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minHeight: androidx.compose.ui.unit.Dp = 54.dp,
    singleLine: Boolean = true
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiClay.copy(alpha = 0.82f)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            shape = RoundedCornerShape(999.dp),
            color = QuoteSurfaceLow
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = if (singleLine) 0.dp else 12.dp),
                verticalAlignment = if (singleLine) {
                    Alignment.CenterVertically
                } else {
                    Alignment.Top
                }
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = if (singleLine) 54.dp else 60.dp),
                    contentAlignment = if (singleLine) {
                        Alignment.CenterStart
                    } else {
                        Alignment.TopStart
                    }
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            color = PikiMutedText.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = singleLine,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = QuoteText,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PikiClay.copy(alpha = 0.45f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/*************** Campo de fecha ***************/
@Composable
private fun PikiDateField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiClay.copy(alpha = 0.82f)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(999.dp))
                .clickable {
                    onClick()
                },
            shape = RoundedCornerShape(999.dp),
            color = QuoteSurfaceLow
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = QuoteText
                )

                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = PikiClay.copy(alpha = 0.55f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/*************** Color ***************/
@Composable
private fun ColorInputRow(
    color: String,
    onColorChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Color",
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiClay.copy(alpha = 0.82f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorStripe(
                color = QuotePurple
            )

            Spacer(modifier = Modifier.width(5.dp))

            ColorStripe(
                color = QuotePink
            )

            Spacer(modifier = Modifier.width(5.dp))

            ColorStripe(
                color = QuoteOrange
            )

            Spacer(modifier = Modifier.width(10.dp))

            PikiInputField(
                modifier = Modifier.weight(1f),
                label = "",
                value = color,
                onValueChange = onColorChange,
                placeholder = "Lila Pastel",
                icon = Icons.Outlined.ColorLens
            )
        }
    }
}

@Composable
private fun ColorStripe(
    color: Color
) {
    Box(
        modifier = Modifier
            .width(7.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(color)
    )
}

/*************** Selector de diseño ***************/
@Composable
private fun DesignSelector(
    selectedDesign: DesignType,
    onDesignSelected: (DesignType) -> Unit
) {
    Column {
        Text(
            text = "Complejidad del Diseño",
            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiClay.copy(alpha = 0.82f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DesignType.entries.forEach { design ->
                DesignChip(
                    text = design.label,
                    selected = selectedDesign == design,
                    onClick = {
                        onDesignSelected(design)
                    }
                )
            }
        }
    }
}

@Composable
private fun DesignChip(
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
            PikiClay
        } else {
            PikiWhite
        },
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) {
                PikiClay
            } else {
                PikiClay.copy(alpha = 0.28f)
            }
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (selected) {
                PikiWhite
            } else {
                PikiClay
            },
            maxLines = 1
        )
    }
}

/*************** Descuento amigo ***************/
@Composable
private fun FriendDiscountRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = QuotePinkSoft.copy(alpha = 0.52f),
        border = BorderStroke(
            width = 2.dp,
            color = QuotePink.copy(alpha = 0.32f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Percent,
                contentDescription = null,
                tint = QuotePink,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Aplicar Descuento Amigo",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PikiWhite,
                    checkedTrackColor = QuotePink,
                    uncheckedThumbColor = PikiWhite,
                    uncheckedTrackColor = PikiClay.copy(alpha = 0.20f)
                )
            )
        }
    }
}

/*************** Botón calcular ***************/
@Composable
private fun CalculateButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(top = 7.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(PikiClay)
        )

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .border(
                    width = 3.dp,
                    color = PikiClay,
                    shape = RoundedCornerShape(999.dp)
                ),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PikiSky,
                contentColor = PikiClay
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Text(
                text = "Calcular",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Outlined.Calculate,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/*************** Mini-card de impresora ***************/
@Composable
private fun PrinterStatusCard(
    selectedPrinterProfile: PrinterProfile?
) {
    val profile = selectedPrinterProfile

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(999.dp),
        color = PikiSky.copy(alpha = 0.22f),
        border = BorderStroke(
            width = 2.dp,
            color = PikiClay.copy(alpha = 0.70f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(999.dp),
                color = PikiSky,
                border = BorderStroke(
                    width = 2.dp,
                    color = PikiClay
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalPrintshop,
                        contentDescription = null,
                        tint = PikiClay,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (profile == null) {
                        "Sin impresora activa"
                    } else if (profile.isActive) {
                        "Impresora lista"
                    } else {
                        "Impresora histórica"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiClay
                )

                Text(
                    text = profile?.let {
                        "${it.name} · ${it.powerKw} kW · ${it.lifespanHours} h"
                    } ?: "Agregá una impresora desde Ajustes.",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = PikiMutedText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/*************** Resultado ***************/
/*
    Lo dejamos más prolijo que el original, pero la etapa fuerte
    del resultado queda para Cotización 2.
*/
/*@Composable
private fun QuoteResultScreen(
    breakdown: QuoteBreakdown,
    order: Order?,
    isEditing: Boolean,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PikiPaper)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Resultado",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .kawaiiShadow(),
                shape = RoundedCornerShape(36.dp),
                colors = CardDefaults.cardColors(
                    containerColor = QuotePink
                ),
                border = BorderStroke(
                    width = 3.dp,
                    color = PikiClay
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "TOTAL ESTIMADO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiWhite,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ARS ${"%.2f".format(breakdown.totalFinal)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = QuoteText
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        color = PikiWhite.copy(alpha = 0.92f),
                        border = BorderStroke(
                            width = 2.dp,
                            color = PikiWhite
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ResultLine(
                                icon = Icons.Outlined.Inventory2,
                                label = "Material",
                                value = "ARS ${"%.2f".format(breakdown.costoMaterial)}"
                            )

                            ResultLine(
                                icon = Icons.Outlined.Bolt,
                                label = "Energía",
                                value = "ARS ${"%.2f".format(breakdown.costoEnergia)}"
                            )

                            ResultLine(
                                icon = Icons.Outlined.LocalPrintshop,
                                label = "Desgaste",
                                value = "ARS ${"%.2f".format(breakdown.costoDegradamiento)}"
                            )

                            ResultLine(
                                icon = Icons.Outlined.Palette,
                                label = "Diseño",
                                value = "ARS ${"%.2f".format(breakdown.costoDiseno)}"
                            )

                            HorizontalDivider(
                                color = PikiClay.copy(alpha = 0.18f)
                            )

                            ResultLine(
                                icon = Icons.Outlined.TrendingUp,
                                label = "Margen",
                                value = "ARS ${"%.2f".format(breakdown.margenMonto)}",
                                strong = true
                            )

                            if (breakdown.descuentoCantidad > 0.0) {
                                ResultLine(
                                    icon = Icons.Outlined.Percent,
                                    label = "Desc. cantidad",
                                    value = "-ARS ${"%.2f".format(breakdown.descuentoCantidad)}"
                                )
                            }

                            if (breakdown.descuentoAmigo > 0.0) {
                                ResultLine(
                                    icon = Icons.Outlined.Percent,
                                    label = "Desc. amigo",
                                    value = "-ARS ${"%.2f".format(breakdown.descuentoAmigo)}"
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            order?.let {
                ResultInfoCard(order = it)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PikiClay,
                    contentColor = PikiWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (isEditing) {
                        "Guardar cambios"
                    } else {
                        "Guardar pedido"
                    },
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(
                    width = 2.dp,
                    color = PikiClay
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = PikiClay,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Volver a editar",
                    color = PikiClay,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ResultLine(
    icon: ImageVector,
    label: String,
    value: String,
    strong: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PikiClay,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (strong) {
                FontWeight.ExtraBold
            } else {
                FontWeight.Bold
            },
            color = PikiClay
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.ExtraBold,
            color = QuoteText
        )
    }
}

@Composable
private fun ResultInfoCard(
    order: Order
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = PikiWhite,
        border = BorderStroke(
            width = 2.dp,
            color = PikiCreamLine
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen comercial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay
            )

            Text(
                text = "Impresora: ${order.printerNameSnapshot.ifBlank { order.printer.label }}",
                fontWeight = FontWeight.Bold,
                color = PikiWood
            )

            Text(
                text = "Entrega estimada: ${formatearFechaArgentina(order.deliveryDate)}",
                fontWeight = FontWeight.Bold,
                color = PikiWood
            )

            Text(
                text = "Pago: ${order.paymentType.label}",
                fontWeight = FontWeight.Bold,
                color = PikiWood
            )

            if (order.paymentType == PaymentType.DEPOSIT) {
                Text(
                    text = "Seña: ${order.depositPercentage}%",
                    fontWeight = FontWeight.Bold,
                    color = PikiWood
                )
            }
        }
    }
}*/
/*************** Resultado estilo web ***************/
/*
    Etapa Cotización 2:
    - Card rosa protagonista para el total.
    - Desglose interno en tarjeta blanca.
    - Resumen comercial separado.
    - Acciones inferiores estilo web.
*/
@Composable
private fun QuoteResultScreen(
    breakdown: QuoteBreakdown,
    order: Order?,
    isEditing: Boolean,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PikiPaper)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 26.dp, bottom = 18.dp)
        ) {
            Text(
                text = if (isEditing) {
                    "Cotización actualizada"
                } else {
                    "Cotización calculada"
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Revisá el total, el desglose de costos y guardá el pedido.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PikiMutedText,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(bottom = 18.dp)
            ) {
                item {
                    TotalEstimateCard(
                        breakdown = breakdown
                    )
                }

                if (order != null) {
                    item {
                        ResultInfoCard(
                            order = order
                        )
                    }
                }
            }

            ResultActionRow(
                isEditing = isEditing,
                onSave = onSave,
                onBack = onBack
            )
        }
    }
}

/*************** Card rosa del total ***************/
@Composable
private fun TotalEstimateCard(
    breakdown: QuoteBreakdown
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(38.dp),
        colors = CardDefaults.cardColors(
            containerColor = QuotePink
        ),
        border = BorderStroke(
            width = 3.dp,
            color = PikiClay
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "TOTAL ESTIMADO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWhite,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ARS ${"%.2f".format(breakdown.totalFinal)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = QuoteText,
                lineHeight = 42.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            BreakdownCard(
                breakdown = breakdown
            )
        }
    }
}

/*************** Card blanca interna del desglose ***************/
@Composable
private fun BreakdownCard(
    breakdown: QuoteBreakdown
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = PikiWhite.copy(alpha = 0.92f),
        border = BorderStroke(
            width = 3.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            BreakdownLine(
                icon = Icons.Outlined.Inventory2,
                label = "Material",
                value = "ARS ${"%.2f".format(breakdown.costoMaterial)}"
            )

            BreakdownLine(
                icon = Icons.Outlined.Bolt,
                label = "Energía",
                value = "ARS ${"%.2f".format(breakdown.costoEnergia)}"
            )

            BreakdownLine(
                icon = Icons.Outlined.LocalPrintshop,
                label = "Desgaste",
                value = "ARS ${"%.2f".format(breakdown.costoDegradamiento)}"
            )

            BreakdownLine(
                icon = Icons.Outlined.Palette,
                label = "Diseño",
                value = "ARS ${"%.2f".format(breakdown.costoDiseno)}"
            )

            HorizontalDivider(
                color = PikiClay.copy(alpha = 0.18f),
                thickness = 1.dp
            )

            BreakdownLine(
                icon = Icons.Outlined.ReceiptLong,
                label = "Subtotal",
                value = "ARS ${"%.2f".format(breakdown.subtotal)}",
                muted = true
            )

            val marginPercent =
                if (breakdown.subtotal > 0.0) {
                    (breakdown.margenMonto / breakdown.subtotal) * 100.0
                } else {
                    0.0
                }

            BreakdownLine(
                icon = Icons.Outlined.TrendingUp,
                label = "Margen (${"%.0f".format(marginPercent)}%)",
                value = "ARS ${"%.2f".format(breakdown.margenMonto)}",
                strong = true
            )

            if (breakdown.descuentoCantidad > 0.0) {
                BreakdownLine(
                    icon = Icons.Outlined.Percent,
                    label = "Desc. cantidad",
                    value = "-ARS ${"%.2f".format(breakdown.descuentoCantidad)}",
                    discount = true
                )
            }

            if (breakdown.descuentoAmigo > 0.0) {
                BreakdownLine(
                    icon = Icons.Outlined.Percent,
                    label = "Desc. amigo",
                    value = "-ARS ${"%.2f".format(breakdown.descuentoAmigo)}",
                    discount = true
                )
            }
        }
    }
}

/*************** Línea del desglose ***************/
@Composable
private fun BreakdownLine(
    icon: ImageVector,
    label: String,
    value: String,
    strong: Boolean = false,
    muted: Boolean = false,
    discount: Boolean = false
) {
    val lineColor =
        when {
            discount -> PikiLeaf
            muted -> PikiClay.copy(alpha = 0.58f)
            else -> PikiClay
        }

    val valueColor =
        when {
            discount -> PikiLeafDark
            strong -> QuoteText
            muted -> PikiClay.copy(alpha = 0.58f)
            else -> PikiClay
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = lineColor,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(9.dp))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (strong) {
                FontWeight.ExtraBold
            } else {
                FontWeight.Bold
            },
            color = lineColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}

/*************** Resumen comercial ***************/
@Composable
private fun ResultInfoCard(
    order: Order
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite
        ),
        border = BorderStroke(
            width = 2.dp,
            color = PikiCreamLine
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = PikiClay,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(9.dp))

                Text(
                    text = "Resumen comercial",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiClay
                )
            }

            HorizontalDivider(
                color = PikiCreamLine.copy(alpha = 0.75f),
                thickness = 1.dp
            )

            ResultInfoRow(
                icon = Icons.Outlined.EditNote,
                label = "Proyecto",
                value = order.title.ifBlank { "Sin título" }
            )

            ResultInfoRow(
                icon = Icons.Outlined.Person,
                label = "Cliente",
                value = order.clientName.ifBlank { "Sin cliente" }
            )

            ResultInfoRow(
                icon = Icons.Outlined.LocalPrintshop,
                label = "Impresora",
                value = order.printerNameSnapshot.ifBlank {
                    order.printer.label
                }
            )

            ResultInfoRow(
                icon = Icons.Outlined.CalendarMonth,
                label = "Entrega",
                value = formatearFechaArgentina(order.deliveryDate)
            )

            ResultInfoRow(
                icon = Icons.Outlined.Payments,
                label = "Pago",
                value = if (order.paymentType == PaymentType.DEPOSIT) {
                    "${order.paymentType.label} · ${order.depositPercentage}%"
                } else {
                    order.paymentType.label
                }
            )

            if (order.notes.isNotBlank()) {
                ResultInfoRow(
                    icon = Icons.Outlined.Edit,
                    label = "Notas",
                    value = order.notes
                )
            }
        }
    }
}

/*************** Fila del resumen comercial ***************/
@Composable
private fun ResultInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = RoundedCornerShape(999.dp),
            color = PikiSky.copy(alpha = 0.28f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PikiClay,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiClay.copy(alpha = 0.60f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWood,
                lineHeight = 20.sp
            )
        }
    }
}

/*************** Acciones inferiores del resultado ***************/
@Composable
private fun ResultActionRow(
    isEditing: Boolean,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onSave,
            modifier = Modifier
                .weight(1f)
                .height(58.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PikiClay,
                contentColor = PikiWhite
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Save,
                contentDescription = null,
                modifier = Modifier.size(21.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (isEditing) {
                    "Guardar cambios"
                } else {
                    "Guardar pedido"
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Surface(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(999.dp))
                .clickable {
                    onBack()
                },
            shape = RoundedCornerShape(999.dp),
            color = PikiWhite.copy(alpha = 0.70f),
            border = BorderStroke(
                width = 2.dp,
                color = PikiWhite
            ),
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Volver a editar",
                    tint = PikiClay,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
/*************** Dropdown genérico para enums ***************/
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

    Column {
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiClay.copy(alpha = 0.82f)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            DropdownAnchor(
                modifier = Modifier.menuAnchor(),
                value = textForValue(selected),
                active = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                containerColor = PikiWhite
            ) {
                enumValues<T>().forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = textForValue(option),
                                color = QuoteText,
                                fontWeight = FontWeight.Bold
                            )
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
}

/*************** Dropdown de impresoras dinámicas ***************/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrinterProfileDropdown(
    label: String,
    profiles: List<PrinterProfile>,
    selectedProfile: PrinterProfile?,
    onSelect: (PrinterProfile) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column {
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiClay.copy(alpha = 0.82f)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (profiles.isNotEmpty()) {
                    expanded = !expanded
                }
            }
        ) {
            DropdownAnchor(
                modifier = Modifier.menuAnchor(),
                value = selectedProfile?.let { profile ->
                    if (profile.isActive) {
                        profile.name
                    } else {
                        "${profile.name} (inactiva)"
                    }
                } ?: "Sin impresoras activas",
                active = profiles.isNotEmpty()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                containerColor = PikiWhite
            ) {
                profiles.forEach { profile ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (profile.isActive) {
                                    profile.name
                                } else {
                                    "${profile.name} (inactiva)"
                                },
                                color = QuoteText,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            onSelect(profile)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/*************** Ancla visual de dropdown ***************/
@Composable
private fun DropdownAnchor(
    modifier: Modifier = Modifier,
    value: String,
    active: Boolean
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(999.dp),
        color = QuoteSurfaceLow
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (active) {
                    QuoteText
                } else {
                    PikiMutedText
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = PikiClay.copy(alpha = 0.55f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/*************** Calendario para fecha ***************/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarioFechaDialog(
    fechaInicial: LocalDate,
    onCancelar: () -> Unit,
    onAceptar: (LocalDate) -> Unit
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = localDateToUtcMillis(fechaInicial)
        )

    DatePickerDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis =
                        datePickerState.selectedDateMillis

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
private fun parseIsoDateOrNull(
    value: String
): LocalDate? {
    return try {
        LocalDate.parse(value)
    } catch (e: Exception) {
        null
    }
}

/*************** Formatear fecha guardada para mostrar ***************/
private fun formatearFechaArgentina(
    value: String
): String {
    val date =
        parseIsoDateOrNull(value)

    return date?.format(formatoFechaArgentina) ?: value
}

/*************** Sumar días hábiles ***************/
private fun addBusinessDays(
    startDate: LocalDate,
    businessDays: Int
): LocalDate {
    var date =
        startDate

    var remainingDays =
        businessDays.coerceAtLeast(0)

    while (remainingDays > 0) {
        date =
            date.plusDays(1)

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
private fun localDateToUtcMillis(
    date: LocalDate
): Long {
    return date
        .atStartOfDay()
        .toInstant(ZoneOffset.UTC)
        .toEpochMilli()
}

/*************** Conversión millis UTC -> LocalDate ***************/
private fun utcMillisToLocalDate(
    millis: Long
): LocalDate {
    return Instant
        .ofEpochMilli(millis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}

/*************** Compatibilidad con PrinterType heredado ***************/
private fun legacyPrinterFromProfileId(
    printerId: String
): PrinterType {
    return when (printerId) {
        PrinterType.A1_COMBO.defaultProfileId -> PrinterType.A1_COMBO
        PrinterType.A1_MINI.defaultProfileId -> PrinterType.A1_MINI
        else -> PrinterType.A1_COMBO
    }
}