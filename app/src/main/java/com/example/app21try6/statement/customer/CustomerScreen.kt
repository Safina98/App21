package com.example.app21try6.statement.customer

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.statement.CustomerAdapter
import com.example.app21try6.statement.CustomerDelListener
import com.example.app21try6.statement.CustomerListener
import com.example.app21try6.statement.CustomerLongListener
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.utils.DialogUtils

@Composable
fun CustomerScreen (
    viewModel: StatementHSViewModel
){
    val context = LocalContext.current
    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<CustomerTable?>(null) }
    val list= viewModel.allCustomer.collectAsState(listOf())
    val adapter = remember {  CustomerAdapter(
        CustomerListener {
            //showCustomerDialog(it)
            Log.i("Customer Screen","updated")
            selectedItem = it
            showUpdateDialog = true
        },
        CustomerLongListener {

        },
        CustomerDelListener {
            Log.i("Customer Screen","deleted")
            DialogUtils.showDeleteDialog(
                context,

                viewModel,
                it,
                { vm, item ->
                    (vm as StatementHSViewModel).deleteCustomerTable(item as CustomerTable)
                }
            )
           // DialogUtils.showDeleteDialog(requireContext(),this, viewModel, it, { vm, item -> (vm as StatementHSViewModel).deleteCustomerTable(item as CustomerTable) })
        }
    ) }


    LaunchedEffect(list.value) {
        adapter.submitList(list.value)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                RecyclerView(context).apply {
                    this.adapter = adapter
                    layoutManager = LinearLayoutManager(context)
                }
            }
        )
    }
    if (showUpdateDialog && selectedItem != null) {
        UpsertCostumerDialog (
            item = selectedItem!!,
            onConfirm = {
                viewModel.updateCustomer(it)
                showUpdateDialog = false
            },
            onDismiss = { showUpdateDialog = false }
        )
    }

}