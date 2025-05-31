package com.example.assinment_evaluation.Adapter

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmentevaluation.Activity.TCourseActivityActivity
import com.example.assignmentevaluation.R
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Course


class CourseForTeacherAdapter(
    private val context: Context,
    private var courseList: MutableList<Course>
) : RecyclerView.Adapter<CourseForTeacherAdapter.CourseForTeacherViewHolder>() {

    class CourseForTeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val dept: TextView = itemView.findViewById(R.id.dept)
        val courseCode: TextView = itemView.findViewById(R.id.courseCode)
        val courseId: TextView = itemView.findViewById(R.id.courseId)

        val lt: TextView = itemView.findViewById(R.id.lt)
        val section: TextView = itemView.findViewById(R.id.section)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseForTeacherViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_course_for_teacher, parent, false)
        return CourseForTeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseForTeacherViewHolder, position: Int) {
        val course = courseList[position]
        holder.title.text = course.title
        holder.courseCode.text = course.courseCode
        holder.courseId.text = "C Id: "+ course.courseId

        holder.dept.text = course.dept
        holder.section.text = course.sec
        holder.lt.text = course.lt


        holder.itemView.setOnClickListener{
           var  sp = SP(context)
            sp.addS("courseId",course.courseId.toString())
            var intent = Intent(context, TCourseActivityActivity::class.java)
            context.startActivity(intent)
        }


        holder.itemView.setOnLongClickListener {
            showOptionsDialog(course, position)
            true
        }


    }

    override fun getItemCount() : Int {
        return courseList.size
    }

    private fun showOptionsDialog(course: Course, position: Int) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(context)
            .setTitle("Choose Option")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showEditDialog(course, position)
                    1 -> confirmDelete(course, position)
                }
            }.show()
    }




    private fun confirmDelete(course: Course, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this course and its assignments?")
            .setPositiveButton("Yes") { _, _ ->
                val dbHelper = MyDatabaseHelper(context)

                // Step 1: Get all assignments of the course
                val assignments = dbHelper.getAssignmentsByCourseId(course.courseId!!)

                // Step 2: Delete from submit_assignment table
                for (assignment in assignments) {
                    dbHelper.deleteSubmitAssignmentsByAssignmentId(assignment.assignmentId!!)
                }

                // Step 3: Delete all assignments from assignment table
                for (assignment in assignments) {
                    dbHelper.deleteAssignmentById(assignment.assignmentId!!)
                }
                 val ans =  dbHelper.deleteCourseStudentByCourseId(course.courseId!!)
                val deleted = dbHelper.deleteCourseById(course.courseId!!)

                if (deleted && ans) {
                    courseList.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(context, "Course and related assignments deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete course", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun showEditDialog(course: Course, position: Int) {
        var db = MyDatabaseHelper(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_course, null)

        val deptEditText = dialogView.findViewById<EditText>(R.id.dept)
        val ltEditText = dialogView.findViewById<EditText>(R.id.lt)
        val secEditText = dialogView.findViewById<EditText>(R.id.sec)
        val titleEditText = dialogView.findViewById<EditText>(R.id.title)
        val sNameEditText = dialogView.findViewById<EditText>(R.id.sName)
        val courseCodeEditText = dialogView.findViewById<EditText>(R.id.courseCode)
        val accessKeyEditText = dialogView.findViewById<EditText>(R.id.accessKey)
        val addButton = dialogView.findViewById<AppCompatButton>(R.id.add)
        val closeButton = dialogView.findViewById<ImageView>(R.id.closeButton)

        // Set current course data
        deptEditText.setText(course.dept)
        ltEditText.setText(course.lt)
        secEditText.setText(course.sec)
        titleEditText.setText(course.title)
        sNameEditText.setText(course.sName)
        courseCodeEditText.setText(course.courseCode)
        accessKeyEditText.setText(course.accessKey)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        addButton.setOnClickListener {
            // Collect updated data
            val dept = deptEditText.text.toString().trim()
            val lt = ltEditText.text.toString().trim()
            val sec = secEditText.text.toString().trim()
            val title = titleEditText.text.toString().trim()
            val sName = sNameEditText.text.toString().trim()
            val courseCode = courseCodeEditText.text.toString().trim()
            val accessKey = accessKeyEditText.text.toString().trim()

            // Validation check: Ensure no field is empty
            if (dept.isEmpty() || lt.isEmpty() || sec.isEmpty() || title.isEmpty() ||
                sName.isEmpty() || courseCode.isEmpty() || accessKey.isEmpty()) {
                Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the updated course object
            val updatedCourse = Course(
                courseId = course.courseId,
                teacherId = course.teacherId,
                title = title,
                sName = sName,
                courseCode = courseCode,
                accessKey = accessKey,
                dept = dept,
                lt = lt,
                sec = sec
            )

            // Update the course in the database
            val isUpdated = db.updateCourse(updatedCourse)

            if (isUpdated) {
                courseList[position] = updatedCourse
                notifyItemChanged(position)

                Toast.makeText(context, "Course updated successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Failed to update course.", Toast.LENGTH_SHORT).show()
            }


        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }




}
