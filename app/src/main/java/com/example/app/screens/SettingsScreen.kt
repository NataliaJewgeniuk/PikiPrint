package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app.models.DesignType
import com.example.app.models.FilamentType
import com.example.app.models.PrinterSettings
import com.example.app.models.PrinterType
import com.example.app.models.QuoteSettings
import com.example.app.viewmodels.AppViewModel

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

    /*************** Estados de impresoras ***************/
    val printerPriceInputs = remember(settings) {
        PrinterType.entries.associateWith { printer ->
            mutableStateOf(
                (settings.printers[printer]?.price ?: 0.0).toString()
            )
        }
    }

    val printerLifespanInputs = remember(settings) {
        PrinterType.entries.associateWith { printer ->
            mutableStateOf(
                (settings.printers[printer]?.lifespanHours ?: 0).toString()
            )
        }
    }

    val printerPowerInputs = remember(settings) {
        PrinterType.entries.associateWith { printer ->
            mutableStateOf(
                (settings.printers[printer]?.powerKw ?: 0.0).toString()
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

    /*************** Guardar cambios ***************/
    fun guardarCambios() {
        val updatedFilamentCost = mutableMapOf<FilamentType, Double>()

        FilamentType.entries.forEach { type ->
            updatedFilamentCost[type] =
                filamentInputs[type]?.value?.toDoubleOrNull()
                    ?: settings.filamentCost[type]
                            ?: 0.0
        }

        val updatedPrinters = mutableMapOf<PrinterType, PrinterSettings>()

        PrinterType.entries.forEach { printer ->
            updatedPrinters[printer] = PrinterSettings(
                price =
                    printerPriceInputs[printer]?.value?.toDoubleOrNull()
                        ?: settings.printers[printer]?.price
                        ?: 0.0,

                lifespanHours =
                    printerLifespanInputs[printer]?.value?.toIntOrNull()
                        ?: settings.printers[printer]?.lifespanHours
                        ?: 0,

                powerKw =
                    printerPowerInputs[printer]?.value?.toDoubleOrNull()
                        ?: settings.printers[printer]?.powerKw
                        ?: 0.0
            )
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

        val updatedSettings = QuoteSettings(
            filamentCost = updatedFilamentCost,
            energyRate = energyRate.toDoubleOrNull() ?: settings.energyRate,
            printers = updatedPrinters,
            marginPercentage = margin.toDoubleOrNull() ?: settings.marginPercentage,
            quantityDiscounts = updatedQuantityDiscounts,
            friendDiscountPercentage =
                friendDiscount.toDoubleOrNull()
                    ?: settings.friendDiscountPercentage,
            designCosts = updatedDesignCosts
        )

        viewModel.updateSettings(updatedSettings)
    }

    /*************** Pantalla ***************/
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 88.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "⚙️ Parámetros de cálculo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            /*************** Costos de filamento ***************/
            item {
                SectionCard(
                    title = "🧵 Costo de filamento",
                    subtitle = "Precio por kg de cada material."
                ) {
                    FilamentType.entries.forEach { type ->
                        CampoNumero(
                            label = "${type.label} ($/kg)",
                            value = filamentInputs[type]?.value ?: "",
                            onValueChange = {
                                filamentInputs[type]?.value = it
                            }
                        )
                    }
                }
            }

            /*************** Energía y margen ***************/
            item {
                SectionCard(
                    title = "⚡ Energía y margen",
                    subtitle = "Valores generales usados por todos los presupuestos."
                ) {
                    CampoNumero(
                        label = "Precio kWh",
                        value = energyRate,
                        onValueChange = {
                            energyRate = it
                        }
                    )

                    CampoNumero(
                        label = "Margen de ganancia (%)",
                        value = margin,
                        onValueChange = {
                            margin = it
                        }
                    )

                    CampoNumero(
                        label = "Descuento amigo (%)",
                        value = friendDiscount,
                        onValueChange = {
                            friendDiscount = it
                        }
                    )
                }
            }

            /*************** Impresoras ***************/
            item {
                SectionCard(
                    title = "🖨️ Impresoras",
                    subtitle = "Precio, vida útil y consumo para calcular desgaste y electricidad."
                ) {
                    PrinterType.entries.forEach { printer ->
                        Text(
                            text = printer.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        CampoNumero(
                            label = "Precio de la impresora",
                            value = printerPriceInputs[printer]?.value ?: "",
                            onValueChange = {
                                printerPriceInputs[printer]?.value = it
                            }
                        )

                        CampoNumero(
                            label = "Vida útil estimada (horas)",
                            value = printerLifespanInputs[printer]?.value ?: "",
                            onValueChange = {
                                printerLifespanInputs[printer]?.value = it
                            }
                        )

                        CampoNumero(
                            label = "Consumo eléctrico (kW)",
                            value = printerPowerInputs[printer]?.value ?: "",
                            onValueChange = {
                                printerPowerInputs[printer]?.value = it
                            }
                        )

                        if (printer != PrinterType.entries.last()) {
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            /*************** Descuentos por cantidad ***************/
            item {
                SectionCard(
                    title = "🏷️ Descuentos por cantidad",
                    subtitle = "Porcentaje aplicado según cantidad de unidades."
                ) {
                    listOf(25, 50, 75, 100).forEach { quantity ->
                        CampoNumero(
                            label = "+$quantity unidades (%)",
                            value = quantityDiscountInputs[quantity]?.value ?: "",
                            onValueChange = {
                                quantityDiscountInputs[quantity]?.value = it
                            }
                        )
                    }
                }
            }

            /*************** Costos de diseño ***************/
            item {
                SectionCard(
                    title = "✏️ Costos de diseño",
                    subtitle = "Importe fijo que se suma una sola vez al pedido."
                ) {
                    DesignType.entries.forEach { designType ->
                        CampoNumero(
                            label = designType.label,
                            value = designCostInputs[designType]?.value ?: "",
                            onValueChange = {
                                designCostInputs[designType]?.value = it
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        /*************** Botón fijo guardar ***************/
        Button(
            onClick = {
                guardarCambios()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Guardar Ajustes ✨")
        }
    }
}

/*************** Tarjeta de sección ***************/
/*
    Agrupa parámetros relacionados para que la pantalla no quede
    como una lista enorme sin estructura.
*/
@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            content()
        }
    }
}

/*************** Campo numérico reutilizable ***************/
/*
    Se usa para precios, porcentajes, horas y consumos.
*/
@Composable
private fun CampoNumero(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        )
    )
}