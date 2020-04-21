package com.google.firebase.ml.md.kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ml.md.R


class HomeActivity : AppCompatActivity() {

    var fbAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


            imageButton6.setOnClickListener {
                   startActivity(Intent(this, Profile::class.java))
                    finish()
            }
            imageButton4.setOnClickListener {
                   startActivity(Intent(this, LiveBarcodeScanningActivity::class.java))
                finish()
            }
             imageButton5.setOnClickListener {
            startActivity(Intent(this, History::class.java))
            finish()
            }

             imageButton3.setOnClickListener {
            startActivity(Intent(this, Rewardsactivity::class.java))
            finish()
             }

        btn_signout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            fbAuth.signOut()
            finish()
        }

    }
}




