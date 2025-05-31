package com.example.assignmentevaluation.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignmentevaluation.Adapter.SHomePagerAdapter
import com.example.assignmentevaluation.Fragment.SCourseFragment
import com.example.assignmentevaluation.Model.CourseStudent
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityStudentHomeBinding
import com.example.assignmentevaluation.databinding.ActivityTeacherHomeBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Course
import com.google.android.material.tabs.TabLayoutMediator

class StudentHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentHomeBinding
    lateinit var sp : SP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        setViewPager()
        setStudentDetails()

        binding.threeDot.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            menuInflater.inflate(R.menu.menu_three_dot, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.add_student -> {
                        showAddCourseDialog()
                        true
                    }
                    R.id.add_course -> {
                        logOut()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        binding.aaa.setOnClickListener{
            startActivity(Intent(this@StudentHomeActivity, StudentProfileActivity::class.java))
        }

        binding.aa.setOnClickListener{
            startActivity(Intent(this@StudentHomeActivity, StudentProfileActivity::class.java))
        }


    }

    override fun onResume() {
        super.onResume()
        setStudentDetails()

    }

    @SuppressLint("SuspiciousIndentation")
    private fun setStudentDetails() {
       var studentId = sp.getS("studentId")
        var db = MyDatabaseHelper(this)
        var student  = db.getStudentById(studentId)
        if(student!=null){
       binding.name.text = student.name


            if (student.profilePic != null && student.profilePic!!.isNotEmpty()) {
                val profileBitmap = BitmapFactory.decodeByteArray(student.profilePic, 0, student.profilePic!!.size)
                binding.profilePic.setImageBitmap(profileBitmap)
            } else {
                binding.profilePic.setImageResource(R.drawable.student)
            }



        }
    }


    private fun showAddCourseDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_course_student)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val courseId = dialog.findViewById<EditText>(R.id.courseId)
        val accessKey = dialog.findViewById<EditText>(R.id.accessKey)

        val add = dialog.findViewById<AppCompatButton>(R.id.add)
        val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        add.setOnClickListener {

            val courseIdText = courseId.text.toString().trim()
            val accessKeyText = accessKey.text.toString().trim()

            if (courseIdText.isEmpty() || accessKeyText.isEmpty() ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = MyDatabaseHelper(this)
            var course = db.getCourseByCourseId(courseIdText)
            if(course!=null){
                if(course.accessKey!=accessKeyText){
                    Toast.makeText(this, "Wrong Access Key", Toast.LENGTH_SHORT).show()
                }else{
                    var studentId = sp.getS("studentId")
                    var courseStudent = CourseStudent(courseIdText,studentId)

                    var answer = db.isCourseStudentExists(courseIdText,studentId)
                    if(answer){
                        Toast.makeText(this, "Course already added", Toast.LENGTH_SHORT).show()
                    }else{
                        var result =  db.insertCourseStudent(courseStudent)
                        if(result){
                            Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            setViewPager()
                        }else{
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }else{
                Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show()
            }




        }


        dialog.show()
    }




    private fun logOut() {
        sp.addB("isLogin", true)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setViewPager() {
        val adapter = SHomePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Course"
                1 -> "Assignment"
                2 -> "My Submission"
                else -> ""
            }
        }.attach()
    }
}