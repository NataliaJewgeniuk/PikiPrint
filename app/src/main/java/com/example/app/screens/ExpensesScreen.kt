package com.example.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.models.Expense
import com.example.app.models.ExpenseCategory
import com.example.app.viewmodels.AppViewModel
import java.util.UUID

@Composable
fun ExpensesScreen(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, "Agregar")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Gastos", style = MaterialTheme.typography.headlineMedium)
            val total = state.expenses.sumOf { it.amount }
            Text("Total Gastado: $${total}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(state.expenses) { exp ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(exp.title, style = MaterialTheme.typography.titleMedium)
                                Text(exp.category.name)
                            }
                            Text("-$${exp.amount}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo Gasto") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Descripción") })
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addExpense(Expense(UUID.randomUUID().toString(), title, amount.toDoubleOrNull() ?: 0.0, "", ExpenseCategory.OTHER))
                    showDialog = false
                    title = ""
                    amount = ""
                }) { Text("Agregar") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}