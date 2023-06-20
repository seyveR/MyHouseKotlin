package com.example.myhousev3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myhousev3.R
import com.example.myhousev3.databases.SharedViewModel
import com.example.myhousev3.databinding.FragmentInfoBinding
import com.example.myhousev3.databinding.FragmentLoadBinding


class LoadFragment : Fragment() {
    private lateinit var binding: FragmentLoadBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadBinding.inflate(inflater, container, false)

        Glide.with(this)
            .asGif()
            .load(R.drawable.konfeti)
            .into(binding.gif)

        // Наблюдаем за общим количеством товаров в корзине
        sharedViewModel.getCartItemCount().observe(viewLifecycleOwner) { itemCount ->
            // Если данные загружены, перейдите в CartFragment
            if (itemCount != null) {
                findNavController().navigate(R.id.action_loadFragment_to_cartFragment)
            }
        }

        // Наблюдаем за общей стоимостью товаров в корзине
        sharedViewModel.getTotalPrice().observe(viewLifecycleOwner) { totalPrice ->
            // Дополнительно, вы можете использовать totalPrice
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Загружаем данные

    }
}