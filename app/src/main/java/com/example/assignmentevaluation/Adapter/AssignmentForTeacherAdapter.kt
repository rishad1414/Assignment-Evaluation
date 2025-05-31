package com.example.assinment_evaluation.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmentevaluation.Activity.TCourseActivityActivity
import com.example.assignmentevaluation.Activity.TUpdateAssignmentActivity
import com.example.assignmentevaluation.R
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Assignment
import com.example.assinment_evaluation.Model.Course


class AssignmentForTeacherAdapter(
    private val context: Context,
    private var assignmentList: List<Assignment>
) : RecyclerView.Adapter<AssignmentForTeacherAdapter.CourseForTeacherViewHolder>() {

    class CourseForTeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val courseCode: TextView = itemView.findViewById(R.id.courseCode)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseForTeacherViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_assignment_for_teacher, parent, false)
        return CourseForTeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseForTeacherViewHolder, position: Int) {
        val assignment = assignmentList[position]
        holder.title.text = assignment.title

        val db = MyDatabaseHelper(context)
        val course = db.getCourseByCourseId(assignment.courseId.toString())
        if (course != null) {
            holder.courseCode.text = course.courseCode
        }


        holder.itemView.setOnClickListener{
           var  sp = SP(context)
            if (course != null) {
                sp.addS("assignmentId",assignment.assignmentId.toString())
            }
            var intent = Intent(context, TUpdateAssignmentActivity::class.java)
            context.startActivity(intent)
        }

        holder.delete.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Delete Assignment")
            builder.setMessage("Are you sure you want to delete this assignment?")

            builder.setPositiveButton("Yes") { dialog, _ ->
                val db = MyDatabaseHelper(context)
                db.deleteAssignmentById(assignment.assignmentId.toString())
                db.deleteSubmitAssignmentsByAssignmentId(assignment.assignmentId.toString())


                assignmentList = assignmentList.toMutableList().apply {
                    removeAt(position)
                }
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, assignmentList.size)

                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alert = builder.create()
            alert.show()
        }


    }

    override fun getItemCount() : Int {
        return assignmentList.size
    }


}
