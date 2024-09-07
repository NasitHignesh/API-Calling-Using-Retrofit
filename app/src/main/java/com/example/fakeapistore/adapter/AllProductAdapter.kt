package com.example.fakeapistore.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeapistore.R
import com.example.fakeapistore.activity.UpdateActivity
import com.example.fakeapistore.model.Rating
import com.example.fakeapistore.model.SingleProductData
import com.squareup.picasso.Picasso

class AllProductAdapter(private val context: Activity, private val arrayList: List<SingleProductData>) :
    RecyclerView.Adapter<AllProductAdapter.MyViewHolder>() {

    private lateinit var myListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var category: TextView = itemView.findViewById(R.id.Category)
        var title: TextView = itemView.findViewById(R.id.title)
        var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        var totalRating: TextView = itemView.findViewById(R.id.totalRating)
        var price: TextView = itemView.findViewById(R.id.price)
        var id: TextView = itemView.findViewById(R.id.id)

        var actionButton: ImageView = itemView.findViewById(R.id.actionImageView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false)
        return MyViewHolder(itemView).apply {
            itemView.setOnClickListener {
                myListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currItem = arrayList[position]
        val rat = (currItem.rating.rate * 2).toInt()

        Picasso.get().load(currItem.image).into(holder.imageView)
        holder.category.text = "Category: ${currItem.category}"
        holder.title.text = currItem.title
        holder.totalRating.text = "Rating: ${currItem.rating.count}"
        holder.price.text = "Price: ${currItem.price}"
        holder.id.text = "Id: ${currItem.id} "
        holder.ratingBar.setProgress(rat, true)

        holder.actionButton.setOnClickListener {
            popPop(currItem, holder.actionButton)
        }
    }

    private fun popPop(currItem: SingleProductData, actionButton: ImageView) {
        val popupMenu = PopupMenu(context, actionButton)
        popupMenu.menuInflater.inflate(R.menu.edit_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {

            // create object
            val product = SingleProductData(
                category = currItem.category,
                description = currItem.description,
                id = currItem.id,
                image = currItem.image,
                price = currItem.price,
                title = currItem.title,
                rating = Rating(rate = currItem.rating.rate, count = currItem.rating.count)
            )


            when (it.itemId) {
                R.id.edit -> {
                    val intent = Intent(context, UpdateActivity::class.java)
                    intent.putExtra("object", product)
                    context.startActivity(intent)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
    }

}
