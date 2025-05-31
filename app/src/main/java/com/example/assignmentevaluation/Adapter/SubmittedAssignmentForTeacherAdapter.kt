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
import com.example.assignmentevaluation.Activity.SMySubmissionActivity
import com.example.assignmentevaluation.Activity.TCourseActivityActivity
import com.example.assignmentevaluation.Activity.TSubmissionActivity
import com.example.assignmentevaluation.Activity.TUpdateAssignmentActivity
import com.example.assignmentevaluation.R
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Assignment
import com.example.assinment_evaluation.Model.Course
import com.example.assinment_evaluation.Model.SubmitAssignment


class SubmittedAssignmentForTeacherAdapter(
    private val context: Context,
    private var assignmentList: List<SubmitAssignment>
) : RecyclerView.Adapter<SubmittedAssignmentForTeacherAdapter.AssignmentForStudentViewHolder>() {

    class AssignmentForStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.studentName)
        val studentId: TextView = itemView.findViewById(R.id.studentId)
        val date: TextView = itemView.findViewById(R.id.date)
        val time: TextView = itemView.findViewById(R.id.time)
        val status: TextView = itemView.findViewById(R.id.status)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentForStudentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_submitted_assignment_for_teacher, parent, false)
        return AssignmentForStudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssignmentForStudentViewHolder, position: Int) {
        val assignment = assignmentList[position]

        holder.date.text = assignment.submitDate
        holder.time.text = assignment.submitTime
        holder.status.text = assignment.status


        val db = MyDatabaseHelper(context)
        var student = db.getStudentById(assignment.studentId!!)

        if(student!=null){
            holder.studentName.text = student.name
            holder.studentId.text = student.studentId
        }

        holder.itemView.setOnClickListener{
           var  sp = SP(context)

            sp.addS("submitAssignmentId",assignment.id.toString())

            var intent = Intent(context, TSubmissionActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun getItemCount() : Int {
        return assignmentList.size
    }


}
