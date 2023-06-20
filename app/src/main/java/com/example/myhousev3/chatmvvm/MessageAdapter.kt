package com.example.myhousev3.chatmvvm

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myhousev3.R
import com.example.myhousev3.databases.UserDb
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private var messages = emptyList<MessageItem>()

    fun setMessages(messageList: List<MessageItem>) {
        this.messages = messageList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = messages[position]
        holder.bind(msg)
    }

    override fun getItemCount(): Int = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(msg: MessageItem) {
            itemView.findViewById<TextView>(R.id.user_email).text = msg.userEmail
            itemView.findViewById<TextView>(R.id.message).text = msg.message

            val timestamp = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date(msg.timestamp))
            itemView.findViewById<TextView>(R.id.timestamp).text = timestamp

            val userDao = UserDb.getDb(itemView.context).getDao()
            CoroutineScope(Dispatchers.IO).launch {
                val isAdmin = userDao.getUserByAdminStatus2(msg.userEmail) != null

                withContext(Dispatchers.Main) {
                    if (isAdmin) {
                        // Изменяем цвет фона для сообщений администратора
                        itemView.findViewById<View>(R.id.msgBg).apply {
                            setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.lightBlue))
                        }

                    }else{
                        itemView.findViewById<TextView>(R.id.user_email).apply {
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                        }
                        itemView.findViewById<TextView>(R.id.timestamp).apply {
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                        }
                        itemView.findViewById<TextView>(R.id.message).apply {
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                        }
                    }
                }
            }
        }
    }
}


