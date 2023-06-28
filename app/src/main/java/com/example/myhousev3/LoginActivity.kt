package com.example.myhousev3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.R.anim.abc_fade_in
import androidx.lifecycle.lifecycleScope
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databases.UserItem
import com.example.myhousev3.databinding.ActivityLoginBinding
import com.example.myhousev3.databinding.ActivityMainBinding
import com.example.myhousev3.databinding.ActivityRegisterBinding
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt



class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)


        binding.loginBtn.setOnClickListener {
            val email = binding.mail.text.toString().trim()
            val password = binding.password.text.toString().trim()  // Применяем trim() и к паролю

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password)
        }

        binding.btnRegLog.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = UserDb.getDb(this@LoginActivity).getDao()
            val user = userDao.getUserEmail(email)
            if (user != null && BCrypt.checkpw(password, user.pass)) {
                user.is_auth = 1
                userDao.updateUser(user)
                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.password.setText("")
                    Toast.makeText(this@LoginActivity, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        return
    }
}

