package com.example.myhousev3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val userDao = UserDb.getDb(this).getDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val currentUser = userDao.getCurrentUser()
            if (currentUser?.is_auth == 1) {
                withContext(Dispatchers.Main) {
                    val ivLogo = binding.ivLogo
                    ivLogo.alpha = 0f
                    ivLogo.animate().setDuration(3000).alpha(1f).withEndAction{
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                        finish()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    val ivLogo = binding.ivLogo
                    ivLogo.alpha = 0f
                    ivLogo.animate().setDuration(3000).alpha(1f).withEndAction{
                        val i = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(i)
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                        finish()
                    }
                }
            }
        }
    }
}
