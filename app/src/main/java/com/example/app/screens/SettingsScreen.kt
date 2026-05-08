package com.example.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalPrintshop
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.models.DesignType
import com.example.app.models.FilamentType
import com.example.app.models.PrinterProfile
import com.example.app.models.QuoteSettings
import com.example.app.ui.components.kawaiiShadow
import com.example.app.ui.theme.PikiClay
import com.example.app.ui.theme.PikiCreamLine
import com.example.app.ui.theme.PikiLeaf
import com.example.app.ui.theme.PikiPaper
import com.example.app.ui.theme.PikiSky
import com.example.app.ui.theme.PikiWhite
import com.example.app.viewmodels.AppViewModel
import kotlinx.coroutines.delay

/*************** Colores específicos de Ajustes ***************/
/*
    Estos colores vienen del HTML de Settings.
    Los dejamos locales para no obligarte a tocar Color.kt en esta etapa.
*/
private val PikiSettingsText = Color(0xFF3D6472)
private val PikiAccentPurple = Color(0xFFD6B7F0)
private val PikiAccentPink = Color(0xFFFFB1C1)
private val PikiButtonShadowBrown = Color(0xFF5D4037)
private val PikiDangerBg = Color(0xFFFFD8D2)
private val PikiDangerText = Color(0xFF9C3F32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AppViewModel
) {
    val state by viewModel.state.collectAsState()
    val settings = state.quoteSettings

    /*************** Estados generales ***************/
    var energyRate by remember(settings) {
        mutableStateOf(settings.energyRate.toString())
    }

    var margin by remember(settings) {
        mutableStateOf(settings.marginPercentage.toString())
    }

    var friendDiscount by remember(settings) {
        mutableStateOf(settings.friendDiscountPercentage.toString())
    }

    /*************** Estados de filamento ***************/
    val filamentInputs = remember(settings) {
        FilamentType.entries.associateWith { type ->
            mutableStateOf(
                (settings.filamentCost[type] ?: 0.0).toString()
            )
        }
    }

    /*************** Estados de descuentos por cantidad ***************/
    val quantityDiscountInputs = remember(settings) {
        listOf(25, 50, 75, 100).associateWith { quantity ->
            mutableStateOf(
                (settings.quantityDiscounts[quantity] ?: 0.0).toString()
            )
        }
    }

    /*************** Estados de costos de diseño ***************/
    val designCostInputs = remember(settings) {
        DesignType.entries.associateWith { designType ->
            mutableStateOf(
                (settings.designCosts[designType] ?: 0.0).toString()
            )
        }
    }

    /*************** Estado de feedback de guardado ***************/
    var savedFeedbackVisible by remember {
        mutableStateOf(false)
    }

    /*************** Estados de impresoras dinámicas ***************/
    var selectedPrinterForEdit by remember {
        mutableStateOf<PrinterProfile?>(null)
    }

    var showAddPrinterSheet by remember {
        mutableStateOf(false)
    }

    val printerSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    /*************** Ocultar feedback después de guardar ***************/
    LaunchedEffect(savedFeedbackVisible) {
        if (savedFeedbackVisible) {
            delay(1600)
            savedFeedbackVisible = false
        }
    }

    /*************** Guardar cambios generales ***************/
    /*
        Importante:
        Las impresoras dinámicas NO se reconstruyen acá.
        Se preserva settings.printerProfiles tal como esté.
        Las impresoras se agregan/editan/desactivan mediante funciones del ViewModel.
    */
    fun guardarCambios() {
        val updatedFilamentCost = mutableMapOf<FilamentType, Double>()

        FilamentType.entries.forEach { type ->
            updatedFilamentCost[type] =
                filamentInputs[type]?.value?.toDoubleOrNull()
                    ?: settings.filamentCost[type]
                            ?: 0.0
        }

        val updatedQuantityDiscounts = mutableMapOf<Int, Double>()

        listOf(25, 50, 75, 100).forEach { quantity ->
            updatedQuantityDiscounts[quantity] =
                quantityDiscountInputs[quantity]?.value?.toDoubleOrNull()
                    ?: settings.quantityDiscounts[quantity]
                            ?: 0.0
        }

        val updatedDesignCosts = mutableMapOf<DesignType, Double>()

        DesignType.entries.forEach { designType ->
            updatedDesignCosts[designType] =
                designCostInputs[designType]?.value?.toDoubleOrNull()
                    ?: settings.designCosts[designType]
                            ?: 0.0
        }

        val updatedSettings: QuoteSettings = settings.copy(
            filamentCost = updatedFilamentCost,
            energyRate = energyRate.toDoubleOrNull() ?: settings.energyRate,
            marginPercentage = margin.toDoubleOrNull() ?: settings.marginPercentage,
            quantityDiscounts = updatedQuantityDiscounts,
            friendDiscountPercentage =
                friendDiscount.toDoubleOrNull()
                    ?: settings.friendDiscountPercentage,
            designCosts = updatedDesignCosts,
            printerProfiles = settings.printerProfiles.toMutableList()
        )

        viewModel.updateSettings(updatedSettings)
        savedFeedbackVisible = true
    }

    /*************** Pantalla ***************/
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PikiPaper)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(22.dp),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 26.dp,
                bottom = 126.dp
            )
        ) {
            item {
                SettingsHeader()
            }

            item {
                BaseCostsCard(
                    energyRate = energyRate,
                    onEnergyRateChange = {
                        energyRate = it
                    },
                    margin = margin,
                    onMarginChange = {
                        margin = it
                    },
                    friendDiscount = friendDiscount,
                    onFriendDiscountChange = {
                        friendDiscount = it
                    }
                )
            }

            item {
                FilamentsCard(
                    filamentInputs = filamentInputs
                )
            }

            item {
                PrinterFleetCard(
                    profiles = settings.printerProfiles,
                    onAddPrinter = {
                        showAddPrinterSheet = true
                    },
                    onEditPrinter = { profile ->
                        selectedPrinterForEdit = profile
                    },
                    onDeactivatePrinter = { profile ->
                        viewModel.deactivatePrinterProfile(profile.id)
                        savedFeedbackVisible = true
                    },
                    onReactivatePrinter = { profile ->
                        viewModel.reactivatePrinterProfile(profile.id)
                        savedFeedbackVisible = true
                    }
                )
            }

            item {
                DesignComplexityCard(
                    designCostInputs = designCostInputs
                )
            }

            item {
                VolumeDiscountsCard(
                    quantityDiscountInputs = quantityDiscountInputs
                )
            }
        }

        SaveSettingsButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 22.dp),
            saved = savedFeedbackVisible,
            onClick = {
                guardarCambios()
            }
        )

        /*************** BottomSheet para agregar impresora ***************/
        if (showAddPrinterSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAddPrinterSheet = false
                },
                sheetState = printerSheetState,
                containerColor = PikiPaper,
                tonalElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                dragHandle = {
                    SheetDragHandle()
                }
            ) {
                PrinterEditSheet(
                    title = "Agregar impresora",
                    profile = null,
                    onClose = {
                        showAddPrinterSheet = false
                    },
                    onSave = { name, price, lifespanHours, powerKw ->
                        viewModel.addPrinterProfile(
                            name = name,
                            price = price,
                            lifespanHours = lifespanHours,
                            powerKw = powerKw
                        )

                        showAddPrinterSheet = false
                        savedFeedbackVisible = true
                    },
                    onDeactivate = null
                )
            }
        }

        /*************** BottomSheet para editar impresora ***************/
        selectedPrinterForEdit?.let { profile ->
            ModalBottomSheet(
                onDismissRequest = {
                    selectedPrinterForEdit = null
                },
                sheetState = printerSheetState,
                containerColor = PikiPaper,
                tonalElevation = 0.dp,
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                dragHandle = {
                    SheetDragHandle()
                }
            ) {
                PrinterEditSheet(
                    title = "Editar impresora",
                    profile = profile,
                    onClose = {
                        selectedPrinterForEdit = null
                    },
                    onSave = { name, price, lifespanHours, powerKw ->
                        viewModel.updatePrinterProfile(
                            profile.copy(
                                name = name,
                                price = price,
                                lifespanHours = lifespanHours,
                                powerKw = powerKw
                            )
                        )

                        selectedPrinterForEdit = null
                        savedFeedbackVisible = true
                    },
                    onDeactivate = {
                        viewModel.deactivatePrinterProfile(profile.id)
                        selectedPrinterForEdit = null
                        savedFeedbackVisible = true
                    }
                )
            }
        }
    }
}

