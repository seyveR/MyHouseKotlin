package com.example.myhousev3.databases

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.myhousev3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProdAdapter(private val prodDao: ProdDao, private val lifecycleScope: LifecycleCoroutineScope,
                   private val itemClickListener: (Bundle) -> Unit) : RecyclerView.Adapter<ProdAdapter.ProdViewHolder>() {
    private var products: List<ProdItem> = listOf()

    private var colors: List<Int> = listOf(
        R.color.catColor1, R.color.catColor2, R.color.catColor3,
        R.color.catColor4, R.color.catColor5, R.color.catColor6 )

    fun setProd(prod: List<ProdItem>) {
        this.products = prod
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.prod_item, parent, false)
        return ProdViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdViewHolder, position: Int) {
        val prod = products[position]
        holder.bind(prod)

        val color = ContextCompat.getColor(holder.itemView.context, colors[position % colors.size])
        holder.itemView.findViewById<CardView>(R.id.materialCardView).setCardBackgroundColor(color)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("id", prod.id!!)
                putString("name", prod.name)
                putString("type", prod.type)
                putString("price", prod.price)
                putString("info", prod.info)
                putString("imgRes", prod.imageRes)
                putString("imgCat", prod.imageCat)
            }

            lifecycleScope.launch {
                val sameNameProducts = withContext(Dispatchers.IO) {
                    prodDao.getProductsByName(prod.name)
                }
                val differentSizeProducts = sameNameProducts.filter { it.info != prod.info }
                if (differentSizeProducts.isNotEmpty()) {
                    bundle.putString("sizeM", differentSizeProducts[0].info)
                }

                itemClickListener(bundle)
            }
        }


    }

    override fun getItemCount(): Int = products.size

    class ProdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(prod: ProdItem) {

            itemView.findViewById<TextView>(R.id.name).text = prod.name
            itemView.findViewById<TextView>(R.id.info).text = prod.info
            itemView.findViewById<TextView>(R.id.price).text = prod.price
//            itemView.findViewById<TextView>(R.id.size).text = prod.weight
            val imageView = itemView.findViewById<ImageView>(R.id.user_image)
            if (prod.imageRes != null) {
                val imageResId = itemView.context.resources.getIdentifier(
                    prod.imageRes,
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