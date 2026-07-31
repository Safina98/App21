    package com.example.app21try6

    import android.content.Context
    import android.os.Bundle
    import android.util.Log
    import android.view.KeyEvent
    import android.view.inputmethod.InputMethodManager
    import androidx.activity.OnBackPressedCallback
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.WindowCompat
    import androidx.databinding.DataBindingUtil
    import androidx.drawerlayout.widget.DrawerLayout
    import androidx.navigation.findNavController
    import androidx.navigation.fragment.NavHostFragment
    import androidx.navigation.ui.AppBarConfiguration
    import androidx.navigation.ui.NavigationUI
    import com.example.app21try6.databinding.ActivityMainBinding
    import com.example.app21try6.stock.upsertproduk.InputUpdateProduct
    import com.example.app21try6.stock.brandstock.BrandStockFragment
    import com.example.app21try6.stock.subproductstock.SubProductStockFragment
    import com.example.app21try6.transaction.transactionedit.TransactionEditFragment
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.core.view.updatePadding

    class MainActivity : AppCompatActivity() {
        private lateinit var drawerLayout: DrawerLayout
        private lateinit var appBarConfiguration: AppBarConfiguration

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            @Suppress("UNUSED_VARIABLE")
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
            val toolbar:androidx.appcompat.widget.Toolbar = binding.toolbar
           drawerLayout = binding.drawerLayout
            setSupportActionBar(toolbar)

            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // push toolbar down below the status bar
                binding.toolbar.updatePadding(top = systemBars.top)

                // push the nav host fragment content up above the nav bar
                binding.myNavHostFragment.updatePadding(bottom = systemBars.bottom)

                // IMPORTANT: do not consume — let insets keep propagating to child fragments
                // (needed for your keyboard-close detection in the Fragment)
                insets
            }
           // val db = VendibleDatabase.getInstance(this)
            //RealtimeDatabaseSync.startSyncAllTables(db.brandDao, db.categoryDao)

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
            if (currentFragment is InputUpdateProduct) {
                if (currentFragment.handleBackPress()) return true
            }

            val navController = findNavController(R.id.myNavHostFragment)
            return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
        }
        override fun onResume() {
            super.onResume()

            // Call the sync function here, passing the activity context (or better, the application context)
            // WorkManager will ignore the request if a sync with the same unique name is already running/queued.
          //  scheduleImmediateSync(applicationContext)
        }
        var activeDropdownBackHandler: (() -> Boolean)? = null

        override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            Log.i("Autocompleteprobs","dispatchKeyEven")
            if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val focused = currentFocus
                Log.i("Autocompleteprobs","event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP")
                // Stage 1: keyboard is up -> hide it, consume, stop here
                if (focused != null && imm.hideSoftInputFromWindow(focused.windowToken, 0)) {
                    Log.i("Autocompleteprobs","focused != null && imm.hideSoftInputFromWindow(focused.windowToken, 0)")
                    return true
                }

                // Stage 2: keyboard already down -> ask the visible tab if it has a dropdown open
                if (activeDropdownBackHandler?.invoke() == true) {
                    Log.i("Autocompleteprobs","activeDropdownBackHandler?.invoke() == true")
                    return true
                }
            }
            return super.dispatchKeyEvent(event)
        }

    }