package com.example.firebasekotlin.storage

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firebasekotlin.databinding.ActivityStorageBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageActivity : AppCompatActivity() {

    private lateinit var storageRef: StorageReference
    private lateinit var binding: ActivityStorageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance().reference

        binding.uploadButton.setOnClickListener {
            selectFileToUpload()
        }

        binding.fetchButton.setOnClickListener {
            fetchImage("uploads/image_33")
        }
    }

    private val selectFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploadFile(it)
        } ?: run {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectFileToUpload() {
        selectFileLauncher.launch("image/*")
    }

    private fun uploadFile(fileUri: Uri) {
        val safeFileName = fileUri.lastPathSegment?.replace(":", "_")
        val fileRef = storageRef.child("uploads/$safeFileName")

        val uploadTask = fileRef.putFile(fileUri)

        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
            Log.d("StorageActivity", "Uploaded file path: ${fileRef.path}")
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchImage(imagePath: String) {
        val fileRef = storageRef.child(imagePath)

        fileRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .into(binding.imageViewFetch)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch image: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
