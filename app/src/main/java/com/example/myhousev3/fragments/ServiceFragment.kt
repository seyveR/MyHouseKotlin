package com.example.myhousev3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhousev3.R
import com.example.myhousev3.databases.ProdAdapter
import com.example.myhousev3.databases.ProdDao
import com.example.myhousev3.databases.ProdDb
import com.example.myhousev3.databinding.FragmentHomeBinding
import com.example.myhousev3.databinding.FragmentServiceBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ServiceFragment : Fragment() {

    private lateinit var binding: FragmentServiceBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var prodDao: ProdDao
    private lateinit var prodAdapter: ProdAdapter
    private var catName: String? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView ?: throw RuntimeException("RecyclerView not found in layout")

        prodDao = ProdDb.getDb(requireContext()).getDao()
        navController = findNavController()
        // Initialize prodAdapter here, before using it
        prodAdapter = ProdAdapter(prodDao, viewLifecycleOwner.lifecycleScope) { bundle ->
            findNavController().navigate(R.id.prodCartFragment, bundle)
        }
        binding.btnBack.setOnClickListener { navController.navigate(R.id.homeFragment) }

        recyclerView.adapter = prodAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())



        // Get the cat name from the arguments
        val catName = arguments?.getString("cat_name")
        if (catName != null) {
            viewProdByCat(catName)
        } else {
            viewProd()
        }

        return binding.root
    }

    private fun viewProd() {
        prodDao.getUniqueNameLowestWeight().onEach { products ->
            prodAdapter.setProd(products)
        }.launchIn(lifecycleScope)
    }
    private fun viewProdByCat(catName: String) {
        prodDao.getProductsByType(catName).onEach { products ->
            prodAdapter.setProd(products)
        }.launchIn(lifecycleScope)
    }
}
