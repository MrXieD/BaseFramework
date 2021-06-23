package com.example.contactroom

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.contactroom.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initBottomNavigate()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolBar)
    }

    /**
     * 使用 FragmentContainerView 创建 NavHostFragment，
     * 或通过 FragmentTransaction 手动将 NavHostFragment 添加到您的 Activity 时，
     * 尝试通过 Navigation.findNavController(Activity, @IdRes int) 检索 Activity 的 onCreate() 中的 NavController 将失败。
     * 您应改为直接从 NavHostFragment 检索 NavController。
     */
    private fun initBottomNavigate() {
        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.e(TAG, "OnDestinationChangedListener:$destination,$arguments ")
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_call, R.id.navigation_contact, R.id.navigation_collect
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController);
//        navView.setupWithNavController(navController)
    }
}