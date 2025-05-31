package com.example.assignmentevaluation.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.FragmentSCourseBinding
import com.example.assignmentevaluation.databinding.FragmentTAssignmentBinding
import com.example.assinment_evaluation.Adapter.CourseForStudentAdapter
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Course


class SCourseFragment : Fragment() {
    private lateinit var binding: FragmentSCourseBinding
    private lateinit var sp: SP
    private lateinit var studentId: String
    private lateinit var adapter: CourseForStudentAdapter
    private lateinit var databaseHelper: MyDatabaseHelper
    private val courseList = mutableListOf<Course>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSCourseBinding.inflate(inflater, container, false)
        sp = SP(requireContext())
        studentId = sp.getS("studentId")
        databaseHelper = MyDatabaseHelper(requireContext())

        setupRecyclerView()
        loadCourses()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.courseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CourseForStudentAdapter(requireContext(),courseList)
        binding.courseRecyclerView.adapter = adapter
    }

    fun refreshCourses() {
        courseList.clear()
        loadCourses()
    }

    private fun loadCourses() {
        val courseIds = databaseHelper.getCoursesByStudentId(studentId)
        for (courseId in courseIds) {
            val course = databaseHelper.getCourseByCourseId(courseId)
            course?.let {
                courseList.add(it)
            }
        }
        adapter.notifyDataSetChanged()
    }
}
