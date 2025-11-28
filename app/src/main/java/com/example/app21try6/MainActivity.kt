package com.example.app21try6

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.cloud.RealtimeDatabaseSync
import com.example.app21try6.databinding.ActivityMainBinding
import com.example.app21try6.stock.brandstock.BrandStockFragment
import com.example.app21try6.stock.subproductstock.SubProductStockFragment
import com.example.app21try6.transaction.transactionedit.TransactionEditFragment

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        val toolbar:androidx.appcompat.widget.Toolbar = binding.toolbar
       drawerLayout = binding.drawerLayout
        setSupportActionBar(toolbar)
        val db = VendibleDatabase.getInstance(this)
        RealtimeDatabaseSync.startListening()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                if (currentFragment is SubProductStockFragment) {
                    if (currentFragment.handleBackPress()) {
                        return // If handled, don't navigate back
                    }
                }
                isEnabled = false // Disable callback to allow default back behavior
                onBackPressedDispatcher.onBackPressed() // Perform default back navigation
            }
        })

    }
    /*
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

     */
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment

        if (currentFragment is SubProductStockFragment ) {
            if (currentFragment.handleBackPress()) {
                return true // Stay in fragment
            }
        }
        if (currentFragment is BrandStockFragment ) {
            if (currentFragment.handleBackPress()) {
                return true // Stay in fragment
            }
        }
        if (currentFragment is TransactionEditFragment ) {
            if (currentFragment.handleBackPress()) {
                return true // Stay in fragment
            }
        }

        val navController = findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

}