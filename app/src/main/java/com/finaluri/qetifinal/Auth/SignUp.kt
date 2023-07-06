package com.finaluri.qetifinal.Auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.finaluri.qetifinal.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.uiux.recyclerview.Module.Auth.SignUpData

class SignUp:AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var storageRef = Firebase.storage
    private lateinit var imageUri: Uri

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        binding.imageView.setOnClickListener {
            selectImage()
        }

        saveUser()

        binding.gosignin.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }


    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SignUp.REQUEST_IMAGE_PICK)
    }

    private fun saveUser() {
        binding.submit.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val pass = binding.pasword.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (::imageUri.isInitialized) {
                            val database = Firebase.database
                            val userRef = database.getReference("users")

                            val user = SignUpData(
                                auth.uid.toString(),
                                null,
                                username,
                                email
                            ) // Update the ProductData object

                            if (auth.uid.toString() != null) {
                                val storage = FirebaseStorage.getInstance()
                                val storageRef =
                                    storage.reference.child("users_images").child(auth.uid.toString())

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
                                        user.imageSrc = downloadUri.toString()

                                        val userMap = HashMap<String, Any>()
                                        userMap["productId"] = user.productId!!
                                        userMap["imageSrc"] = user.imageSrc!!
                                        userMap["username"] = user.username!!
                                        userMap["email"] = user.email!!

                                        userRef.child(auth.uid.toString()).setValue(userMap)
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
                            startActivity(Intent(this,SignIn::class.java))
                        } else {
                            // Display an error message for missing image
                            Toast.makeText(this, "გთხოვთ აირჩიოთ სურათი", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "ცარიელი ველები არ არის დაშვებული !!", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!
            binding.imageView.setImageURI(imageUri)
        }
    }

    companion object {
        const val REQUEST_IMAGE_PICK = 1
    }
}