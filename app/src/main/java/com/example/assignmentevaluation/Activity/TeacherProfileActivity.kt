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
import com.example.assignmentevaluation.databinding.ActivityTeacherProfileBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import java.io.ByteArrayOutputStream

class TeacherProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherProfileBinding
    lateinit var sp : SP
    private lateinit var teacherId :String

    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTeacherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        teacherId = sp.getS("teacherId")
        setTeacherDetails()



        binding.edit.setOnClickListener {
            openImagePicker()
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.updatebtn.setOnClickListener{
            updateTeacher()
        }








    }


    private fun updateTeacher() {
        val dbf = MyDatabaseHelper(this)
        val teacher = dbf.getTeacherById(teacherId)
        if (teacher != null) {
            teacher.name = binding.name.text.toString()
            teacher.email = binding.email.text.toString()
            teacher.des = binding.des.text.toString()
            teacher.dept = binding.dept.text.toString()

            val isUpdated = dbf.updateTeacher(teacher)

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
        val teacher = db.getTeacherById(teacherId)
        if (teacher != null) {
            teacher.profilePic = imageByteArray

            val isUpdated = db.updateTeacher(teacher)

            if (isUpdated) {
                Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show()
        }
    }




    private fun setTeacherDetails() {
        val db = MyDatabaseHelper(this)
        val teacher = db.getTeacherById(teacherId)

        if (teacher != null) {
            binding.name.setText(teacher.name)
            binding.teacherId.setText(teacher.teacherId)
            binding.email.setText(teacher.email)
            binding.dept.setText(teacher.dept)
            binding.des.setText(teacher.des)



            teacher.profilePic?.let {
                val profileBitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                binding.picture.setImageBitmap(profileBitmap)
            } ?: run {
                binding.picture.setImageResource(R.drawable.teacher)
            }
        } else {
            Toast.makeText(this, "Teacher not found", Toast.LENGTH_SHORT).show()
        }
    }
}