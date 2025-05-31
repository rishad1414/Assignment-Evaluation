package com.example.assinment_evaluation.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmentevaluation.Activity.TCourseActivityActivity
import com.example.assignmentevaluation.R
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Course


class CourseForStudentAdapter(
    private val context: Context,
    private var courseList: List<Course>
) : RecyclerView.Adapter<CourseForStudentAdapter.CourseForStudentViewHolder>() {

    class CourseForStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val courseCode: TextView = itemView.findViewById(R.id.courseCode)
        val teacherName: TextView = itemView.findViewById(R.id.teacherName)
        val profilePic: ImageView = itemView.findViewById(R.id.profilePic)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseForStudentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_course_for_student, parent, false)
        return CourseForStudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseForStudentViewHolder, position: Int) {
        val course = courseList[position]
        holder.title.text = course.title
        holder.courseCode.text = course.courseCode

        var db = MyDatabaseHelper(context)
        var teacher = db.getTeacherById(course.teacherId.toString())
        if (teacher != null) {
            holder.teacherName.text = teacher.name
        }


    }

    override fun getItemCount() : Int {
        return courseList.size
    }


}
