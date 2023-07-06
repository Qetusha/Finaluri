package com.finaluri.qetifinal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.finaluri.qetifinal.Module.InsertData
import com.finaluri.qetifinal.databinding.ActivityInsertBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class Insert : AppCompatActivity() {

    private lateinit var binding : ActivityInsertBinding

    private lateinit var nameEditText: EditText
    private lateinit var imageImageView: ImageView

    private lateinit var saveButton: Button

    private lateinit var imageUri: Uri

    private val auth = Firebase.auth

    private lateinit var database: DatabaseReference
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameEditText = findViewById(R.id.addtitle)
        imageImageView = findViewById(R.id.addimg)

        saveButton = findViewById(R.id.button)

        imageImageView.setOnClickListener {
            selectImage()
        }

        saveButton.setOnClickListener {
            saveProduct()
        }

        binding.addimg.setOnClickListener {
            selectImage()
        }

    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun saveProduct() {

    val nametitle = binding.addtitle.text.toString().trim()

    if (nametitle.isNotEmpty()) {
            if (::imageUri.isInitialized) {
                val database = Firebase.database
                //val database = FirebaseDatabase.getInstance()
                val insertRef = database.getReference("products")
                val insertId = insertRef.push().key

                val product = InsertData(
                    auth.uid.toString(),
                    null,
                    nametitle
                ) // Update the ProductData object

                if (insertId != null) {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference.child("insert_images").child(insertId)

                    val uploadTask = storageRef.putFile(imageUri)
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        storageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            product.imageSrc = downloadUri.toString()

                            val productMap = HashMap<String, Any>()
                            productMap["productId"] = product.productId!!
                            productMap["imageSrc"] = product.imageSrc!!
                            productMap["nametitle"] = product.nametitle!!

                            insertRef.child(insertId).setValue(productMap)
                                .addOnSuccessListener {
                                    // Product saved successfully
                                    finish()
                                }
                                .addOnFailureListener {
                                    // Handle the failure
                                }
                        } else {
                            // Handle the failure
                        }
                    }
                }
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Display an error message for missing image
                Toast.makeText(this, "გთხოვთ აირჩიოთ სურათი", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(this, "შენახულია", Toast.LENGTH_SHORT).show()
    } else {
        // Display an error message for missing fields
        Toast.makeText(this, "გთხოვთ შეავსოვთ გამოვტოვებული ველი", Toast.LENGTH_SHORT).show()
    }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!
            imageImageView.setImageURI(imageUri)
        }
    }

    companion object {
        const val REQUEST_IMAGE_PICK = 1
    }
}