package com.example.app.screens

import androidx.compose.foundation.background
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
import com.example.app.models.*
import com.example.app.utils.Calculator
import com.example.app.utils.QuoteBreakdown
import com.example.app.viewmodels.AppViewModel
import java.time.LocalDate
import java.util.UUID

@Composable
fun QuotesScreen(viewModel: AppViewModel, onGoToOrders: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val editingOrder by viewModel.editingOrder.collectAsState()

    var title by remember(editingOrder) { mutableStateOf(editingOrder?.title ?: "") }
    var clientName by remember(editingOrder) { mutableStateOf(editingOrder?.clientName ?: "") }
    var printHours by remember(editingOrder) { mutableStateOf(editingOrder?.printTimeHours?.toString() ?: "2") }
    var printMinutes by remember(editingOrder) { mutableStateOf(editingOrder?.printTimeMinutes?.toString() ?: "30") }
    var quantity by remember(editingOrder) { mutableStateOf(editingOrder?.quantity?.toString() ?: "1") }
    var weight by remember(editingOrder) { mutableStateOf(editingOrder?.weightGramsPerUnit?.toString() ?: "100") }
    var color by remember(editingOrder) { mutableStateOf(editingOrder?.color ?: "Blanco") }
    var selectedFilament by remember(editingOrder) { mutableStateOf(editingOrder?.filament ?: FilamentType.PLA) }
    var selectedPrinter by remember(editingOrder) { mutableStateOf(editingOrder?.printer ?: PrinterType.A1_COMBO) }
    var selectedDesign by remember(editingOrder) { mutableStateOf(editingOrder?.designType ?: DesignType.BASIC) }
    var isFriend by remember(editingOrder) { mutableStateOf(editingOrder?.isFriend ?: false) }

    var breakdown by remember { mutableStateOf<QuoteBreakdown?>(null) }
    var currentOrderData by remember { mutableStateOf<Order?>(null) }

    if (breakdown != null) {
        val b = breakdown!!
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Card(shape = RoundedCornerShape(32.dp), modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(listOf(Color(0xFFB584E8), Color(0xFFFF8BA7), Color(0xFFFFB347))))
                        .padding(16.dp)
                ) { Text("✨ Presupuesto Final", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(16.dp))
            Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F7))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Total: ARS ${"%.2f".format(b.totalFinal)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text("Material: ARS ${"%.2f".format(b.costoMaterial)}")
                    Text("Energía: ARS ${"%.2f".format(b.costoEnergia)}")
                    Text("Desgaste: ARS ${"%.2f".format(b.costoDegradamiento)}")
                    Text("Diseño: ARS ${"%.2f".format(b.costoDiseno)}")
                    Text("Subtotal: ARS ${"%.2f".format(b.subtotal)}")
                    Text("Margen: ARS ${"%.2f".format(b.margenMonto)}")
                    Text("Desc. cantidad: -ARS ${"%.2f".format(b.descuentoCantidad)}")
                    Text("Desc. amigo: -ARS ${"%.2f".format(b.descuentoAmigo)}")
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                currentOrderData?.let { if (editingOrder == null) viewModel.addOrder(it) else viewModel.updateOrder(it) }
                viewModel.setEditingOrder(null)
                onGoToOrders()
            }, modifier = Modifier.fillMaxWidth()) { Text("Guardar Pedido 🌸") }
            OutlinedButton(onClick = { breakdown = null }, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("🌸 Calculadora PikiPrint 3D", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item { OutlinedTextField(title, { title = it }, label = { Text("Título/Proyecto") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { OutlinedTextField(clientName, { clientName = it }, label = { Text("Cliente") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { OutlinedTextField(color, { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { OutlinedTextField(printHours, { printHours = it }, label = { Text("Horas") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { OutlinedTextField(printMinutes, { printMinutes = it }, label = { Text("Minutos") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { OutlinedTextField(weight, { weight = it }, label = { Text("Peso por unidad (g)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { OutlinedTextField(quantity, { quantity = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { DropdownEnum("Material", selectedFilament) { selectedFilament = it } }
        item { DropdownEnum("Impresora", selectedPrinter) { selectedPrinter = it } }
        item { DropdownEnum("Diseño", selectedDesign) { selectedDesign = it } }
        item { Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) { Checkbox(checked = isFriend, onCheckedChange = { isFriend = it }); Text("Aplicar Descuento Amigo") } }
        item {
            Button(onClick = {
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
                    createdAt = LocalDate.now().toString()
                )
                val res = Calculator.calculateQuote(order, state.quoteSettings)
                order.totalComputed = res.totalFinal
                currentOrderData = order
                breakdown = res
            }, modifier = Modifier.fillMaxWidth()) { Text("Calcular ✨") }
        }
    }
}

@Composable
private inline fun <reified T : Enum<T>> DropdownEnum(label: String, selected: T, crossinline onSelect: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(selected.name, {}, readOnly = true, label = { Text(label) }, modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(20.dp))
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            enumValues<T>().forEach { opt -> DropdownMenuItem(text = { Text(opt.name) }, onClick = { onSelect(opt); expanded = false }) }
        }
    }
}
