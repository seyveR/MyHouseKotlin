package com.example.myhousev3.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myhousev3.R
import com.example.myhousev3.chatmvvm.MessageAdapter
import com.example.myhousev3.chatmvvm.MessageItem
import com.example.myhousev3.chatmvvm.MessageViewModel
import com.example.myhousev3.databases.CartDao
import com.example.myhousev3.databases.UserDao
import com.example.myhousev3.databases.UserDb
import com.example.myhousev3.databinding.FragmentAdminChatBinding
import com.example.myhousev3.databinding.FragmentPaymentBinding
import com.example.myhousev3.databinding.FragmentSuccessBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminChatFragment : Fragment() {

    private lateinit var binding: FragmentAdminChatBinding
    private var selectedImagePath: String? = null
    private val messageViewModel: MessageViewModel by viewModels()
    private lateinit var userDao: UserDao
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminChatBinding.inflate(inflater, container, false)

        val adapter = MessageAdapter()
        binding.messageRecyclerView.adapter = adapter
        binding.messageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        navController = findNavController()
        userDao = UserDb.getDb(requireContext()).getDao()


        lifecycleScope.launch {
            val currentUser = withContext(Dispatchers.IO) {
                userDao.getUserByAuthStatus(1)
            }

            val adminUsers = withContext(Dispatchers.IO) {
                userDao.getUsersByAdminStatus()
            }


            val userEmail = currentUser?.email ?: ""
            Log.d("mytag", "email = $userEmail")

            messageViewModel.allMessages.observe(viewLifecycleOwner) { messages ->
                val filteredMessages = if (currentUser != null && currentUser.is_admin == 1) {
                    // Отображать все сообщения для администратора
                    messages
                } else {
                    // Фильтрация сообщений на основе требований и подстановка email из users
                    messages.filter { message ->
                        (message.userEmail == userEmail) || adminUsers.any { adminUser ->
                            adminUser.email == message.userEmail
                        }
                    }
                }

                Log.d("SuccessFragment", "Filtered Messages: $filteredMessages")
                adapter.setMessages(filteredMessages)
            }
        }

        binding.btnBack.setOnClickListener { navController.navigate(R.id.settingsFragment)}
        binding.btnSend.setOnClickListener {
            val messageText = binding.msgEt.text.toString().trim()
            val currentMsgEmail = binding.msgEmail.text.toString().trim()

            if (messageText.isNotEmpty() && currentMsgEmail.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val currentUser = userDao.getCurrentUser()

                    if (currentUser != null) {
                        val messageItem = MessageItem(
                            userEmail = currentUser.email,
                            message = messageText,
                            imageUser = currentUser.imageRes ?: "",
                            to_whom = currentMsgEmail,
                            timestamp = System.currentTimeMillis()
                        )
                        messageViewModel.insert(messageItem)
                        withContext(Dispatchers.Main) {
                            binding.msgEt.setText("")
                            binding.msgEmail.setText("")
                        }
                    }
                }
            }
        }

        return binding.root
    }
}

