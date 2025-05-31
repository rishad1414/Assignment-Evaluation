package com.example.assignmentevaluation.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityTeacherSignUpBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Teacher
import java.io.ByteArrayOutputStream

class TeacherSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherSignUpBinding
    lateinit var sp : SP


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTeacherSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSpinnerValue()
        sp = SP(this)



        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.scrollTo(0, binding.password.bottom)
            }
        }

        binding.cpassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.scrollTo(0, binding.cpassword.bottom)
            }
        }

        binding.userType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parentView?.getItemAtPosition(position).toString()

                if(selectedItem=="Student"){
                    startActivity(Intent(this@TeacherSignUpActivity,StudentSignUpActivity::class.java))
                    finish()
                }

            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

         binding.goToLogin.setOnClickListener{
             startActivity(Intent(this@TeacherSignUpActivity,LoginActivity::class.java))
             finish()
         }

        binding.signup.setOnClickListener {
          signUp()
        }






    }

    private fun signUp() {
        val name = binding.name.text.toString().trim()
        val email = binding.editemail.text.toString().trim()
        val dept = binding.dept.selectedItem.toString()
        val des = binding.des.text.toString().trim()
        val password = binding.password.text.toString()
        val cpassword = binding.cpassword.text.toString()

        val defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.profile)
        val profilePicBytes = imageToByteArray(defaultBitmap)

        if (name.isEmpty() || email.isEmpty() || des.isEmpty() || password.isEmpty() || cpassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
        } else if (password != cpassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            val db = MyDatabaseHelper(this)

            if (db.isEmailExists(email)) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                return
            }

            val teacher = Teacher(
                teacherId = System.currentTimeMillis().toString(),
                name = name,
                dept = dept,
                des = des,
                email = email,
                profilePic= profilePicBytes,
                password = password
            )

            val success = db.insertTeacher(teacher)

            if (success) {
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@TeacherSignUpActivity,LoginActivity::class.java))
                finish()

            } else {
                Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun imageToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }


    private fun setSpinnerValue() {
        ArrayAdapter.createFromResource(
            this,
            R.array.department_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.dept.adapter = adapter
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.user_array_t,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.userType.adapter = adapter
        }
    }
}