package com.example.assignmentevaluation.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivitySplashBinding
import com.example.assignmentevaluation.databinding.ActivityTsubmissionListBinding
import com.example.assinment_evaluation.Adapter.SubmittedAssignmentForStudentAdapter
import com.example.assinment_evaluation.Adapter.SubmittedAssignmentForTeacherAdapter
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.SubmitAssignment

class TSubmissionListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTsubmissionListBinding
    lateinit var sp : SP

    private lateinit var adapter: SubmittedAssignmentForTeacherAdapter
    private lateinit var databaseHelper: MyDatabaseHelper
    private val assignmentList = mutableListOf<SubmitAssignment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTsubmissionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        databaseHelper = MyDatabaseHelper(this)

        setupRecyclerView()
        loadAssignment()

        binding.back.setOnClickListener{
            finish()
        }



    }

    override fun onResume() {
        super.onResume()
       assignmentList.clear()
        loadAssignment()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SubmittedAssignmentForTeacherAdapter(this,assignmentList)
        binding.recyclerView.adapter = adapter
    }


    private fun loadAssignment() {
        var assignmentId = sp.getS("assignmentId")
        val assignments = databaseHelper.getSubmitAssignmentsByAssignmentId(assignmentId)
        for (assignment in assignments) {
            assignmentList.add(assignment)
        }
        adapter.notifyDataSetChanged()
    }
}