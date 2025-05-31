package com.example.assignmentevaluation.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.FragmentSAssignmentBinding
import com.example.assignmentevaluation.databinding.FragmentSCourseBinding
import com.example.assignmentevaluation.databinding.FragmentSSubmittedAssignmentBinding
import com.example.assinment_evaluation.Adapter.AssignmentForStudentAdapter
import com.example.assinment_evaluation.Adapter.CourseForStudentAdapter
import com.example.assinment_evaluation.Adapter.SubmittedAssignmentForStudentAdapter
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Assignment
import com.example.assinment_evaluation.Model.Course
import com.example.assinment_evaluation.Model.SubmitAssignment

class SSubmittedAssignmentFragment : Fragment() {
    private lateinit var binding: FragmentSSubmittedAssignmentBinding
    private lateinit var sp: SP
    private lateinit var studentId: String
    private lateinit var adapter: SubmittedAssignmentForStudentAdapter
    private lateinit var databaseHelper: MyDatabaseHelper
    private val assignmentList = mutableListOf<SubmitAssignment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSSubmittedAssignmentBinding.inflate(inflater, container, false)
        sp = SP(requireContext())
        studentId = sp.getS("studentId")
        databaseHelper = MyDatabaseHelper(requireContext())

        setupRecyclerView()
        loadAssignment()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        assignmentList.clear()
        loadAssignment()
    }


    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SubmittedAssignmentForStudentAdapter(requireContext(),assignmentList)
        binding.recyclerView.adapter = adapter
    }


    private fun loadAssignment() {
        val assignments = databaseHelper.getSubmitAssignmentsByStudentId(studentId)
        for (assignment in assignments) {
            assignmentList.add(assignment)
        }
        adapter.notifyDataSetChanged()
    }




}