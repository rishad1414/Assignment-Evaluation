package com.example.assignmentevaluation.Activity

import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivitySassignmentSubmissionBinding
import com.example.assignmentevaluation.databinding.ActivitySmySubmissionBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SMySubmissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmySubmissionBinding
    lateinit var sp : SP
    lateinit var submitAssignmentId : String

    private var selectedPdfBytes: ByteArray? = null
    private var selectedPdfBytes2: ByteArray? = null

    private val pdfPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val fileName = getFileNameFromUri(it)
                binding.pdfFileName.text = fileName
                selectedPdfBytes2 = contentResolver.openInputStream(it)?.readBytes()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySmySubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        submitAssignmentId = sp.getS("submitAssignmentId")
        expandControl()
        setAssignemntDetails()

        binding.back.setOnClickListener{
            finish()
        }

        binding.showFile.setOnClickListener{
            val intent = Intent(this, PdfActivity::class.java)
            intent.putExtra("pdfByteArray", selectedPdfBytes)
            startActivity(intent)
        }

        binding.pdfFileName.setOnClickListener {
            pdfPickerLauncher.launch("application/pdf")
        }


        binding.update.setOnClickListener{
            updateAssignment()
        }







    }

    private fun updateAssignment() {
        if (selectedPdfBytes2 == null) {
            Toast.makeText(this@SMySubmissionActivity, "Please select a file", Toast.LENGTH_SHORT).show()
            return
        }

        // Check the size of the PDF file (in bytes)
        val maxFileSize = 1 * 1024 * 1024
        val fileSize = selectedPdfBytes2!!.size

        if (fileSize > maxFileSize) {
            Toast.makeText(this@SMySubmissionActivity, "File size is too large. Maximum allowed size is 1 MB.", Toast.LENGTH_SHORT).show()
            return
        }


        val db = MyDatabaseHelper(this)
        val submittedAssignment = db.getSubmitAssignmentId(submitAssignmentId)

        if (submittedAssignment != null) {
            submittedAssignment.file = selectedPdfBytes2
            submittedAssignment.submitDate = getCurrentDate()
            submittedAssignment.submitTime = getCurrentTime()

            val success = db.updateSubmitAssignment(submittedAssignment)

            if (success) {
                Toast.makeText(this, "Assignment updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update assignment", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No assignment found to update", Toast.LENGTH_SHORT).show()
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



    private fun setAssignemntDetails() {
        var db = MyDatabaseHelper(this)
        var submittedAssignment = db.getSubmitAssignmentId(submitAssignmentId)
        if(submittedAssignment!=null){
           var assignment = db.getAssignmentById(submittedAssignment.assignmentId!!)
            if(assignment!=null){
                var course = db.getCourseByCourseId(assignment.courseId!!)
                if(course!=null){
                    binding.courseTitle.text = course.title
                    binding.submitDate.text = submittedAssignment.submitDate
                    binding.submitTime.text = submittedAssignment.submitTime
                    binding.status.text =     submittedAssignment.status
                    selectedPdfBytes = submittedAssignment.file

                    if(submittedAssignment.mark!="")
                        binding.obtainedMark.text = submittedAssignment.mark
                    else
                        binding.obtainedMark.text = "Mark not given"

                    if(submittedAssignment.feedBack!="")
                        binding.feedBack.text = submittedAssignment.feedBack
                    else
                        binding.feedBack.text = "No feedback provided"



                    binding.deadline.text = assignment.deadline
                    binding.assignmentTitle.text = assignment.title
                    binding.mark.text = assignment.totalMark
                    binding.des.text = assignment.des

                    if(submittedAssignment.status=="Accepted"){
                        binding.update.visibility = View.GONE
                        binding.aaaa.visibility = View.GONE
                    }
                }
            }
        }


    }

    private fun expandControl() {
        binding.assignmentLayout.visibility = View.GONE
        binding. header.setOnClickListener {
            if (binding.assignmentLayout.visibility == View.GONE) {
                binding.assignmentLayout.visibility = View.VISIBLE
                binding.expandIcon.setImageResource(R.drawable.ic_dropup)
            } else {
                binding. assignmentLayout.visibility = View.GONE
                binding.expandIcon.setImageResource(R.drawable.ic_dropdown)
            }
        }
    }
}