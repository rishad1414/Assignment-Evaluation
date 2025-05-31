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
import com.example.assignmentevaluation.databinding.ActivityStudentSignUpBinding
import com.example.assignmentevaluation.databinding.ActivityTeacherSignUpBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Student
import java.io.ByteArrayOutputStream

class StudentSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentSignUpBinding
    lateinit var sp : SP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSpinnerValue()
        sp = SP(this)


        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.scrollTo(0, binding.password.bottom)
            }
        }

        binding.userType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parentView?.getItemAtPosition(position).toString()

                if(selectedItem=="Teacher"){
                    startActivity(Intent(this@StudentSignUpActivity,TeacherSignUpActivity::class.java))
                    finish()
                }

            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        binding.goToLogin.setOnClickListener{
            startActivity(Intent(this@StudentSignUpActivity,LoginActivity::class.java))
            finish()
        }

        binding.signup.setOnClickListener {
            signUp()
        }

    }


    private fun signUp() {
        val name = binding.editname.text.toString().trim()
        val email = binding.editemail.text.toString().trim()
        val studentId = binding.editid.text.toString().trim()
        val dept = binding.dept.selectedItem.toString()
        val batch = binding.batch.selectedItem.toString()
        val sec = binding.sec.selectedItem.toString()
        val password = binding.password.text.toString()

        val defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.profile)
        val profilePicBytes = imageToByteArray(defaultBitmap)

        if (name.isEmpty() || email.isEmpty() || studentId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
        } else {
            val db = MyDatabaseHelper(this)

            if (db.isEmailExists(email)) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                return
            }

            val student = Student(
                studentId = studentId,
                name = name,
                email = email,
                dept = dept,
                batch = batch,
                sec = sec,
                profilePic = profilePicBytes,
                password = password
            )

            val success = db.insertStudent(student)

            if (success) {
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@StudentSignUpActivity, LoginActivity::class.java))
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
            R.array.batch_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.batch.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.sec_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sec.adapter = adapter
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.user_array_s,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.userType.adapter = adapter
        }
    }
}