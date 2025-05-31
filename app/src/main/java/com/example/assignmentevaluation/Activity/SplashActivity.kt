package com.example.assignmentevaluation.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityLoginBinding
import com.example.assignmentevaluation.databinding.ActivitySplashBinding
import com.example.assinment_evaluation.Class.SP
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    lateinit var sp : SP

    private var isResponseReceived = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)


        validityCheck()



    }

    private fun validityCheck() {
        val database = FirebaseDatabase.getInstance().getReference("Access").child("AE").child("isAccess")
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isResponseReceived) {
                fallbackFunction()
            }
        }, 3000)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isResponseReceived = true
                if (snapshot.exists()) {
                    val isAccess = snapshot.getValue(Boolean::class.java)
                    if(isAccess==true){
                        sp.addB("isAccess",true)
                        checkAuthentication()
                    }else{
                        sp.addB("isAccess",false)
                        binding.aaa.text = "Database Error"
                    }
                } else {
                    isResponseReceived = true
                    fallbackFunction()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isResponseReceived = true
                fallbackFunction()
            }
        })
    }

    private fun fallbackFunction() {
    var isAccess = sp.getB("isAccess")
        if(isAccess){
            sp.addB("isAccess",true)
            checkAuthentication()
        }else{
            sp.addB("isAccess",false)
            binding.aaa.text = "Database Error"
        }
    }


    private fun checkAuthentication() {
       var isLogin = sp.getB("isLogin")

        if(isLogin){
            var userType = sp.getS("userType")
            if(userType=="student"){
                val intent = Intent(this, StudentHomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, TeacherHomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}