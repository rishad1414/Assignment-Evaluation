package com.example.assignmentevaluation.Activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityTuploadAssignmentBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Assignment
import com.example.assinment_evaluation.Model.Course
import java.util.Date
import java.text.SimpleDateFormat

class TUploadAssignmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTuploadAssignmentBinding
    lateinit var sp: SP
    private lateinit var courseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTuploadAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        courseId = sp.getS("courseId")

        binding.back.setOnClickListener {
            finish()
        }

        binding.date.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                binding.date.text = selectedDate
            }, year, month, day)

            // Disable past dates
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }


        binding.add.setOnClickListener {
            val title = binding.title.text.toString().trim()
            val description = binding.description.text.toString().trim()
            val mark = binding.mark.text.toString().trim()

            val deadlineText = binding.date.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || mark.isEmpty() ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(deadlineText=="Submission Date"){
                Toast.makeText(this, "Please set deadline", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val assignment = Assignment(
                assignmentId = System.currentTimeMillis().toString(),
                courseId = courseId,
                title = title,
                des = description,
                totalMark = mark
            )

            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            try {
                val deadlineDate = dateFormat.parse(deadlineText)
                assignment.deadline = dateFormat.format(deadlineDate)
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = MyDatabaseHelper(this)
            val success = db.insertAssignment(assignment)

            if (success) {
                Toast.makeText(this, "Assignment uploaded successfully", Toast.LENGTH_SHORT).show()
                showAddCourseDialog(assignment)
            } else {
                Toast.makeText(this, "Failed to upload assignment", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun showAddCourseDialog(assignment: Assignment) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_upload_assignment_success)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val title = dialog.findViewById<TextView>(R.id.title)
        val courseCode = dialog.findViewById<TextView>(R.id.courseCode)
        val deadline = dialog.findViewById<TextView>(R.id.deadline)

        val goback = dialog.findViewById<Button>(R.id.goback)
        val view = dialog.findViewById<Button>(R.id.view)

        val db = MyDatabaseHelper(this)
        val course = db.getCourseByCourseId(assignment.courseId.toString())

        title.setText(assignment.title)
        if (course != null) {
            courseCode.setText(course.courseCode)
        }
        deadline.setText(assignment.deadline.toString())



        view.setOnClickListener {
            dialog.dismiss()
            sp.addS("assignmentId", assignment.assignmentId.toString())
            startActivity(Intent(this@TUploadAssignmentActivity,TUpdateAssignmentActivity::class.java))
            finish()
        }
        goback.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }



}
