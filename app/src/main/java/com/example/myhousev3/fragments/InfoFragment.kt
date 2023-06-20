package com.example.myhousev3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

import com.bumptech.glide.Glide
import com.example.myhousev3.R
import com.example.myhousev3.databinding.FragmentInfoBinding

import com.example.myhousev3.databinding.FragmentLoadBinding


class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false)
        navController = findNavController()

        binding.btnBack.setOnClickListener { navController.navigate(R.id.helpFragment) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}
