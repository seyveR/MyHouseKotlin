package com.example.myhousev3


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databases.UserItem
import com.example.myhousev3.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val db = UserDb.getDb(this)

        suspend fun checkUser(email: String): UserItem? {
            return UserDb.getDb(this).getDao().getUserEmail(email)
        }
        binding.regBtn.setOnClickListener {
            val email = binding.mail.text.toString().trim()
            val password = binding.pass.text.toString()
            val firstName = binding.firstName.text.toString()


            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            GlobalScope.launch {
                val userList = checkUser(email)
                if (userList == null) {
                    val user = UserItem(null,
                        binding.firstName.text.toString(),
                        binding.mail.text.toString(),
                        binding.pass.text.toString(),
                    )
                    db.getDao().insertUser(user)
                    withContext(Dispatchers.Main) {
                        // Очистка текстовых полей
                        binding.firstName.setText("")
                        binding.mail.setText("")
                        binding.pass.setText("")

                        Toast.makeText(this@RegisterActivity, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.firstName.setText("")
                        binding.mail.setText("")
                        binding.pass.setText("")
                        Toast.makeText(this@RegisterActivity, "Пользователь с такой почтой уже существует", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.btnLogReg.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }
    }


}
