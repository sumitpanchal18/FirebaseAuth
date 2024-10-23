package com.example.firebasekotlin.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasekotlin.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnSignUp.setOnClickListener {
            signUpUser()
        }

        binding.tvRedirectLogin.setOnClickListener {
            finish()
        }

        binding.imgPhoneSignUP.setOnClickListener {
            startActivity(Intent(this, PhoneAuthActivity::class.java))
        }
        binding.imgGoogleSignUP.setOnClickListener {
            startActivity(Intent(this, GoogleAuthActivity::class.java))
//            GoogleAuthActivity().signInGoogle()
        }
        binding.imgFbSignUP.setOnClickListener {
            startActivity(Intent(this, FacebookAuth::class.java))
        }

        binding.imgGuestSignUP.setOnClickListener {
            signUpAnonymously()
        }
    }

    private fun signUpAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Signed in anonymously", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun signUpUser() {

        val email = binding.etSignUpEmailAddress.text.toString()
        val pass = binding.etSignUpPassword.text.toString()
        val conPass = binding.etSignUpConfirmPassword.text.toString()

        if (email.isBlank() || pass.isBlank() || conPass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != conPass) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}