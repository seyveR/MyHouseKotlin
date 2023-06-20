package com.example.myhousev3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.myhousev3.R
import com.example.myhousev3.databinding.FragmentSettingsBinding
import com.example.myhousev3.databinding.FragmentSuccessBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        navController = findNavController()

        binding.helpCv.setOnClickListener { navController.navigate(R.id.successFragment) }
        return binding.root

    }
}