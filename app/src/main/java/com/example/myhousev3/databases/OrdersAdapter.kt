package com.example.myhousev3.databases

import android.graphics.Color
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myhousev3.R
import java.util.logging.Handler

class OrdersAdapter(private val deleteCallback: (OrderItem) -> Unit, private val navController: NavController) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var orders: List<OrderItem> = listOf()
    private var colors: List<Int> = listOf(
        R.color.catColor1, R.color.catColor2, R.color.catColor3,
        R.color.catColor4, R.color.catColor5, R.color.catColor6 )


    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_ITEM = 1
    }

    fun setOrder(order: List<OrderItem>) {
        this.orders = order
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (orders.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    fun isOrderEmpty(): Boolean {
        return orders.isEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EMPTY -> {
                val view = inflater.inflate(R.layout.orders_model_empty, parent, false)
                EmptyViewHolder(view, navController)
            }
            TYPE_ITEM -> {
                val view = inflater.inflate(R.layout.orders_model, parent, false)
                OrderViewHolder(view, deleteCallback)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OrderViewHolder) {
            holder.bind(orders[position])

            val color = ContextCompat.getColor(holder.itemView.context, colors[position % colors.size])
            holder.itemView.findViewById<CardView>(R.id.prodImgCv).setCardBackgroundColor(color)
        }
        // Для EmptyViewHolder ничего делать не нужно
    }

    override fun getItemCount(): Int {
        return if (orders.isEmpty()) 1 else orders.size
    }

    class EmptyViewHolder(itemView: View, private val navController: NavController)  : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<Button>(R.id.toHome).setOnClickListener {
                navController.navigate(R.id.homeFragment)
            }
        }
    }

    class OrderViewHolder(itemView: View, private val deleteCallback: (OrderItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(order: OrderItem) {

            itemView.findViewById<TextView>(R.id.name).text = order.name
//            itemView.findViewById<TextView>(R.id.type).text = cloth.type
            itemView.findViewById<TextView>(R.id.price).text = order.price
            itemView.findViewById<TextView>(R.id.info).text = order.info
            val imageView = itemView.findViewById<ImageView>(R.id.prodImg)
            if (order.imageRes != null) {
                val imageResId = itemView.context.resources.getIdentifier(
                    order.imageRes,
                    "drawable",
                    itemView.context.packageName
                )
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.logohousepng)
            }

            val statusTextView = itemView.findViewById<TextView>(R.id.tvStatus)
            val statusCardView = itemView.findViewById<CardView>(R.id.status)
            statusTextView.text = "в обработке"
            statusCardView.setCardBackgroundColor(Color.BLUE)

            android.os.Handler(Looper.getMainLooper()).postDelayed({
                statusTextView.text = "выполнено"
                statusCardView.setCardBackgroundColor(Color.GREEN)
            }, 5000)
        }
    }
}