package com.google.firebase.ml.md.kotlin


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ml.md.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mDatabase:DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        imageButton4.setOnClickListener {
            val intent = Intent(baseContext, LoginActivity::class.java)
            startActivity(intent)
        }


        btn_sign_up.setOnClickListener {
            signUpUser()

        }


    }
    private fun signUpUser(){

        if(tv_username.text.toString().isEmpty()){
            tv_username.error="Please enter email"
            tv_username.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()){
            tv_username.error = "Please enter valid email"
            tv_username.requestFocus()
            return
        }

        if(tv_password.text.toString().isEmpty()){
            tv_password.error = "Please enter password"
            tv_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        startActivity(Intent(this,LoginActivity::class.java))
                                        finish()
                                    }
                                }

                    } else {
                        Toast.makeText(baseContext, "Sign Up failed. Try again after some time",
                                Toast.LENGTH_SHORT).show()
                    }


                }

    }

}
