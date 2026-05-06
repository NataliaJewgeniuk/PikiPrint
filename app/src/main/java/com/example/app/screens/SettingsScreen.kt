package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.app.models.*
import com.example.app.viewmodels.AppViewModel

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    val settings = state.quoteSettings

    var energyRate by remember { mutableStateOf(settings.energyRate.toString()) }
    var margin by remember { mutableStateOf(settings.marginPercentage.toString()) }
    var friend by remember { mutableStateOf(settings.friendDiscountPercentage.toString()) }

    val filamentInputs = remember { FilamentType.entries.associateWith { mutableStateOf((settings.filamentCost[it] ?: 0.0).toString()) } }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("⚙️ Configuración", style = MaterialTheme.typography.headlineSmall) }
        item { OutlinedTextField(energyRate, { energyRate = it }, label = { Text("Precio kWh") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { OutlinedTextField(margin, { margin = it }, label = { Text("Margen (%)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { OutlinedTextField(friend, { friend = it }, label = { Text("Descuento Amigo (%)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        item { Text("Filamentos (precio por Kg)") }
        FilamentType.entries.forEach { ft ->
            item { OutlinedTextField(filamentInputs[ft]!!.value, { filamentInputs[ft]!!.value = it }, label = { Text(ft.name) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) }
        }
        item {
            Button(onClick = {
                val updated = settings.copy(
                    energyRate = energyRate.toDoubleOrNull() ?: settings.energyRate,
                    marginPercentage = margin.toDoubleOrNull() ?: settings.marginPercentage,
                    friendDiscountPercentage = friend.toDoubleOrNull() ?: settings.friendDiscountPercentage
                )
                FilamentType.entries.forEach { updated.filamentCost[it] = filamentInputs[it]!!.value.toDoubleOrNull() ?: updated.filamentCost[it]!! }
                viewModel.updateSettings(updated)
            }, modifier = Modifier.fillMaxWidth()) { Text("Guardar Ajustes ✨") }
        }
    }
}
