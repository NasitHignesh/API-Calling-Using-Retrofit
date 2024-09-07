package com.example.fakeapistore.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fakeapistore.R
import com.example.fakeapistore.interfaces.ApiInterface
import com.example.fakeapistore.model.RetrofitClientInstance
import com.example.fakeapistore.model.SingleProductData
import com.example.fakeapistore.model.UpdateProduct
import com.squareup.picasso.Picasso
import retrofit2.Call

class UpdateActivity : AppCompatActivity() {

    private lateinit var updateTextTitle: TextView
    private lateinit var updateTextPrice: TextView
    private lateinit var updateTextDescription: TextView
    private lateinit var updateTextRate: TextView
    private lateinit var updateTextCount: TextView
    private lateinit var updateSpinner: Spinner
    private lateinit var updateDemoImages: ImageView
    private lateinit var updatePicImages: Button
    private lateinit var updateProductButton: Button
    private lateinit var updateShowAlert: CardView
    private lateinit var updateCameraOption: ImageView
    private lateinit var updateGalleryOption: ImageView
    private lateinit var updateAlertCancelButton: Button
    private lateinit var category: String
    private var PERMISSION_GALLERY_CODE = 1001
    private var PERMISSION_CODE_CAMERA = 1002


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val obj = if (intent != null) {
            intent.getSerializableExtra("object") as? SingleProductData
        } else {
            Toast.makeText(this@UpdateActivity, "data not get null", Toast.LENGTH_SHORT).show()
            null
        }

        findId()
        setOldValues(obj)
        updatePicImages.setOnClickListener {
            showAlert()
        }
        updateProductButton.setOnClickListener {
            updateProductFun(obj)
        }


    }

    private fun updateProductFun(obj: SingleProductData?) {
        val retrofitBuilder: ApiInterface = RetrofitClientInstance.getRetrofitInstance()
        // create object
        var updateProductObject = UpdateProduct(
            title = updateTextTitle.text.toString(),
            price = updateTextPrice.text.toString().toDouble(),
            description = updateTextDescription.text.toString(),
            image = updateDemoImages.toString(),
            category = category
        )

        var ids: Int? = obj?.id
        val call = ids?.let { retrofitBuilder.updateProduct(it, updateProductObject) }
        call?.enqueue(object : retrofit2.Callback<UpdateProduct> {
            // api is fire successful
            override fun onResponse(call: Call<UpdateProduct>, response: retrofit2.Response<UpdateProduct>) {
                if (response.isSuccessful) {
                    // Request successful
                    Toast.makeText(this@UpdateActivity, "Api successful call", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@UpdateActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Handle error
                    Toast.makeText(this@UpdateActivity, "Api Response not isSuccessful", Toast.LENGTH_SHORT).show()
                }
            }

            // api is fire failed
            override fun onFailure(call: Call<UpdateProduct>, t: Throwable) {
                // Handle failure
                Toast.makeText(this@UpdateActivity, "Handle failure", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAlert() {
        // alert show karva mate
        updateShowAlert.visibility = View.VISIBLE
        updateCameraOption.setOnClickListener {
            // camera na option par click thay tyare alert ne hide karva mate
            updateShowAlert.visibility = View.INVISIBLE
            // Permission se ke nay a chek karva mate
            checkCameraPermissionAndStartActivity(PERMISSION_CODE_CAMERA)
        }
        updateGalleryOption.setOnClickListener {
            // gallery na option par click thya tyare tyare alert ne hide karva mate
            updateShowAlert.visibility = View.INVISIBLE
            // image pic in gallery
            val bGallery = Intent(Intent.ACTION_PICK)
            bGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(bGallery, PERMISSION_GALLERY_CODE)
        }
        // when click a cancel alert
        updateAlertCancelButton.setOnClickListener {
            updateShowAlert.visibility = View.INVISIBLE
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
            //gallery option hoy tyare
            if (requestCode == PERMISSION_GALLERY_CODE) {
                if (data != null) {
                    updateDemoImages.setImageURI(data.data)
                }
            }
            //camera option hoy tyare
            else if (requestCode == PERMISSION_CODE_CAMERA) {
                if (data != null) {
                    val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
                    updateDemoImages.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun setOldValues(obj: SingleProductData?) {
        if (obj != null) {
            updateTextTitle.text = obj.title
            updateTextPrice.text = obj.price.toString()
            updateTextDescription.text = obj.description
            updateTextRate.text = obj.rating.rate.toString()
            updateTextCount.text = obj.rating.count.toString()
            // for old images set -
            Picasso.get().load(obj.image).into(updateDemoImages)
        }
        setSpinner(obj)
    }

    private fun findId() {
        updateTextTitle = findViewById(R.id.updateTextTitle)
        updateTextPrice = findViewById(R.id.updateTextPrice)
        updateTextDescription = findViewById(R.id.updateTextDescription)
        updateTextRate = findViewById(R.id.updateTextRate)
        updateTextCount = findViewById(R.id.updateTextCount)
        updateSpinner = findViewById(R.id.updateSpinner)
        updateDemoImages = findViewById(R.id.updateDemoImages)
        updatePicImages = findViewById(R.id.updatePicImages)
        updateProductButton = findViewById(R.id.updateProductButton)
        updateShowAlert = findViewById(R.id.updateShowAlert)
        updateCameraOption = findViewById(R.id.updateCameraOption)
        updateGalleryOption = findViewById(R.id.updateGalleryOption)
        updateAlertCancelButton = findViewById(R.id.updateAlertCancelButton)
    }

    private fun setSpinner(obj: SingleProductData?) {
        val adapter = ArrayAdapter.createFromResource(this@UpdateActivity, R.array.spinner_list, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        updateSpinner.adapter = adapter
        updateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                // this data variable is selected item data
                val data = adapterView?.getItemAtPosition(i).toString()
                category = data
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle nothing selected event
            }
        }

        // foe set old categories
        var defaultSelectionIndex = 0
        val list = listOf<String>("electronics", "jewelery", "men's clothing", "women's clothing")
        for (x in list.indices) {
            if (obj != null) {
                if (list[x] == obj.category) {
                    defaultSelectionIndex = x
                    break
                }
            }
        }
        updateSpinner.setSelection(defaultSelectionIndex)
    }
}