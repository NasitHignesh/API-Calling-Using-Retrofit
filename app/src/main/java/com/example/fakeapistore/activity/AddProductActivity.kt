package com.example.fakeapistore.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fakeapistore.R
import com.example.fakeapistore.interfaces.ApiInterface
import com.example.fakeapistore.model.Product
import com.example.fakeapistore.model.Rating
import com.example.fakeapistore.model.RetrofitClientInstance
import retrofit2.Call

class AddProductActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var addProductButton: Button
    private lateinit var picImages: Button
    private lateinit var editTextTitle: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextRate: EditText
    private lateinit var editTextCount: EditText
    private lateinit var hiddenLinearLayout: LinearLayout
    private lateinit var inputLinearLayout: LinearLayout
    private lateinit var category1: String
    private lateinit var cameraOption: ImageView
    private lateinit var galleryOption: ImageView
    private lateinit var image: ImageView
    private lateinit var demoImages: ImageView
    private lateinit var showAlert: CardView
    private lateinit var alertCancelButton: Button
    private var PERMISSION_GALLERY_CODE = 1001
    private var PERMISSION_CODE_CAMERA = 1002


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        // find by its id
        spinner = findViewById(R.id.spinner)
        addProductButton = findViewById(R.id.addProductButton)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextPrice = findViewById(R.id.editTextPrice)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextRate = findViewById(R.id.editTextRate)
        editTextCount = findViewById(R.id.editTextCount)
        hiddenLinearLayout = findViewById(R.id.hiddenLinearLayout)
        inputLinearLayout = findViewById(R.id.linearLayout4)
        picImages = findViewById(R.id.picImages)
        cameraOption = findViewById(R.id.cameraOption)
        galleryOption = findViewById(R.id.galleryOption)
        showAlert = findViewById(R.id.showAlert)
        alertCancelButton = findViewById(R.id.alertCancelButton)
        image = findViewById(R.id.imageView)
        demoImages = findViewById(R.id.demoImages)

        setSpinner()

        picImages.setOnClickListener {
            showAlert()
        }

        addProductButton.setOnClickListener {
            addProduct()
        }

    }

    private fun showAlert() {

        // alert show karva mate
        showAlert.visibility = View.VISIBLE

        cameraOption.setOnClickListener {
            // camera na option par click thay tyare alert ne hide karva mate
            showAlert.visibility = View.INVISIBLE

            // Permission se ke nay a chek karva mate
            checkCameraPermissionAndStartActivity(PERMISSION_CODE_CAMERA)
        }

        galleryOption.setOnClickListener {
            // gallery na option par click thya tyare tyare alert ne hide karva mate
            showAlert.visibility = View.INVISIBLE

            // image pic in gallery
            val bGallery = Intent(Intent.ACTION_PICK)
            bGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(bGallery, PERMISSION_GALLERY_CODE)
        }

        // when click a cancel alert
        alertCancelButton.setOnClickListener {
            showAlert.visibility = View.INVISIBLE
        }

    }

    private fun checkCameraPermissionAndStartActivity(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Camera permission already allow, proceed with camera operation
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, requestCode)
        } else {
            // Camera permission not allow, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            // profile pic hoy and gallery option hoy tyare
            if (requestCode == PERMISSION_GALLERY_CODE) {
                if (data != null) {
                    image.setImageURI(data.data)
                    demoImages.setImageURI(data.data)
                }
            }
            // profile pic hoy and camera option hoy tyare
            else if (requestCode == PERMISSION_CODE_CAMERA) {
                if (data != null) {
                    val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
                    image.setImageBitmap(bitmap)
                    demoImages.setImageBitmap(bitmap)
                }
            }
        }

    }

    private fun addProduct() {
        val retrofitBuilder: ApiInterface = RetrofitClientInstance.getRetrofitInstance()

        // for store input value
        val titles = editTextTitle.text.toString()
        val prices = editTextPrice.text.toString().toDoubleOrNull()
        val description = editTextDescription.text.toString()
        val rate = editTextRate.text.toString().toDoubleOrNull()
        val counts = editTextCount.text.toString().toInt()
        val img = demoImages.toString()

        // create object
        val product = Product(
            title = titles,
            price = prices!!,
            description = description,
            category = category1,
            image = img,
            rating = Rating(rate = rate!!, count = counts)
        )

        // call the api
        val call = retrofitBuilder.postProduct(product)

//         for response
        call.enqueue(object : retrofit2.Callback<Product> {
            // api is fire successful
            override fun onResponse(call: Call<Product>, response: retrofit2.Response<Product>) {

                if (response.isSuccessful) {
                    // Request successful
                    Toast.makeText(this@AddProductActivity, "Api successful call", Toast.LENGTH_SHORT).show()

                    // for checking api is all or not
                    Log.d("post","$response")

                    // find field in demo
                    val category: TextView = findViewById(R.id.Category)
                    val title: TextView = findViewById(R.id.title)
                    val ratingBar: RatingBar = findViewById(R.id.ratingBar)
                    val totalRating: TextView = findViewById(R.id.totalRating)
                    val price: TextView = findViewById(R.id.price)
                    val rat = (rate * 2).toInt()

                    // set values in demo
                    category.text = "Category:  $category1"
                    title.text = titles
                    totalRating.text = "Rating: $counts"
                    price.text = "Price: $prices"
                    ratingBar.setProgress(rat, true)

                    // handle input all field hide and demo show
                    inputLinearLayout.visibility = View.INVISIBLE
                    hiddenLinearLayout.visibility = View.VISIBLE

                } else {
                    // Handle error
                    Toast.makeText(this@AddProductActivity, "Handle error", Toast.LENGTH_SHORT).show()
                }
            }

            // api is fire failed
            override fun onFailure(call: Call<Product>, t: Throwable) {
                // Handle failure
                Toast.makeText(this@AddProductActivity, "Handle failure", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter.createFromResource(this@AddProductActivity, R.array.spinner_list, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                // this data variable is selected item data
                val data = adapterView?.getItemAtPosition(i).toString()
                category1 = data
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle nothing selected event
            }
        }
    }
}