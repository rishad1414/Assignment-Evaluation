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
import com.example.assinment_evaluation.Adapter.AssignmentForStudentAdapter
import com.example.assinment_evaluation.Adapter.CourseForStudentAdapter
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Assignment
import com.example.assinment_evaluation.Model.Course

class SAssignmentFragment : Fragment() {
    private lateinit var binding: FragmentSAssignmentBinding
    private lateinit var sp: SP
    private lateinit var studentId: String
    private lateinit var adapter: AssignmentForStudentAdapter
    private lateinit var databaseHelper: MyDatabaseHelper
    private val assignmentList = mutableListOf<Assignment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSAssignmentBinding.inflate(inflater, container, false)
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
        adapter = AssignmentForStudentAdapter(requireContext(),assignmentList)
        binding.recyclerView.adapter = adapter
    }


    private fun loadAssignment() {
        val courseIds = databaseHelper.getCoursesByStudentId(studentId)
        for (courseId in courseIds) {
            val assignments = databaseHelper.getAssignmentsByCourseId(courseId)
            for (assignment in assignments) {
                var temp = databaseHelper.getSubmitAssignment(assignment.assignmentId!!, studentId)
                if(temp==null)
                     assignmentList.add(assignment)
            }
        }
        adapter.notifyDataSetChanged()
    }



}