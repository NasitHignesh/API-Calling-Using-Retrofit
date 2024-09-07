package com.example.fakeapistore.interfaces

import com.example.fakeapistore.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    // get all data
    @GET("products")
    fun getAllData(): Call<AllProductData>

    // get a single product
    @GET("products/{id}")
    fun getSingleData(@Path("id") id: Int): Call<SingleProductData>

    // get all categories
    @GET("products/categories")
    fun getAllCategories(): Call<AllCategoriesData>

    // get data for category wise
    @GET("products/category/{categoryName}")
    fun getCategoryData(@Path("categoryName") categoryName: String): Call<AllProductData>

    // for get limit data
    @GET("products")
    fun getLimitData(@Query("limit") id: Int): Call<AllProductData>

    // sort api
    @GET("products")
    fun getSortData(@Query("sort") sort: String = "desc"): Call<AllProductData>

    // add new data
    @POST("products")
    fun postProduct(@Body product:Product): Call<Product>

    //Delete data
    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") id: Int): Call<Void>

    // update product
    @PUT("products/{id}")
    fun updateProduct(@Path("id") id: Int , @Body updateProduct: UpdateProduct): Call<UpdateProduct>

}