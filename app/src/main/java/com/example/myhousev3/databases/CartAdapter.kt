package com.example.myhousev3.databases

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myhousev3.R

class CartAdapter(private val deleteCallback: (CartItem) -> Unit, private val navController: NavController,
                  private val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var carts: List<CartItem> = listOf()
    private var colors: List<Int> = listOf(
        R.color.catColor1, R.color.catColor2, R.color.catColor3,
        R.color.catColor4, R.color.catColor5, R.color.catColor6 )

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_ITEM = 1
    }

    fun setCart(cart: List<CartItem>) {
        this.carts = cart
        var total = 0.0
        for (item in cart) {
            total += item.price.toDoubleOrNull() ?: 0.0
        }
        sharedViewModel.setTotalPrice(total)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (carts.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    fun isCartEmpty(): Boolean {
        return carts.isEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EMPTY -> {
                val view = inflater.inflate(R.layout.cart_model_empty, parent, false)
                EmptyViewHolder(view, navController)
            }
            TYPE_ITEM -> {
                val view = inflater.inflate(R.layout.cart_model, parent, false)
                CartViewHolder(view, deleteCallback, sharedViewModel)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CartViewHolder) {
            holder.bind(carts[position])

            val color = ContextCompat.getColor(holder.itemView.context, colors[position % colors.size])
            holder.itemView.findViewById<CardView>(R.id.prodImgCv).setCardBackgroundColor(color)
            val itemCount = carts.size
            sharedViewModel.setCartItemCount(itemCount)
        }
    }

    override fun getItemCount(): Int {
        return if (carts.isEmpty()) 1 else carts.size
    }

    class EmptyViewHolder(itemView: View, private val navController: NavController)  : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<Button>(R.id.toHome).setOnClickListener {
                navController.navigate(R.id.homeFragment)
            }
        }
    }

    class CartViewHolder(itemView: View, private val deleteCallback: (CartItem) -> Unit, private val sharedViewModel: SharedViewModel) : RecyclerView.ViewHolder(itemView) {
        fun bind(cart: CartItem) {

            itemView.findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(it.context)
                builder.setMessage("Вы точно хотите убрать товар из корзины?")
                builder.setCancelable(true)
                builder.setPositiveButton("Да") { dialog, _ ->
                    deleteCallback(cart)
                    sharedViewModel.decrementCartItemCount()
                    dialog.dismiss()
                }
                builder.setNegativeButton("Нет") { dialog, _ ->
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }

            itemView.findViewById<TextView>(R.id.name).text = cart.name
//            itemView.findViewById<TextView>(R.id.type).text = cloth.type
            itemView.findViewById<TextView>(R.id.price).text = cart.price
            itemView.findViewById<TextView>(R.id.info).text = cart.info
            val imageView = itemView.findViewById<ImageView>(R.id.prodImg)
            if (cart.imageRes != null) {
                val imageResId = itemView.context.resources.getIdentifier(
                    cart.imageRes,
                    "drawable",
                    itemView.context.packageName
                )
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.logohousepng) // Загрузите изображение по умолчанию, если у пользователя нет изображения
            }
        }
    }
}
