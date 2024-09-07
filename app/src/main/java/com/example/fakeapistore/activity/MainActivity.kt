package com.example.fakeapistore.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeapistore.R
import com.example.fakeapistore.adapter.AllCategoriesAdapter
import com.example.fakeapistore.adapter.AllProductAdapter
import com.example.fakeapistore.interfaces.ApiInterface
import com.example.fakeapistore.model.AllCategoriesData
import com.example.fakeapistore.model.AllProductData
import com.example.fakeapistore.model.RetrofitClientInstance
import com.example.fakeapistore.model.SingleProductData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var allProductAdapter: AllProductAdapter
    private lateinit var recyclerViewHorizontal: RecyclerView
    private lateinit var allCategoriesAdapter: AllCategoriesAdapter
    private lateinit var filterImageButton: ImageButton
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var limitSpinner: Spinner

    lateinit var list: List<String>
    var retrofitBuilder: ApiInterface = RetrofitClientInstance.getRetrofitInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewHorizontal = findViewById(R.id.recyclerViewHorizontal)
        filterImageButton = findViewById(R.id.filterImageButton)
        floatingActionButton = findViewById(R.id.floatingActionButton)
        limitSpinner = findViewById(R.id.limitSpinner)

        // for data show in recyclerView first time
        val retrofitData = retrofitBuilder.getAllData()
        response(retrofitData)



        setSpinner()

        // for data show in recyclerView first time in AllCategories
        val retrofitData2 = retrofitBuilder.getAllCategories()
        responseHorizontal(retrofitData2)


        // add a new product
        floatingActionButton.setOnClickListener {
            val intent = Intent(this@MainActivity, AddProductActivity::class.java)
            startActivity(intent)
        }

        filterImageButton.setOnClickListener {
            sortFun(retrofitBuilder)
        }

    }

    private fun sortFun(retrofitBuilder: ApiInterface) {
        val popupMenu = PopupMenu(this, filterImageButton)
        popupMenu.menuInflater.inflate(R.menu.popup_menus, popupMenu.menu)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.increase -> {
                    val retrofitData = retrofitBuilder.getAllData()
                    response(retrofitData)
                    true
                }
                R.id.decrease -> {
                    val retrofitData = retrofitBuilder.getSortData()
                    response(retrofitData)
                    true
                }
                else -> false
            }
        }
    }

    fun response(retrofitData: Call<AllProductData>) {
        retrofitData.enqueue(object : Callback<AllProductData?> {
            override fun onResponse(call: Call<AllProductData?>, response: Response<AllProductData?>) {
                //  api is run successful run and get data
                val responseBody = response.body()
                if (responseBody == null) {
                    Toast.makeText(this@MainActivity, "Data not available", Toast.LENGTH_SHORT).show()
                } else {
                    val list: List<SingleProductData> = responseBody
                    allProductAdapter = AllProductAdapter(this@MainActivity, list)
                    recyclerView.adapter = allProductAdapter

                    allProductAdapter.setItemClickListener(object : AllProductAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            // passed id and start DetailActivity
                            val intent = Intent(this@MainActivity, DetailActivity::class.java)
                            intent.putExtra("getSingleData", position + 1)
                            startActivity(intent)
                        }
                    })
                }

            }

            override fun onFailure(call: Call<AllProductData?>, t: Throwable) {
                // api not get data
                Toast.makeText(this@MainActivity, "Not get data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun responseHorizontal(retrofitData: Call<AllCategoriesData>) {
        retrofitData.enqueue(object : Callback<AllCategoriesData?> {
            override fun onResponse(call: Call<AllCategoriesData?>, response: Response<AllCategoriesData?>) {
                //  api is run successful run and get data
                val responseBody = response.body()
                if (responseBody == null) {
                    Toast.makeText(this@MainActivity, "Data not available", Toast.LENGTH_SHORT).show()
                } else {
                    list = responseBody
                    allCategoriesAdapter = AllCategoriesAdapter(this@MainActivity, list)
                    recyclerViewHorizontal.adapter = allCategoriesAdapter

                    allCategoriesAdapter.setItemClickListener(object : AllCategoriesAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            // Handle item click here
                            val str = list[position]
                            val retrofitData = retrofitBuilder?.getCategoryData(str)
                            if (retrofitData != null) {
                                response(retrofitData)
                            }
                        }
                    })
                }
            }

            override fun onFailure(call: Call<AllCategoriesData?>, t: Throwable) {
                // api not get data
                Toast.makeText(this@MainActivity, "Not get data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter.createFromResource(this@MainActivity, R.array.limit_list, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        limitSpinner.adapter = adapter
        limitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                // this data variable is selected item data
                val data = adapterView?.getItemAtPosition(i).toString()

                val retrofitDataForLimit = retrofitBuilder.getLimitData(data.toInt())
                response(retrofitDataForLimit)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle nothing selected event
            }
        }
        limitSpinner.setSelection(3)
    }

}

