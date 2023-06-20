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

import com.example.myhousev3.databinding.FragmentLoadBinding


class InfoFragment : Fragment() {

    private lateinit var binding: FragmentLoadBinding
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadBinding.inflate(inflater, container, false)
        navController = findNavController()

        Glide.with(this)
            .asGif()
            .load(R.drawable.load)
            .into(binding.gif)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}
