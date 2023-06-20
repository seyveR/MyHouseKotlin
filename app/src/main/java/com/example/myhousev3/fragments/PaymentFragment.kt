package com.example.myhousev3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myhousev3.R
import com.example.myhousev3.databases.CartDao
import com.example.myhousev3.databases.CartDb
import com.example.myhousev3.databases.OrderDao
import com.example.myhousev3.databases.OrderDb
import com.example.myhousev3.databases.OrderItem
import com.example.myhousev3.databases.SharedViewModel
import com.example.myhousev3.databases.UserDao
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databinding.FragmentPaymentBinding
import com.google.android.material.badge.BadgeDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding
    private lateinit var userDao: UserDao
    private lateinit var cartDao: CartDao
    private lateinit var orderDao: OrderDao
    private var badge: BadgeDrawable? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        val controller = findNavController()
        val backBtn = binding.backBtn
        backBtn.setOnClickListener{ controller.navigate(R.id.cartFragment) }


        userDao = UserDb.getDb(requireContext()).getDao()
        cartDao = CartDb.getDb(requireContext()).getDao()
        orderDao = OrderDb.getDb(requireContext()).getDao()

        binding.tinkoffCv.setOnClickListener {
            // Заполняем поля значениями при нажатии на CardView
            binding.cardNumberEdit.setText("2200 7002 8092 9002")
            binding.EditCVV.setText("777")
            binding.EditDate.setText("31/30")
        }

        binding.sbpCv.setOnClickListener {
            // Заполняем поля значениями при нажатии на CardView
            binding.cardNumberEdit.setText("")
            binding.EditCVV.setText("")
            binding.EditDate.setText("")
        }

        binding.btnPay.setOnClickListener {
            if (binding.cardNumberEdit.text.isNullOrEmpty() ||
                binding.EditCVV.text.isNullOrEmpty() ||
                binding.EditDate.text.isNullOrEmpty()
            ) {

                // Здесь вы можете показать сообщение пользователю, что все поля должны быть заполнены
                Toast.makeText(
                    requireContext(),
                    "Все поля должны быть заполнены",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                lifecycleScope.launch {
                    val currentUser = withContext(Dispatchers.IO) {
                        userDao.getUserByAuthStatus(1)
                    }
                    currentUser?.let {
                        withContext(Dispatchers.IO) {
                            val cartItems = cartDao.getCartItemsByUser(it.email).first()
                            cartItems.forEach { cartItem ->
                                val orderItem = OrderItem(
                                    name = cartItem.name,
                                    type = cartItem.type,
                                    price = cartItem.price,
                                    info = cartItem.info,
                                    user_info = it.email,
                                    imageRes = cartItem.imageRes
                                )
                                orderDao.insertOrder(orderItem)
                            }
                            cartDao.deleteCartItemsByUser(it.email)
                            sharedViewModel.resetCartItemCount()
                        }
                        controller.navigate(R.id.cartFragment)
                    }
                }
            }
        }

        return binding.root
    }
}

