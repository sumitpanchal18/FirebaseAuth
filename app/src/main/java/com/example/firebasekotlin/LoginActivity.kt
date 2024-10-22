package com.example.firebasekotlin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasekotlin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRedirectSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.imgPhoneLogin.setOnClickListener {
            startActivity(Intent(this, PhoneAuthActivity::class.java))
        }
        binding.imgGoogleLogin.setOnClickListener {
            startActivity(Intent(this, GoogleAuthActivity::class.java))
//            GoogleAuthActivity().signInGoogle()
        }
        binding.imgFbLogin.setOnClickListener {
            startActivity(Intent(this, FacebookAuth::class.java))
        }

        binding.imgGuestLogin.setOnClickListener {
            signInAnonymously()
        }

        auth = FirebaseAuth.getInstance()
    }

    private fun loginUser() {
        val email = binding.etEmailAddress.text.toString()
        val pass = binding.etPassword.text.toString()
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val uid = user.uid
                        Log.d(TAG, "User Id : $uid")
                    }
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Signed in anonymously", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}