/*************** Header de pantalla ***************/
@Composable
private fun SettingsHeader() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = PikiSettingsText
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Configurá los costos base y la lógica de precios de tu taller.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = PikiSettingsText.copy(alpha = 0.70f),
            lineHeight = 20.sp
        )
    }
}

/*************** Costos base ***************/
@Composable
private fun BaseCostsCard(
    energyRate: String,
    onEnergyRateChange: (String) -> Unit,
    margin: String,
    onMarginChange: (String) -> Unit,
    friendDiscount: String,
    onFriendDiscountChange: (String) -> Unit
) {
    BentoCard(
        title = "Costos base",
        icon = Icons.Outlined.AttachMoney,
        background = PikiSky,
        titleColor = PikiSettingsText
    ) {
        BubbleNumberField(
            label = "Energía ($/kWh)",
            value = energyRate,
            onValueChange = onEnergyRateChange
        )

        BubbleNumberField(
            label = "Ganancia / margen (%)",
            value = margin,
            onValueChange = onMarginChange
        )

        BubbleNumberField(
            label = "Descuento amigo (%)",
            value = friendDiscount,
            onValueChange = onFriendDiscountChange
        )
    }
}

/*************** Filamentos ***************/
@Composable
private fun FilamentsCard(
    filamentInputs: Map<FilamentType, MutableState<String>>
) {
    BentoCard(
        title = "Filamentos",
        icon = Icons.Outlined.Inventory2,
        background = PikiLeaf,
        titleColor = PikiSettingsText
    ) {
        FilamentType.entries.forEach { type ->
            FilamentPriceRow(
                name = type.label,
                value = filamentInputs[type]?.value ?: "",
                onValueChange = {
                    filamentInputs[type]?.value = it
                }
            )
        }
    }
}

