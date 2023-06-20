package com.example.myhousev3.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myhousev3.R
import com.example.myhousev3.databases.CartDao
import com.example.myhousev3.databases.CartDb
import com.example.myhousev3.databases.CartItem
import com.example.myhousev3.databases.ProdDao
import com.example.myhousev3.databases.ProdDb
import com.example.myhousev3.databases.ProdItem
import com.example.myhousev3.databases.SharedViewModel
import com.example.myhousev3.databases.UserDao
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databinding.FragmentProdCartBinding
import com.example.myhousev3.databinding.FragmentProdCartOthersBinding
import com.google.android.material.badge.BadgeDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProdCartOthersFragment : Fragment() {

    private lateinit var binding: FragmentProdCartOthersBinding
    private lateinit var userDao: UserDao
    private lateinit var cartDao: CartDao
    private lateinit var prodDao: ProdDao
    private lateinit var navController: NavController
    private var badge: BadgeDrawable? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var sameNameProducts: List<ProdItem> = listOf()
    private var currentProductIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProdCartOthersBinding.inflate(inflater, container, false)
        userDao = UserDb.getDb(requireContext()).getDao()
        cartDao = CartDb.getDb(requireContext()).getDao()
        prodDao = ProdDb.getDb(requireContext()).getDao()



        val btnCart = binding.btnCart
        btnCart.setOnClickListener {
            lifecycleScope.launch {
                val currentUser = userDao.getUserByAuthStatus(1)
                if (currentUser == null) {
                    // Если пользователь не вошел в систему
                    AlertDialog.Builder(requireContext())
                        .setTitle("Подтверждение")
                        .setMessage("You need to login before adding product to cart")
                        .setPositiveButton("OK") { dialog, which ->
                            navController = findNavController()
                            navController.navigate(R.id.homeFragment)
                        }
                        .show()
                } else {
                    // Если пользователь вошел в систему
                    AlertDialog.Builder(requireContext())
                        .setTitle("Подтверждение")
                        .setMessage("Вы точно хотите добавить услугу в корзину?")
                        .setPositiveButton("Да") { dialog, which ->
                            addToCart()
                            dialog.dismiss()
                        }
                        .setNegativeButton("Нет") { dialog, which ->
                            // Закрыть диалог
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }

        val id = arguments?.getInt("id")
        val name = arguments?.getString("name")
        val type = arguments?.getString("type")
        val price = arguments?.getString("price")
        val info = arguments?.getString("info")
        val imgRes = arguments?.getString("imgRes")
        val imgCat = arguments?.getString("imgCat")

        val controller = findNavController()
        val backBtn = binding.backBtn
        backBtn.setOnClickListener{ controller.navigate(R.id.homeFragment) }

        Log.d("ProdCartFragment", "Arguments: id=$id, name=$name, price=$price, size=$info")
        val nameTextView = binding.name // Замените на ваш идентификатор
        val priceTextView = binding.price // Замените на ваш идентификатор
        val infoTextView = binding.info // Замените на ваш идентификатор
        val imageView = binding.prodImg

        nameTextView.text = name
        priceTextView.text = price
        infoTextView.text = info

        // Получите ссылку на ImageView
        if (imgCat != null) {
            val imgCatId = resources.getIdentifier(
                imgCat,
                "drawable",
                requireContext().packageName
            )
            imageView.setImageResource(imgCatId)
        } else {
            imageView.setImageResource(R.drawable.allcl) // Загрузите изображение по умолчанию, если у пользователя нет изображения
        }


        val bundle = arguments
        val names = bundle?.getString("name")

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                names?.let {
                    sameNameProducts = prodDao.getProductsByName(it)
                    currentProductIndex = sameNameProducts.indexOfFirst { it.info == bundle?.getString("weight") }
                }
            }
            withContext(Dispatchers.Main) {
                updateUI()
            }
        }


        return binding.root
    }

    private fun addToCart() {
        val name = binding.name.text.toString()
        val price = binding.price.text.toString()
        val type = arguments?.getString("type")!!
        val info = binding.info.text.toString()
        val imgRes = arguments?.getString("imgRes")

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val currentUser = userDao.getUserByAuthStatus(1)
                if (currentUser != null) {
                    val userInfo = currentUser.email
                    val cartItem = CartItem(name = name, type = type, price = price, info = info, user_info = userInfo, imageRes = imgRes)
                    cartDao.insertCart(cartItem)
                    sharedViewModel.incrementCartItemCount()
                }
            }
        }
    }

    private fun updateUI() {
        if (currentProductIndex >= 0 && sameNameProducts.isNotEmpty()) {
            val product = sameNameProducts[currentProductIndex]
            binding.info.text = product.info
            binding.price.text = product.price
        }
    }

}