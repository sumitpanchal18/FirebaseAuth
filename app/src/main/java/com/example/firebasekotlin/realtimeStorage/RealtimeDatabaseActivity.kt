package com.example.firebasekotlin.realtimeStorage

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasekotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class RealtimeDatabaseActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userIdEditText: EditText
    private lateinit var userNameEditText: EditText
    private lateinit var userAgeEditText: EditText
    private lateinit var userListTextView: TextView
    private lateinit var addButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime_database)

        database = FirebaseDatabase.getInstance().getReference("users")

        userIdEditText = findViewById(R.id.userIdEditText)
        userNameEditText = findViewById(R.id.userNameEditText)
        userAgeEditText = findViewById(R.id.userAgeEditText)
        userListTextView = findViewById(R.id.userListTextView)
        addButton = findViewById(R.id.addButton)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)

        addButton.setOnClickListener { addUser() }
        updateButton.setOnClickListener { updateUser() }
        deleteButton.setOnClickListener { deleteUser() }

        getUsers()
    }

    private fun addUser() {
        val userId = database.push().key ?: return
        val name = userNameEditText.text.toString()
        val age = userAgeEditText.text.toString().toIntOrNull() ?: return

        val user = User(userId, name, age)

        database.child(userId).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show()
                clearFields()
                getUsers()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUsers() {
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = StringBuilder()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        usersList.append("ID: ${it.id}, Name: ${it.name}, Age: ${it.age}\n")
                    }
                }
                userListTextView.text = usersList.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@RealtimeDatabaseActivity,
                    "Failed to load users",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateUser() {
        val userId = userIdEditText.text.toString()
        val name = userNameEditText.text.toString()
        val age = userAgeEditText.text.toString().toIntOrNull()

        if (name.isEmpty() || age == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userUpdates = mapOf("name" to name, "age" to age)

        database.child(userId).updateChildren(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show()
                clearFields()
                getUsers()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteUser() {
        val userId = userIdEditText.text.toString()

        if (userId.isEmpty()) {
            Toast.makeText(this, "Please enter a user ID", Toast.LENGTH_SHORT).show()
            return
        }

        database.child(userId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
                clearFields()
                getUsers()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        userIdEditText.text.clear()
        userNameEditText.text.clear()
        userAgeEditText.text.clear()
    }
}