/*************** Flota de impresoras dinámica ***************/
@Composable
private fun PrinterFleetCard(
    profiles: List<PrinterProfile>,
    onAddPrinter: () -> Unit,
    onEditPrinter: (PrinterProfile) -> Unit,
    onDeactivatePrinter: (PrinterProfile) -> Unit,
    onReactivatePrinter: (PrinterProfile) -> Unit
) {
    BentoCard(
        title = "Flota de impresoras",
        icon = Icons.Outlined.LocalPrintshop,
        background = PikiAccentPurple,
        titleColor = PikiSettingsText
    ) {
        Button(
            onClick = onAddPrinter,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PikiWhite,
                contentColor = PikiSettingsText
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Agregar impresora",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }

        profiles
            .sortedWith(
                compareByDescending<PrinterProfile> { it.isActive }
                    .thenBy { it.name.lowercase() }
            )
            .forEach { profile ->
                PrinterInnerCard(
                    profile = profile,
                    onEdit = {
                        onEditPrinter(profile)
                    },
                    onDeactivate = {
                        onDeactivatePrinter(profile)
                    },
                    onReactivate = {
                        onReactivatePrinter(profile)
                    }
                )
            }
    }
}

/*************** Card interna de impresora dinámica ***************/
@Composable
private fun PrinterInnerCard(
    profile: PrinterProfile,
    onEdit: () -> Unit,
    onDeactivate: () -> Unit,
    onReactivate: () -> Unit
) {
    val activeAlpha =
        if (profile.isActive) 1f else 0.52f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = PikiWhite.copy(
                alpha = if (profile.isActive) 0.78f else 0.45f
            )
        ),
        border = BorderStroke(
            width = 3.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = profile.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiSettingsText.copy(alpha = activeAlpha)
                )

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (profile.isActive) {
                        PikiAccentPurple.copy(alpha = 0.55f)
                    } else {
                        PikiWhite.copy(alpha = 0.48f)
                    }
                ) {
                    Text(
                        text = if (profile.isActive) {
                            "ACTIVA"
                        } else {
                            "INACTIVA"
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiSettingsText.copy(
                            alpha = if (profile.isActive) 1f else 0.55f
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PikiWhite)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar impresora",
                        tint = PikiSettingsText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (profile.isActive) {
                            onDeactivate()
                        } else {
                            onReactivate()
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PikiWhite)
                ) {
                    Icon(
                        imageVector = if (profile.isActive) {
                            Icons.Outlined.Delete
                        } else {
                            Icons.Outlined.CheckCircle
                        },
                        contentDescription = if (profile.isActive) {
                            "Desactivar impresora"
                        } else {
                            "Reactivar impresora"
                        },
                        tint = if (profile.isActive) {
                            PikiDangerText
                        } else {
                            PikiSettingsText
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            PrinterSummaryLine(
                icon = Icons.Outlined.Payments,
                label = "Precio",
                value = "$ ${"%.2f".format(profile.price)}"
            )

            PrinterSummaryLine(
                icon = Icons.Outlined.Timer,
                label = "Vida útil",
                value = "${profile.lifespanHours} h"
            )

            PrinterSummaryLine(
                icon = Icons.Outlined.Bolt,
                label = "Consumo",
                value = "${profile.powerKw} kW"
            )
        }
    }
}

/*************** Línea resumen de impresora ***************/
@Composable
private fun PrinterSummaryLine(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PikiSettingsText.copy(alpha = 0.62f),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = PikiSettingsText.copy(alpha = 0.62f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.ExtraBold,
            color = PikiSettingsText
        )
    }
}

/*************** Complejidad de diseño ***************/
@Composable
private fun DesignComplexityCard(
    designCostInputs: Map<DesignType, MutableState<String>>
) {
    BentoCard(
        title = "Complejidad de diseño",
        icon = Icons.Outlined.Palette,
        background = PikiClay,
        titleColor = PikiWhite
    ) {
        DesignType.entries.forEach { designType ->
            DesignCostRow(
                designType = designType,
                value = designCostInputs[designType]?.value ?: "",
                onValueChange = {
                    designCostInputs[designType]?.value = it
                }
            )
        }
    }
}

/*************** Descuentos por cantidad ***************/
@Composable
private fun VolumeDiscountsCard(
    quantityDiscountInputs: Map<Int, MutableState<String>>
) {
    BentoCard(
        title = "Descuentos por cantidad",
        icon = Icons.Outlined.Percent,
        background = PikiAccentPink,
        titleColor = PikiSettingsText
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DiscountBubble(
                    modifier = Modifier.weight(1f),
                    quantity = 25,
                    value = quantityDiscountInputs[25]?.value ?: "",
                    onValueChange = {
                        quantityDiscountInputs[25]?.value = it
                    }
                )

                DiscountBubble(
                    modifier = Modifier.weight(1f),
                    quantity = 50,
                    value = quantityDiscountInputs[50]?.value ?: "",
                    onValueChange = {
                        quantityDiscountInputs[50]?.value = it
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DiscountBubble(
                    modifier = Modifier.weight(1f),
                    quantity = 75,
                    value = quantityDiscountInputs[75]?.value ?: "",
                    onValueChange = {
                        quantityDiscountInputs[75]?.value = it
                    }
                )

                DiscountBubble(
                    modifier = Modifier.weight(1f),
                    quantity = 100,
                    value = quantityDiscountInputs[100]?.value ?: "",
                    onValueChange = {
                        quantityDiscountInputs[100]?.value = it
                    }
                )
            }
        }
    }
}

/*************** Card bento de ajustes ***************/
@Composable
private fun BentoCard(
    title: String,
    icon: ImageVector,
    background: Color,
    titleColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .kawaiiShadow(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        border = BorderStroke(
            width = 4.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PikiWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PikiSettingsText,
                        modifier = Modifier.size(27.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor
                )
            }

            content()
        }
    }
}

