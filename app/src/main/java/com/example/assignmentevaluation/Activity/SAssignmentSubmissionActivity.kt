package com.example.assignmentevaluation.Activity

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Layout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivitySassignmentSubmissionBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import com.example.assinment_evaluation.Model.SubmitAssignment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SAssignmentSubmissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySassignmentSubmissionBinding
    lateinit var sp : SP
    lateinit var assignmentId : String
    lateinit var studentId : String

    private var selectedPdfBytes: ByteArray? = null
    private val pdfPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val fileName = getFileNameFromUri(it)
                binding.pdfFileName.text = fileName
                selectedPdfBytes = contentResolver.openInputStream(it)?.readBytes()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySassignmentSubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        assignmentId = sp.getS("assignmentId")
        studentId = sp.getS("studentId")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.des.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        setDetails()


        binding.pdfFileName.setOnClickListener {
            pdfPickerLauncher.launch("application/pdf")
        }

        binding.back.setOnClickListener{
            finish()
        }

        binding.submit.setOnClickListener{
            submitAssignment()
        }


    }

    private fun submitAssignment() {
        if (selectedPdfBytes == null) {
            Toast.makeText(this@SAssignmentSubmissionActivity, "Please select a file", Toast.LENGTH_SHORT).show()
            return
        }

        // Check the size of the PDF file (in bytes)
        val maxFileSize = 1 * 1024 * 1024
        val fileSize = selectedPdfBytes!!.size

        if (fileSize > maxFileSize) {
            Toast.makeText(this@SAssignmentSubmissionActivity, "File size is too large. Maximum allowed size is 1 MB.", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with submission
        val id = System.currentTimeMillis().toString()
        val submitTime = getCurrentTime()
        val submitDate = getCurrentDate()

        val submitAssignment = SubmitAssignment(id, assignmentId, studentId, submitTime, submitDate, "Pending", "", "", selectedPdfBytes, "")
        val db = MyDatabaseHelper(this)
        val result = db.insertSubmitAssignment(submitAssignment)
        if (result) {
            Toast.makeText(this@SAssignmentSubmissionActivity, "Your assignment submitted successfully.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this@SAssignmentSubmissionActivity, "Error.", Toast.LENGTH_SHORT).show()
        }
    }


    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(Date())
    }
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }


    private fun getFileNameFromUri(uri: android.net.Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown_file.pdf"
    }




    private fun setDetails() {
       var db = MyDatabaseHelper(this)
        var assignment = db.getAssignmentById(assignmentId)
        if(assignment!=null){
            var course = db.getCourseByCourseId(assignment.courseId!!)
            if(course!=null){
                binding.courseTitle.text = course.title
                binding.deadline.text = assignment.deadline
                binding.assignmentTitle.text = assignment.title
                binding.mark.text = assignment.totalMark
                binding.des.text = assignment.des


            }
        }
    }
}