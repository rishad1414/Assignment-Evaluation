package com.example.assignmentevaluation.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityLoginBinding
import com.example.assignmentevaluation.databinding.ActivityStudentSignUpBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var sp : SP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)

        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.scrollTo(0, binding.password.bottom)
            }
        }

        binding.goToSignUp.setOnClickListener{
            startActivity(Intent(this@LoginActivity,TeacherSignUpActivity::class.java))
            finish()
        }

        binding.login.setOnClickListener {
            login()
        }




    }



    private fun login() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            val db = MyDatabaseHelper(this)

            val student = db.getStudentByEmail(email)
            val teacher = db.getTeacherByEmail(email)

            when {
                student != null && student.password == password -> {
                    sp.addB("isLogin", true)
                    sp.addS("loginEmail", email)
                    sp.addS("userType", "student")
                    sp.addS("studentId", student.studentId.toString())

                    val intent = Intent(this, StudentHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                teacher != null && teacher.password == password -> {
                    sp.addB("isLogin", true)
                    sp.addS("loginEmail", email)
                    sp.addS("userType", "teacher")
                    sp.addS("teacherId", teacher.teacherId.toString())

                    val intent = Intent(this, TeacherHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                student == null && teacher == null -> {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}