package com.example.fakeapistore.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fakeapistore.R
import com.example.fakeapistore.interfaces.ApiInterface
import com.example.fakeapistore.model.SingleProductData
import com.example.fakeapistore.model.RetrofitClientInstance
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var deleteImageButton: ImageButton
    private lateinit var categoryView: TextView
    private lateinit var titleView: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var totalRatingView: TextView
    private lateinit var priceView: TextView
    private lateinit var idView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var ratingBarView: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        imageView = findViewById(R.id.imageView)
        deleteImageButton = findViewById(R.id.deleteImageButton)
        categoryView = findViewById(R.id.categoryView)
        titleView = findViewById(R.id.titleView)
        ratingTextView = findViewById(R.id.ratingTextView)
        totalRatingView = findViewById(R.id.totalRatingView)
        priceView = findViewById(R.id.priceView)
        idView = findViewById(R.id.idView)
        descriptionView = findViewById(R.id.descriptionView)
        ratingBarView = findViewById(R.id.ratingBarView)


        // for passing id get
        val pId: Int = intent.getIntExtra("getSingleData", 1)

        var retrofitBuilder: ApiInterface = RetrofitClientInstance.getRetrofitInstance()
        val retrofitData = retrofitBuilder.getSingleData(pId)
        response(retrofitData)

        deleteImageButton.setOnClickListener {
            val retrofitData = retrofitBuilder.deleteProduct(pId)
            deleteResponse(retrofitData)
        }
    }

    private fun deleteResponse(retrofitData: Call<Void>) {
        retrofitData.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                // for checking api is all or not
                Log.d("post", "$response")

                Toast.makeText(this@DetailActivity, "Product Delete successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@DetailActivity, MainActivity::class.java)
                startActivity(intent)

            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                // api not get data
                Toast.makeText(this@DetailActivity, "Not get data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun response(retrofitData: Call<SingleProductData>) {
        retrofitData.enqueue(object : Callback<SingleProductData?> {
            override fun onResponse(call: Call<SingleProductData?>, response: Response<SingleProductData?>) {
                //  api is run successful run and get data
                val responseBody = response.body()

                if (responseBody == null) {
                    Toast.makeText(this@DetailActivity, "Data not available", Toast.LENGTH_SHORT).show()
                } else {
                    // for checking api is all or not
                    Log.d("post", "$response")

                    val singleItem: SingleProductData = responseBody

                    // for null checking
                    if (singleItem != null) {
                        // for rating bar
                        var rat = (singleItem.rating.rate * 2).toInt()

                        // for set values
                        Picasso.get().load(singleItem.image).into(imageView)
                        categoryView.text = "Category : " + singleItem.category
                        titleView.text = singleItem.title
                        ratingTextView.text = singleItem.rating.rate.toString()
                        totalRatingView.text = "Rating : " + singleItem.rating.count.toString()
                        priceView.text = "Price : $" + singleItem.price.toString()
                        idView.text = "Product Id : " + singleItem.id.toString()
                        descriptionView.text = "Description : " + singleItem.description
                        ratingBarView.setProgress(rat, true)
                    }
                }
            }

            override fun onFailure(call: Call<SingleProductData?>, t: Throwable) {
                // api not get data
                Toast.makeText(this@DetailActivity, "Not get data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}