/*************** BottomSheet para agregar/editar impresora ***************/
@Composable
private fun PrinterEditSheet(
    title: String,
    profile: PrinterProfile?,
    onClose: () -> Unit,
    onSave: (
        name: String,
        price: Double,
        lifespanHours: Int,
        powerKw: Double
    ) -> Unit,
    onDeactivate: (() -> Unit)?
) {
    var name by remember(profile?.id) {
        mutableStateOf(profile?.name ?: "")
    }

    var price by remember(profile?.id) {
        mutableStateOf(profile?.price?.toString() ?: "")
    }

    var lifespan by remember(profile?.id) {
        mutableStateOf(profile?.lifespanHours?.toString() ?: "")
    }

    var power by remember(profile?.id) {
        mutableStateOf(profile?.powerKw?.toString() ?: "")
    }

    val canSave =
        name.isNotBlank() &&
                price.toDoubleOrNull() != null &&
                lifespan.toIntOrNull() != null &&
                power.toDoubleOrNull() != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PikiAccentPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalPrintshop,
                    contentDescription = null,
                    tint = PikiSettingsText,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiSettingsText
                )

                Text(
                    text = if (profile == null) {
                        "Nueva máquina del taller"
                    } else if (profile.isActive) {
                        "Impresora activa"
                    } else {
                        "Impresora inactiva"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = PikiSettingsText.copy(alpha = 0.66f)
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(PikiWhite)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Cerrar",
                    tint = PikiSettingsText,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

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
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SimpleTextInput(
                    label = "Nombre de la impresora",
                    value = name,
                    onValueChange = {
                        name = it
                    }
                )

                BubbleNumberField(
                    label = "Precio de la impresora ($)",
                    value = price,
                    onValueChange = {
                        price = it
                    }
                )

                BubbleNumberField(
                    label = "Vida útil estimada (horas)",
                    value = lifespan,
                    onValueChange = {
                        lifespan = it
                    }
                )

                BubbleNumberField(
                    label = "Consumo eléctrico (kW)",
                    value = power,
                    onValueChange = {
                        power = it
                    }
                )
            }
        }

        Button(
            onClick = {
                onSave(
                    name.trim(),
                    price.toDoubleOrNull() ?: 0.0,
                    lifespan.toIntOrNull() ?: 0,
                    power.toDoubleOrNull() ?: 0.0
                )
            },
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PikiSky,
                contentColor = PikiSettingsText,
                disabledContainerColor = PikiWhite.copy(alpha = 0.50f),
                disabledContentColor = PikiSettingsText.copy(alpha = 0.40f)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Guardar impresora",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }

        if (onDeactivate != null && profile?.isActive == true) {
            Button(
                onClick = onDeactivate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PikiDangerBg,
                    contentColor = PikiDangerText
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Desactivar impresora",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

/*************** Campo tipo burbuja ***************/
@Composable
private fun BubbleNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(start = 10.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiSettingsText.copy(alpha = 0.82f)
        )

        CompactNumberInput(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            textAlign = TextAlign.Start
        )
    }
}

/*************** Fila de filamento ***************/
@Composable
private fun FilamentPriceRow(
    name: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = PikiWhite.copy(alpha = 0.40f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiSettingsText
            )

            CompactNumberInput(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .width(108.dp)
                    .height(40.dp),
                prefix = "$",
                textAlign = TextAlign.End
            )
        }
    }
}

