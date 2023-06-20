package com.example.myhousev3.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myhousev3.LoginActivity
import com.example.myhousev3.R
import com.example.myhousev3.databases.OrderDb
import com.example.myhousev3.databases.UserDao
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databases.UserItem
import com.example.myhousev3.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private val REQUEST_CODE_PICK_IMAGE = 1
    private val PERMISSION_CODE_READ_STORAGE = 2

    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val profileImg = binding.profileImg
        profileImg.setOnClickListener {
            if (checkStoragePermission()) {
                pickImageFromGallery()
            } else {
                requestStoragePermission()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.cardViewOrders.setOnClickListener {
            navController.navigate(R.id.ordersFragment)
        }

        getUserData()
        val builder = AlertDialog.Builder(requireContext())

        val btnLogout = binding.btnLogout
        btnLogout.setOnClickListener {
            builder.setMessage("Вы точно хотите выйти?")
            builder.setCancelable(true)
            builder.setPositiveButton("Да"){dialog,_ ->
                logoutUser()
                dialog.dismiss()
            }
            builder.setNegativeButton("Нет"){dialog, _->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }

        val btnEdit = binding.btnEdit
        btnEdit.setOnClickListener {

            builder.setMessage("Вы точно хотите изменить данные?")
            builder.setCancelable(true)
            builder.setPositiveButton("Да"){dialog,_ ->
                updateUserData()
                dialog.dismiss()
            }
            builder.setNegativeButton("Нет"){dialog, _->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()


        }

    }
    private fun updateUserData() {
        val userDao = UserDb.getDb(requireContext()).getDao()
        val orderDao = OrderDb.getDb(requireContext()).getDao()

        viewLifecycleOwner.lifecycleScope.launch {
            val user = getCurrentUser(userDao)
            if (user != null) {
                val oldEmail = user.email
                val newEmail = binding.EmailTv.text.toString()
                val firstname = binding.NameTv.text.toString()

                user.firstname = firstname
                user.email = newEmail

                withContext(Dispatchers.IO) {
                    updateUser(userDao, user)

                    // Update user_info in the order table
                    orderDao.updateUserInfoByEmail(oldEmail, newEmail)
                }
            }
        }
    }

    private fun logoutUser() {
        GlobalScope.launch {
            updateUserAuthStatus(false)
            withContext(Dispatchers.Main) {
                navigateToLogin()
            }
        }
    }

    private suspend fun updateUserAuthStatus(isAuth: Boolean) {
        val user = getCurrentUser()
        user?.is_auth = if (isAuth) 1 else 0
        user?.let { UserDb.getDb(requireContext()).getDao().updateUser(it) }
    }

    private suspend fun getCurrentUser(): UserItem? {
        val userDao = UserDb.getDb(requireContext()).getDao()
        return getCurrentUser(userDao)
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getUserData() {
        val userDao = UserDb.getDb(requireContext()).getDao()
        viewLifecycleOwner.lifecycleScope.launch {
            val user = getCurrentUser(userDao)
            if (user != null) {
                binding.NameTv.text = user.firstname.toEditable()
                binding.EmailTv.text = user.email.toEditable()

                // Load image from database into ImageView
                val imgUri = user.imageRes
                if (imgUri != null) {
                    Glide.with(requireContext()).load(Uri.parse(imgUri)).into(binding.profileImg)
                }
            } else {
                // Handle the case when user is not authenticated
            }
        }
    }

    private suspend fun getCurrentUser(userDao: UserDao): UserItem? {
        return userDao.getUserByAuthStatus(1)
    }
    private fun String.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }

    private suspend fun updateUser(userDao: UserDao, user: UserItem) {
        withContext(Dispatchers.IO) {
            Log.d("ProfileFragment", "Updating user: $user")
            userDao.updateUser(user)
        }
    }

    //imageProfile
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_CODE_READ_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CODE_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                // permission denied
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            val uri = data?.data
            uri?.let {
                // Now, you have the Uri of the selected image, you can do whatever you want with it.
                // For example, you can display it in the ImageView using Glide or other library
                Glide.with(requireContext()).load(uri).into(binding.profileImg)

                // Or save the uri or path of the image to the database.
                // Here, we are assuming that you have a function called `saveImageToDatabase` to do that.
                saveImageToDatabase(uri)
            }
        }
    }

    private fun saveImageToDatabase(uri: Uri) {
        val userDao = UserDb.getDb(requireContext()).getDao()
        viewLifecycleOwner.lifecycleScope.launch {
            val user = getCurrentUser(userDao)
            if (user != null) {
                user.imageRes = uri.toString() // Saving Uri as String. You can change this to save path instead.
                Log.d("ProfileFragment", "Saving uri to db: ${uri.toString()}")
                updateUser(userDao, user)
            }
        }
    }
}
