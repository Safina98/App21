package com.example.app21try6.statement.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app21try6.database.tables.CustomerTable

@Composable
fun UpsertCostumerDialog(
    item: CustomerTable,
    onConfirm: (CustomerTable) -> Unit,
    onDismiss: () -> Unit
) {
    var customerName by remember { mutableStateOf(item.customerName) }
    var businessName by remember { mutableStateOf(item.customerBussinessName) }
    var address by remember { mutableStateOf(item.customerAddress) }
    var location by remember { mutableStateOf(item.customerLocation) }
    var phoneNumber by remember { mutableStateOf(item.customerPhoneNumber) }



    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Name") }
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") }
                )
                OutlinedTextField(
                    value = location?:"",
                    onValueChange = { location = it },
                    label = { Text("Lokasi") }
                )
                OutlinedTextField(
                    value = phoneNumber?:"",
                    onValueChange = { phoneNumber = it },
                    label = { Text("No telp") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    item.copy(
                        customerName = customerName,
                        customerBussinessName = businessName,
                        customerAddress = address,
                        customerPhoneNumber = phoneNumber
                    )
                )
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}