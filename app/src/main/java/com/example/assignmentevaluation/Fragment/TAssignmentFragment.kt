package com.example.assignmentevaluation.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentevaluation.Activity.TUploadAssignmentActivity
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.FragmentTAssignmentBinding
import com.example.assinment_evaluation.Adapter.AssignmentForTeacherAdapter
import com.example.assinment_evaluation.Adapter.CourseForTeacherAdapter
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper

class TAssignmentFragment : Fragment() {
    private lateinit var binding: FragmentTAssignmentBinding

    private lateinit var sp: SP
    private lateinit var courseId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTAssignmentBinding.inflate(inflater, container, false)
        sp = SP(requireContext())
        courseId = sp.getS("courseId")

     binding.fab.setOnClickListener{
         startActivity(Intent(requireContext(),TUploadAssignmentActivity::class.java))
     }


        fetchAssignment()







        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchAssignment()
    }

    private fun fetchAssignment() {
        val db = MyDatabaseHelper(requireContext())
        val courseId = sp.getS("courseId")
        val assignmentList = db.getAssignmentsByCourseId(courseId)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        var adapter = AssignmentForTeacherAdapter(requireContext(), assignmentList)
        binding.recyclerView.adapter = adapter

    }



}