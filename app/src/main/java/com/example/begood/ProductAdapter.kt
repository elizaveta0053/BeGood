package com.example.begood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnFavorite: ImageView = view.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvTitle.text = product.title
        holder.tvRating.text = product.rating.toString()
        holder.tvLocation.text = product.location
        holder.tvStatus.text = product.status
        holder.imgProduct.setImageResource(product.imageResId)
        
        val favoriteIcon = if (product.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        holder.btnFavorite.setImageResource(favoriteIcon)
    }

    override fun getItemCount(): Int = products.size
}
