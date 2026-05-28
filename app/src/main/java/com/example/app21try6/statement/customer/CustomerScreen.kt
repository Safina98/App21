package com.example.app21try6.statement.customer

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.DraggableFloatingActionButton
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.statement.CustomerAdapter
import com.example.app21try6.statement.CustomerDelListener
import com.example.app21try6.statement.CustomerListener
import com.example.app21try6.statement.CustomerLongListener
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.utils.DialogUtils
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen (
    hsViewModel: StatementHSViewModel
){
    val context = LocalContext.current


    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<CustomerTable?>(null) }
    val list= hsViewModel.filtered.collectAsState(listOf())
    var query by hsViewModel.searchQuery

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
                hsViewModel,
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
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar(
                query = query,
                onQueryChange = { newvalue->hsViewModel.onSearchQueryChange(newvalue) }
            )
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
        DraggableFloatingActionButtonCompose(
            onClick = { showUpdateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
    if (showUpdateDialog) {
        UpsertCostumerDialog (
            item = selectedItem,
            onConfirm = {
                hsViewModel.updateCustomer(it)
                showUpdateDialog = false
                selectedItem =null
            },
            onDismiss = {
                showUpdateDialog = false
                selectedItem =null
            }
        )
    }

}