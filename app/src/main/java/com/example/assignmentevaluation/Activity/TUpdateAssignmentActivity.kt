package com.example.assignmentevaluation.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmentevaluation.databinding.ActivityTupdateAssignmentBinding
import com.example.assinment_evaluation.Class.SP
import android.app.DatePickerDialog
import android.content.Intent
import android.widget.Toast
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.Assignment
import java.util.*

class TUpdateAssignmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTupdateAssignmentBinding
    lateinit var sp: SP
    private lateinit var assignmentId: String
    private lateinit var db: MyDatabaseHelper
    private var assignment: Assignment? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTupdateAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sp = SP(this)
        assignmentId = sp.getS("assignmentId")
        db = MyDatabaseHelper(this)

        binding.back.setOnClickListener {
            finish()
        }

        binding.submission.setOnClickListener{
            startActivity(Intent(this@TUpdateAssignmentActivity, TSubmissionListActivity::class.java))
        }


        assignment = db.getAssignmentById(assignmentId)
        if (assignment != null) {
            binding.title.setText(assignment!!.title)
            binding.description.setText(assignment!!.des)
            binding.mark.setText(assignment!!.totalMark)
            binding.date.text = assignment!!.deadline
        }

        binding.date.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val formattedDate = String.format("%02d-%02d-%04d", day, month + 1, year)
                    binding.date.text = formattedDate
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            )

            // Disable past dates
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

            datePickerDialog.show()
        }



        binding.update.setOnClickListener {
            val title = binding.title.text.toString().trim()
            val description = binding.description.text.toString().trim()
            val mark = binding.mark.text.toString().trim()
            val date = binding.date.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || mark.isEmpty() || date == "Submission Date") {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedAssignment = Assignment(
                assignmentId = assignmentId,
                courseId = assignment!!.courseId,
                title = title,
                des = description,
                deadline = date,
                totalMark = mark
            )

            val result = db.updateAssignment(updatedAssignment)
            if (result) {
                Toast.makeText(this, "Assignment updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
