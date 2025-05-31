package com.example.assinment_evaluation.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmentevaluation.Activity.SAssignmentSubmissionActivity
import com.example.assignmentevaluation.Activity.TCourseActivityActivity
import com.example.assignmentevaluation.Activity.TUpdateAssignmentActivity
import com.example.assignmentevaluation.R
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Assignment
import com.example.assinment_evaluation.Model.Course


class AssignmentForStudentAdapter(
    private val context: Context,
    private var assignmentList: List<Assignment>
) : RecyclerView.Adapter<AssignmentForStudentAdapter.AssignmentForStudentViewHolder>() {

    class AssignmentForStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val courseTitle: TextView = itemView.findViewById(R.id.courseTitle)
        val courseCode: TextView = itemView.findViewById(R.id.courseCode)
        val assignmentTitle: TextView = itemView.findViewById(R.id.assignmentTitle)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentForStudentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_assignment_for_student, parent, false)
        return AssignmentForStudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssignmentForStudentViewHolder, position: Int) {
        val assignment = assignmentList[position]

        holder.date.text = assignment.deadline
        holder.assignmentTitle.text = assignment.title

        val db = MyDatabaseHelper(context)
        val course = db.getCourseByCourseId(assignment.courseId.toString())
        if (course != null) {
            holder.courseCode.text = course.courseCode
            holder.courseTitle.text = course.title
        }


        holder.itemView.setOnClickListener{
           var  sp = SP(context)
            if (course != null) {
                sp.addS("assignmentId",assignment.assignmentId.toString())
            }
            var intent = Intent(context, SAssignmentSubmissionActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun getItemCount() : Int {
        return assignmentList.size
    }


}
