package com.example.myhousev3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhousev3.R
import com.example.myhousev3.databases.CatAdapter
import com.example.myhousev3.databases.CatDao
import com.example.myhousev3.databases.CatDb
import com.example.myhousev3.databases.SharedViewModel
import com.example.myhousev3.databinding.FragmentHomeBinding
import com.example.myhousev3.databinding.FragmentServiceBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var catDao: CatDao
    private lateinit var catAdapter: CatAdapter
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView ?: throw RuntimeException("RecyclerView not found in layout")

        catDao = CatDb.getDb(requireContext()).getDao()

        catAdapter = CatAdapter(catDao, viewLifecycleOwner.lifecycleScope) { bundle ->
            findNavController().navigate(R.id.serviceFragment, bundle)
        }




        recyclerView.adapter = catAdapter
        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // В зависимости от позиции меняем размер элемента.
                return when (position) {
                    0 -> 2 // для первого элемента занимаем 2 столбца (всю ширину)
                    1 -> 1 // для второго элемента занимаем 1 столбец (половину ширины)
                    2 -> 1
                    3 -> 1
                    4 -> 1
                    5 -> 2
                    else -> if (position % 2 == 0) 1 else 2 // для всех остальных позиций чередуем размеры
                }
            }
        }
        recyclerView.layoutManager = layoutManager

        lifecycleScope.launch {
            catDao.getAllCat().collect { categories ->
                catAdapter.setCat(categories)
            }
        }

        return binding.root
    }

}