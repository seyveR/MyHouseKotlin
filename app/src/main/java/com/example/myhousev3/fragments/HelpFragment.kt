package com.example.myhousev3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhousev3.R
import com.example.myhousev3.databases.CatAdapter
import com.example.myhousev3.databases.CatDao
import com.example.myhousev3.databases.CatDb
import com.example.myhousev3.databases.CatItem
import com.example.myhousev3.databases.ProdAdapter
import com.example.myhousev3.databases.ProdDao
import com.example.myhousev3.databases.ProdDb
import com.example.myhousev3.databases.ProdItem
import com.example.myhousev3.databinding.FragmentHelpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HelpFragment : Fragment() {

    private lateinit var edCategory: EditText
    private lateinit var edItemText: EditText
    private lateinit var edType: EditText
    private lateinit var edPrice: EditText
    private lateinit var imageName: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnView: Button

    private lateinit var binding: FragmentHelpBinding
    private lateinit var recyclerView: RecyclerView


    private lateinit var prodDao: ProdDao
    private lateinit var prodAdapter: ProdAdapter
    private lateinit var userRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpBinding.inflate(inflater, container, false)

        initView()

        prodDao = ProdDb.getDb(requireContext()).getDao()

        val navController = findNavController()
        prodAdapter = ProdAdapter(prodDao, viewLifecycleOwner.lifecycleScope, { bundle ->
            navController.navigate(R.id.prodCartFragment, bundle)
        })

        btnAdd.setOnClickListener{ addService() }
        binding.btnChat.setOnClickListener { navController.navigate(R.id.settingsFragment) }
        binding.btnInfo.setOnClickListener { navController.navigate(R.id.infoFragment) }



        return binding.root
    }

    private fun addService(){
        val name = edItemText.text.toString()
        val type = edCategory.text.toString()
        val info = edType.text.toString()
        val price = edPrice.text.toString()
        val image = imageName.text.toString()

        val text = null
        val imageRes = null

        if (name.isEmpty() || type.isEmpty() || price.isEmpty() || info.isEmpty()){
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
        }
        else{
            val prod = ProdItem(name = name, type = type, price=price, info=info, imageRes = image)
            lifecycleScope.launch(Dispatchers.IO) {
                prodDao.insertProd(prod)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Товар добавлен", Toast.LENGTH_SHORT).show()
                    clearEditText()
                }
            }
        }
    }

    private fun clearEditText() {
        edItemText.setText("")
        edCategory.setText("")
        edPrice.setText("")
        edType.setText("")
        imageName.setText("")
    }

    private fun initView() {
        edItemText = binding.itemText
        edCategory = binding.category
        edType = binding.type
        edPrice = binding.price
        btnAdd = binding.addService
        imageName = binding.imageRes
    }
}