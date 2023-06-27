package com.example.myhousev3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.myhousev3.databases.CatDao
import com.example.myhousev3.databases.CatDb
import com.example.myhousev3.databases.CatItem
import com.example.myhousev3.databases.ProdDao
import com.example.myhousev3.databases.ProdDb
import com.example.myhousev3.databases.ProdItem
import com.example.myhousev3.databases.UserDao
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databinding.ActivitySplashScreenBinding
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var catDao: CatDao
    private lateinit var prodDao: ProdDao
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        MapKitFactory.setApiKey("a4476ed5-9873-45bf-b62f-6fc6549aa4b5")
        MapKitFactory.initialize(this)

        catDao = CatDb.getDb(this).getDao()
        prodDao = ProdDb.getDb(this).getDao()
        userDao = UserDb.getDb(this).getDao()


        val userDao = UserDb.getDb(this).getDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val currentUser = userDao.getCurrentUser()

            val initializeDatabaseJob = launch {
                initializeDatabase()
            }
            val initializeProdDatabaseJob = launch {
                initializeProdDatabase()
            }
            // wait for both jobs to complete
            initializeDatabaseJob.join()
            initializeProdDatabaseJob.join()

            withContext(Dispatchers.Main) {
                if (currentUser?.is_auth == 1) {
                    val ivLogo = binding.ivLogo
                    ivLogo.alpha = 0f
                    ivLogo.animate().setDuration(3000).alpha(1f).withEndAction {
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    val ivLogo = binding.ivLogo
                    ivLogo.alpha = 0f
                    ivLogo.animate().setDuration(3000).alpha(1f).withEndAction {
                        val i = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(i)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }
            }
        }
    }
    private suspend fun isDatabaseEmpty(): Boolean {
        return catDao.getAllCat().first().isEmpty()
    }

    private suspend fun initializeDatabase() {
        if (isDatabaseEmpty()) {
            val initialCategories = listOf(
                CatItem(name = "Грузоперевозки", type = "Trucking", imageRes = "trucking"),
                CatItem(name = "Доставка воды", type = "Water Delivery", imageRes = "water"),
                CatItem(name = "Уборка", type = "Cleaning", imageRes = "cleaning"),
                CatItem(name = "Сантехник", type = "Plumber", imageRes = "plumber"),
                CatItem(name = "Няня", type = "Nanny", imageRes = "nanny"),
                CatItem(name = "Химчистка", type = "Dry", imageRes = "dry")
            )
            for (cat in initialCategories) {
                catDao.insertCat(cat)
            }
        }
    }

    private suspend fun initializeProdDatabase() {
        if(prodDao.getAllProd().first().isEmpty()) {
            val prodItems = listOf(
                ProdItem(name = "Мебель", type = "Уборка", price = "2000", info = "2-3 ч", imageRes = "furniturecl", imageCat = "furniturecat"),
                ProdItem(name = "Вода", type = "Доставка воды", price = "180", info = "10 л", imageRes = "watermedium", imageCat = "watersmallcat"),
                ProdItem(name = "Окна", type = "Уборка", price = "3000", info = "3-4 ч", imageRes = "windowcl", imageCat = "windowcat"),
                ProdItem(name = "Вода", type = "Доставка воды", price = "200", info = "15 л", imageRes = "watermedium", imageCat = "waterdeliverybig"),
                ProdItem(name = "Вода", type = "Доставка воды", price = "250", info = "25 л", imageRes = "waterbig", imageCat = "waterdelivery"),
                ProdItem(name = "Вода", type = "Доставка воды", price = "360", info = "50 л", imageRes = "waterbig", imageCat = "watersuper"),
                ProdItem(name = "Уборка", type = "Уборка", price = "5000", info = "4-5 ч", imageRes = "allcl", imageCat = "allcat"),
                ProdItem(name = "Установка ванной техники", type = "Сантехник", price = "3000", info = "2-3 ч", imageRes = "wanna", imageCat = "wannacat"),
                ProdItem(name = "Чистка канализации", type = "Сантехник", price = "1500", info = "2-3 ч", imageRes = "chistka", imageCat = "chistkacat"),
                ProdItem(name = "Машина между городами", type = "Грузоперевозки", price = "7000", info = "1-10 т", imageRes = "carbetwcity", imageCat = "carbetwcity")



            )
            prodItems.forEach { prodDao.insertProd(it) }
        }
    }
}