/*************** Fila de costo de diseño ***************/
@Composable
private fun DesignCostRow(
    designType: DesignType,
    value: String,
    onValueChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(999.dp),
        color = PikiWhite.copy(alpha = 0.20f),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite.copy(alpha = 0.42f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(PikiWhite),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = designType.label.take(1).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiClay
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = designType.label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PikiWhite
            )

            CompactNumberInput(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .width(106.dp)
                    .height(40.dp),
                prefix = "$",
                textAlign = TextAlign.End
            )
        }
    }
}

/*************** Burbuja de descuento ***************/
@Composable
private fun DiscountBubble(
    modifier: Modifier = Modifier,
    quantity: Int,
    value: String,
    onValueChange: (String) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = PikiWhite.copy(alpha = 0.56f),
        border = BorderStroke(
            width = 2.dp,
            color = PikiWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "$quantity+ unidades",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PikiSettingsText.copy(alpha = 0.62f),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompactNumberInput(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    suffix = "%",
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "OFF",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PikiSettingsText.copy(alpha = 0.78f)
                )
            }
        }
    }
}

/*************** Botón guardar ***************/
@Composable
private fun SaveSettingsButton(
    modifier: Modifier = Modifier,
    saved: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(PikiButtonShadowBrown)
        )

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .border(
                    width = 3.dp,
                    color = PikiButtonShadowBrown,
                    shape = RoundedCornerShape(999.dp)
                ),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (saved) {
                    PikiLeaf
                } else {
                    PikiSky
                },
                contentColor = PikiSettingsText
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(
                imageVector = if (saved) {
                    Icons.Outlined.CheckCircle
                } else {
                    Icons.Outlined.Save
                },
                contentDescription = null,
                modifier = Modifier.size(21.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (saved) {
                    "Cambios guardados"
                } else {
                    "Guardar cambios"
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

/*************** Input compacto para números ***************/
/*
    Se usa en campos chicos donde OutlinedTextField queda demasiado alto
    o no deja ver bien los números.
*/
@Composable
private fun CompactNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    suffix: String? = null,
    textAlign: TextAlign = TextAlign.End
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = PikiSettingsText,
            fontWeight = FontWeight.ExtraBold,
            textAlign = textAlign
        ),
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(PikiWhite.copy(alpha = 0.90f))
            .padding(horizontal = 14.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (prefix != null) {
                    Text(
                        text = prefix,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiSettingsText.copy(alpha = 0.70f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = when (textAlign) {
                        TextAlign.Center -> Alignment.Center
                        TextAlign.Start -> Alignment.CenterStart
                        else -> Alignment.CenterEnd
                    }
                ) {
                    innerTextField()
                }

                if (suffix != null) {
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = suffix,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PikiSettingsText.copy(alpha = 0.70f)
                    )
                }
            }
        }
    )
}

/*************** Drag handle del BottomSheet ***************/
@Composable
private fun SheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 4.dp)
            .width(56.dp)
            .height(6.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(PikiCreamLine)
    )
}

/*************** Input simple para texto ***************/
@Composable
private fun SimpleTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            modifier = Modifier.padding(start = 10.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PikiSettingsText.copy(alpha = 0.82f)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = PikiSettingsText,
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(PikiWhite.copy(alpha = 0.90f))
                .padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    innerTextField()
                }
            }
        )
    }
}