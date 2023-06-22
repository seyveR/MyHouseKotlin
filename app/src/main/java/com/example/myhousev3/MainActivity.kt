package com.example.myhousev3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myhousev3.databases.CartDao
import com.example.myhousev3.databases.CartDb
import com.example.myhousev3.databases.CatDao
import com.example.myhousev3.databases.CatDb
import com.example.myhousev3.databases.CatItem
import com.example.myhousev3.databases.ProdDao
import com.example.myhousev3.databases.ProdDb
import com.example.myhousev3.databases.ProdItem
import com.example.myhousev3.databases.SharedViewModel
import com.example.myhousev3.databases.UserDao
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.AccessController

class MainActivity : AppCompatActivity() {

    private lateinit var conf: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var userDao: UserDao
    private lateinit var cartDao: CartDao
    private var cartBadge: BadgeDrawable? = null
    private lateinit var catDao: CatDao
    private lateinit var prodDao: ProdDao
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MapKitFactory.setApiKey("a4476ed5-9873-45bf-b62f-6fc6549aa4b5")
        MapKitFactory.initialize(this)

        //BOTTOM NAVIGATION
        val botNavView: BottomNavigationView = binding.botNavView
        navController = findNavController(R.id.fragmentContainerView)
        cartDao = CartDb.getDb(this).getDao()
        userDao = UserDb.getDb(this).getDao()
        catDao = CatDb.getDb(this).getDao()
        prodDao = ProdDb.getDb(this).getDao()


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> botNavView.menu.findItem(R.id.homeFragment).isChecked = true
                R.id.cartFragment -> botNavView.menu.findItem(R.id.cartFragment).isChecked = true
                R.id.profileFragment -> botNavView.menu.findItem(R.id.profileFragment).isChecked = true
                R.id.helpFragment -> botNavView.menu.findItem(R.id.helpFragment).isChecked = true
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                val currentUser = userDao.getUserByAuthStatus(1)
                currentUser?.let { user ->
                    val cartItemCount = cartDao.getCartItemCount(user.email).first()
                    sharedViewModel.setCartItemCount(cartItemCount)
                    cartBadge = botNavView.getOrCreateBadge(R.id.cartFragment)
                    cartBadge?.isVisible = cartItemCount > 0
                    cartBadge?.number = cartItemCount
                }
            }
        }

        sharedViewModel.getCartItemCount().observe(this, Observer { count ->
            if (count > 0) {
                cartBadge?.number = count
                cartBadge?.isVisible = true
            } else {
                cartBadge?.isVisible = false
            }
        })

        lifecycleScope.launch {
            val currentUser = userDao.getUserByAdminStatus(1)

            conf = AppBarConfiguration(
                setOf(
                    if (currentUser != null) R.id.profileFragment else R.id.settingsFragment,
                    R.id.homeFragment,
                    R.id.cartFragment,
                    R.id.profileFragment,

                )
            )

            withContext(Dispatchers.Main) {
                setupActionBarWithNavController(navController, conf)
                botNavView.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.homeFragment, R.id.cartFragment, R.id.profileFragment -> {
                            navController.popBackStack(R.id.homeFragment, false)
                            navController.popBackStack(R.id.cartFragment, false)
                            navController.navigate(item.itemId)
                            true
                        }

                        R.id.helpFragment -> {
                            lifecycleScope.launch {
                                val authUser = userDao.getUserByAuthStatus(1)
                                Log.d("MainActivity", "Authenticated user: $authUser")
                                if (authUser != null && authUser.is_admin == 1) {
                                    Log.d("MainActivity", "Navigating to helpFragment")
                                    navController.navigate(R.id.helpFragment)
                                } else {
                                    Log.d("MainActivity", "Navigating to settingsFragment")
                                    navController.navigate(R.id.settingsFragment)
                                }
                            }
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        if(navController.currentDestination?.id == R.id.homeFragment) {
            return
        } else {
            super.onBackPressed()
        }
    }

}
