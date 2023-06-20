package com.example.myhousev3.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhousev3.databinding.FragmentCartBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myhousev3.R
import com.example.myhousev3.databases.CartAdapter
import com.example.myhousev3.databases.CartDao
import com.example.myhousev3.databases.CartDb
import com.example.myhousev3.databases.SharedViewModel

import com.example.myhousev3.databases.UserDao
import com.example.myhousev3.databases.UserDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private var currentUserEmail: String = ""
    private lateinit var userDao: UserDao
    private lateinit var cartDao: CartDao
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView ?: throw RuntimeException("RecyclerView not found in layout")
        cartDao = CartDb.getDb(requireContext()).getDao()
        userDao = UserDb.getDb(requireContext()).getDao()
        navController = findNavController()

        initRecyclerView()

        lifecycleScope.launch {
            val currentUser = withContext(Dispatchers.IO) {
                userDao.getUserByAuthStatus(1)
            }
            currentUserEmail = currentUser?.email ?: ""

        }

        viewCart()
        binding.toPay.setOnClickListener {
            navController.navigate(R.id.paymentFragment)
        }

        sharedViewModel.getTotalPrice().observe(viewLifecycleOwner, Observer { price ->
            binding.totalPrice.text = price.toString()
        })

        return binding.root
    }

    private fun viewCart() {
        lifecycleScope.launch {
            val currentUser = withContext(Dispatchers.IO) {
                userDao.getUserByAuthStatus(1)
            }
            currentUserEmail = currentUser?.email ?: ""

            cartDao.getCartByUser(currentUserEmail).onEach { carts ->
                cartAdapter.setCart(carts)
                binding.toPay.visibility = if (cartAdapter.isCartEmpty()) View.GONE else View.VISIBLE
                binding.totalPrice.visibility = if (cartAdapter.isCartEmpty()) View.GONE else View.VISIBLE
                binding.priceTv.visibility = if (cartAdapter.isCartEmpty()) View.GONE else View.VISIBLE
                binding.rublesTv.visibility = if (cartAdapter.isCartEmpty()) View.GONE else View.VISIBLE
            }.launchIn(lifecycleScope)
        }
    }

    private fun initRecyclerView() {
        cartAdapter = CartAdapter({ cartItem ->
            cartItem.id?.let { id ->
                lifecycleScope.launch {
                    cartDao.deleteCartItem(id)
                }
            }
        }, findNavController(), sharedViewModel) // Здесь передаем sharedViewModel

        recyclerView.adapter = cartAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}