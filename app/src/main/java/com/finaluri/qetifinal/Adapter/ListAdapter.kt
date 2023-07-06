package com.finaluri.qetifinal.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.finaluri.qetifinal.Module.InsertData
import com.finaluri.qetifinal.R

@GlideModule
class ListAdapter(
    private val context: Context,
    private val products: ArrayList<InsertData>,
    private val listener: (InsertData) -> Unit
) : RecyclerView.Adapter<ListAdapter.MainProductViewHolder>() {

    class MainProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageSrc: ImageView = itemView.findViewById(R.id.listview)
        private val nametitle: TextView = itemView.findViewById(R.id.nametitle)

        fun bindView(product: InsertData, listener: (InsertData) -> Unit) {
            Glide.with(itemView)
                .load(product.imageSrc)
                .into(imageSrc)

            nametitle.text = product.nametitle
            itemView.setOnClickListener { listener(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list, parent, false)
        return MainProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainProductViewHolder, position: Int) {
        val product = products[position]
        holder.bindView(product, listener)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}