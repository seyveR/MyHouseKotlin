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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myhousev3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatAdapter(private val catDao: CatDao, private val lifecycleScope: LifecycleCoroutineScope,
                  private val itemClickListener: (Bundle) -> Unit,
                ) : RecyclerView.Adapter<CatAdapter.CatViewHolder>() {
    private var categories: List<CatItem> = listOf()

    private var colors: List<Int> = listOf(
        R.color.catColor1, R.color.catColor2, R.color.catColor3,
        R.color.catColor4, R.color.catColor5, R.color.catColor6 )

    fun setCat(cat: List<CatItem>) {
        this.categories = cat
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            itemCount - 1 -> 5  // последний элемент
            else -> if (position % 2 == 0) 1 else 2 // здесь выберите, как вы хотите чередовать другие элементы
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val view = when(viewType) {
            0 -> LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)
            1 -> LayoutInflater.from(parent.context).inflate(R.layout.cat_item_large, parent, false)
            2 -> LayoutInflater.from(parent.context).inflate(R.layout.cat_item_small, parent, false)
            3 -> LayoutInflater.from(parent.context).inflate(R.layout.cat_item_large, parent, false)
            4 -> LayoutInflater.from(parent.context).inflate(R.layout.cat_item_small, parent, false)
            5 -> LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)
            else -> {
                LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)}
        }
        return CatViewHolder(view, itemClickListener)
    }



    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val cat = categories[position]
        holder.bind(cat)

        val color = ContextCompat.getColor(holder.itemView.context, colors[position % colors.size])
        (holder.itemView as CardView).setCardBackgroundColor(color)


    }

    override fun getItemCount(): Int = categories.size

    inner class CatViewHolder(itemView: View,private val itemClickListener: (Bundle) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(cat: CatItem) {

            itemView.findViewById<TextView>(R.id.name).text = cat.name
            val imageView = itemView.findViewById<ImageView>(R.id.imgRes)
            if (cat.imageRes != null) {
                val imageResId = itemView.context.resources.getIdentifier(
                    cat.imageRes,
                    "drawable",
                    itemView.context.packageName
                )
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.cleaning)
            }

            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("cat_name", cat.name)
                }
                itemClickListener(bundle)
            }

        }
    }
}