package com.example.fakeapistore.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeapistore.R
import com.google.android.material.chip.Chip

class AllCategoriesAdapter(private val context: Activity, private var categoriesList: List<String>) : RecyclerView.Adapter<AllCategoriesAdapter.CategoriesViewHolder>() {
    private lateinit var myListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chip: Chip = itemView.findViewById(R.id.chip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.horizontal_item_view, parent, false)
        return CategoriesViewHolder(itemView).apply {
            itemView.setOnClickListener {
                myListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val currItem = categoriesList[position]
        holder.chip.text = currItem
    }

}