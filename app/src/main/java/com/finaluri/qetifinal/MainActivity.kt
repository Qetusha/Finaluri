package com.finaluri.qetifinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finaluri.qetifinal.Module.InsertData
import com.finaluri.qetifinal.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.uiux.recyclerview.Module.Auth.SignUpData

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    companion object {
        const val INTENT_PARCELABLE = "OBJECT_INTENT"
    }

    private lateinit var recyclerView: RecyclerView

    private val lists = ArrayList<InsertData>()

    private lateinit var listadapter: com.finaluri.qetifinal.Adapter.ListAdapter

    private val auth = Firebase.auth
    private lateinit var database: DatabaseReference
    var storageRef = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageRef = FirebaseStorage.getInstance()

        binding.add.setOnClickListener {
            startActivity(Intent(this, Insert::class.java))
        }

        recyclerView = findViewById(R.id.recyclerList)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL,false)
        recyclerView.setHasFixedSize(true)

        listadapter = com.finaluri.qetifinal.Adapter.ListAdapter(this@MainActivity, lists) {}

        recyclerView.adapter = listadapter

        ProfileInfoFromFirebase()
        retrieveProductListFromFirebase()
    }

    private fun retrieveProductListFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("products")
        var listview = findViewById<ImageView>(R.id.listview)

        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lists.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(InsertData::class.java)
                    product?.let {
                        lists.add(product)
                    }
                }
               listadapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun ProfileInfoFromFirebase() {
        database = Firebase.database.reference
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("users")

        productRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(SignUpData::class.java) ?: return
                binding.mainUsername.text = user.username
                Glide.with(this@MainActivity)
                    .load(user.imageSrc)
                    .into(binding.user)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }
}