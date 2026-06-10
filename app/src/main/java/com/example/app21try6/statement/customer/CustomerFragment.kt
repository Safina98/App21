package com.example.app21try6.statement.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.statement.StatementHSViewModel
import com.example.app21try6.statement.StatementHSViewModelFactory

class CustomerFragment: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application= requireNotNull(this.activity).application
        val discountRepo= DiscountRepository(application)
        val viewModelFactory = StatementHSViewModelFactory(application,discountRepo)
        val viewModel = ViewModelProvider(this,viewModelFactory).get(StatementHSViewModel::class.java)

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    CustomerScreen(viewModel)
                }
            }
        }
    }
}