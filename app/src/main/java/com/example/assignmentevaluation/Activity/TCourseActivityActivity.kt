package com.example.assignmentevaluation.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentevaluation.Adapter.TCoursePagerAdapter
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityLoginBinding
import com.example.assignmentevaluation.databinding.ActivityTcourseActivityBinding
import com.example.assinment_evaluation.Adapter.AssignmentForTeacherAdapter
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.google.android.material.tabs.TabLayoutMediator

class TCourseActivityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTcourseActivityBinding
    lateinit var sp : SP
    private lateinit var courseId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTcourseActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        courseId = sp.getS("courseId")

        binding.back.setOnClickListener{
            finish()
        }

        binding.fab.setOnClickListener{
            startActivity(Intent(this,TUploadAssignmentActivity::class.java))
        }


        fetchAssignment()

    }

    override fun onResume() {
        super.onResume()
        fetchAssignment()
    }

    private fun fetchAssignment() {
        val db = MyDatabaseHelper(this)
        val courseId = sp.getS("courseId")
        val assignmentList = db.getAssignmentsByCourseId(courseId)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        var adapter = AssignmentForTeacherAdapter(this, assignmentList)
        binding.recyclerView.adapter = adapter

    }

}