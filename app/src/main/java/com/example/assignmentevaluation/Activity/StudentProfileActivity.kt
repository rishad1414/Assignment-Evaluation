package com.example.assignmentevaluation.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityStudentHomeBinding
import com.example.assignmentevaluation.databinding.ActivityStudentProfileBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import java.io.ByteArrayOutputStream

class StudentProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentProfileBinding
    lateinit var sp : SP
    private lateinit var studentId :String

    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        studentId = sp.getS("studentId")
        setStudentDetails()



        binding.edit.setOnClickListener {
            openImagePicker()
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.updatebtn.setOnClickListener{
            updatestudent()
        }








    }


    private fun updatestudent() {
        val dbf = MyDatabaseHelper(this)
        val student = dbf.getStudentById(studentId)
        if (student != null) {
            student.name = binding.name.text.toString()
            student.email = binding.email.text.toString()
            student.sec = binding.sec.text.toString()
            student.dept = binding.dept.text.toString()
            student.batch = binding.batch.text.toString()

            val isUpdated = dbf.updateStudent(student)

            if (isUpdated) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data
            selectedImageUri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)

                // Convert to byte array
                val imageByteArray = convertBitmapToByteArray(bitmap)

                // Check size (1 MB = 1,048,576 bytes)
                if (imageByteArray.size > 1048576) {
                    Toast.makeText(this, "Image is too large. Please select an image under 1 MB.", Toast.LENGTH_LONG).show()
                    return
                }

                // Set image and update
                binding.picture.setImageBitmap(bitmap)
                updateStudentProfileWithImage(imageByteArray)
            }
        }
    }


    // Convert Bitmap image to ByteArray (BLOB)
    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun updateStudentProfileWithImage(imageByteArray: ByteArray) {
        val db = MyDatabaseHelper(this)
        val student = db.getStudentById(studentId)
        if (student != null) {
            student.profilePic = imageByteArray

            val isUpdated = db.updateStudent(student)

            if (isUpdated) {
                Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show()
        }
    }




    private fun setStudentDetails() {
        val db = MyDatabaseHelper(this)
        val student = db.getStudentById(studentId)

        if (student != null) {
            binding.name.setText(student.name)
            binding.studentId.setText(student.studentId)
            binding.email.setText(student.email)
            binding.dept.setText(student.dept)
            binding.batch.setText(student.batch)
            binding.sec.setText(student.sec)


            student.profilePic?.let {
                val profileBitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                binding.picture.setImageBitmap(profileBitmap)
            } ?: run {
                binding.picture.setImageResource(R.drawable.student)
            }
        } else {
            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show()
        }
    }
}