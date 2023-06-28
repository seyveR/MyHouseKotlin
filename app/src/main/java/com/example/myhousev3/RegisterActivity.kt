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
import java.util.regex.Pattern
import org.mindrot.jbcrypt.BCrypt


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val db = UserDb.getDb(this)

        binding.regBtn.setOnClickListener {
            val email = binding.mail.text.toString().trim()
            val password = binding.pass.text.toString()
            val firstName = binding.firstName.text.toString()

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val firstNamePattern = Pattern.compile("[^0-9]+")
            val firstNameMatcher = firstNamePattern.matcher(firstName)
            if (!firstNameMatcher.matches()) {
                Toast.makeText(this, "Имя не должно содержать цифры", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailPattern = Pattern.compile("^\\S+@(mail|yandex|gmail)\\.(ru|com)$")
            val emailMatcher = emailPattern.matcher(email)
            if (!emailMatcher.matches()) {
                Toast.makeText(this, "Некорректный адрес электронной почты", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPattern = Pattern.compile("^(?=.*[A-Za-z].*[A-Za-z])(?=.*[0-9].*[0-9]).{8,}$")
            val passwordMatcher = passwordPattern.matcher(password)
            if (!passwordMatcher.matches()) {
                Toast.makeText(this, "Пароль должен содержать минимум 8 символов, минимум 6 букв и минимум 2 цифры", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

            // Остальной код остается без изменений
            GlobalScope.launch {
                val userList = checkUser(email)
                if (userList == null) {
                    val user = UserItem(null, firstName, email, hashedPassword)
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

    private suspend fun checkUser(email: String): UserItem? {
        return UserDb.getDb(this).getDao().getUserEmail(email)
    }
}
