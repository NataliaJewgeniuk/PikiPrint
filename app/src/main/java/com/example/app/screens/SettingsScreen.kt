package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.models.FilamentType
import com.example.app.viewmodels.AppViewModel

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    val settings = state.quoteSettings

    var energyRate by remember { mutableStateOf(settings.energyRate.toString()) }
    var margin by remember { mutableStateOf(settings.marginPercentage.toString()) }
    var plaCost by remember { mutableStateOf(settings.filamentCost[FilamentType.PLA].toString()) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Ajustes", style = MaterialTheme.typography.headlineMedium) }
        item { OutlinedTextField(value = plaCost, onValueChange = { plaCost = it }, label = { Text("Costo PLA ($)") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = energyRate, onValueChange = { energyRate = it }, label = { Text("Energía kWh ($)") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value = margin, onValueChange = { margin = it }, label = { Text("Margen (%)") }, modifier = Modifier.fillMaxWidth()) }
        item {
            Button(onClick = {
                val newSet = settings.copy(energyRate = energyRate.toDoubleOrNull() ?: 0.15, marginPercentage = margin.toDoubleOrNull() ?: 50.0)
                newSet.filamentCost[FilamentType.PLA] = plaCost.toDoubleOrNull() ?: 22.0
                viewModel.updateSettings(newSet)
            }, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Text("Guardar Ajustes")
            }
        }
    }
}