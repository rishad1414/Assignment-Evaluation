package com.example.assignmentevaluation.Activity

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityLoginBinding
import com.example.assignmentevaluation.databinding.ActivityTeacherHomeBinding
import com.example.assinment_evaluation.Adapter.CourseForTeacherAdapter
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Course

class TeacherHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeacherHomeBinding
    lateinit var sp : SP


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTeacherHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        setTeacherDetails()


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
            startActivity(Intent(this@TeacherHomeActivity, TeacherProfileActivity::class.java))
        }

        binding.aa.setOnClickListener{
            startActivity(Intent(this@TeacherHomeActivity, TeacherProfileActivity::class.java))
        }


        fetchCourse()


    }


    private fun setTeacherDetails() {
        var teacherId = sp.getS("teacherId")
        var db = MyDatabaseHelper(this)
        var teacher  = db.getTeacherById(teacherId)
        if(teacher!=null){
            binding.name.text = teacher.name

            if (teacher.profilePic != null && teacher.profilePic!!.isNotEmpty()) {
                val profileBitmap = BitmapFactory.decodeByteArray(teacher.profilePic, 0, teacher.profilePic!!.size)
                binding.profilePic.setImageBitmap(profileBitmap)
            } else {
                binding.profilePic.setImageResource(R.drawable.teacher)
            }



        }
    }

    override fun onResume() {
        super.onResume()
        setTeacherDetails()
    }


    private fun fetchCourse() {
        val db = MyDatabaseHelper(this)
        val teacherId = sp.getS("teacherId")
        val courseList = db.getCoursesByTeacherId(teacherId)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        var adapter = CourseForTeacherAdapter(this, courseList.toMutableList())
        binding.recyclerView.adapter = adapter

    }


    private fun showAddCourseDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_course)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dept = dialog.findViewById<Spinner>(R.id.dept)
        val lt = dialog.findViewById<Spinner>(R.id.lt)
        val sec = dialog.findViewById<Spinner>(R.id.sec)
        val title = dialog.findViewById<EditText>(R.id.title)
        val sName = dialog.findViewById<EditText>(R.id.sName)
        val courseCode = dialog.findViewById<EditText>(R.id.courseCode)
        val accessKey = dialog.findViewById<EditText>(R.id.accessKey)

        val add = dialog.findViewById<AppCompatButton>(R.id.add)
        val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)

        setSpinnerValue(dept,lt,sec)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        var teacherId = sp.getS("teacherId")

        add.setOnClickListener {

            val selectedDept = dept.selectedItem.toString()
            val selectedLT = lt.selectedItem.toString()
            val selectedSec = sec.selectedItem.toString()

            val titleText = title.text.toString().trim()
            val sNameText = sName.text.toString().trim()
            val courseCodeText = courseCode.text.toString().trim()
            val accessKeyText = accessKey.text.toString().trim()

            if (titleText.isEmpty() || sNameText.isEmpty() || courseCodeText.isEmpty() || accessKeyText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(selectedDept=="Dept"){
                Toast.makeText(this, "Please select Department", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(selectedLT=="Level-Term"){
                Toast.makeText(this, "Please select Level and Term", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(selectedSec=="Section"){
                Toast.makeText(this, "Please select Section", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val courseId = System.currentTimeMillis().toString()
            val course = Course(
                courseId = courseId,
                teacherId = teacherId,
                title = titleText,
                sName = sNameText,
                courseCode = courseCodeText,
                accessKey = accessKeyText,
                dept = selectedDept,
                lt = selectedLT,
                sec = selectedSec
            )


            val db = MyDatabaseHelper(this)
            val success = db.insertCourse(course)
            if (success) {
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show()
                fetchCourse()
                dialog.dismiss()

            } else {
                Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show()
            }
        }


        dialog.show()
    }

    private fun setSpinnerValue(dept: Spinner?, lt: Spinner?, sec: Spinner?) {
        ArrayAdapter.createFromResource(
            this,
            R.array.department_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            if (dept != null) {
                dept.adapter = adapter
            }
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.lt_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            if (lt != null) {
                lt.adapter = adapter
            }
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.sec_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            if (sec != null) {
                sec.adapter = adapter
            }
        }



    }

    private fun logOut() {
        sp.addB("isLogin", true)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}