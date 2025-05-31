package com.example.assignmentevaluation.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignmentevaluation.R
import com.example.assignmentevaluation.databinding.ActivityStudentSignUpBinding
import com.example.assignmentevaluation.databinding.ActivityTsubmissionBinding
import com.example.assinment_evaluation.Class.SP
import com.example.assinment_evaluation.Database.MyDatabaseHelper

class TSubmissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTsubmissionBinding
    lateinit var sp : SP
    lateinit var submitAssignmentId : String
    lateinit var assignmentId : String
    lateinit var courseId : String
    lateinit var db : MyDatabaseHelper
    var pdfByteArray: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTsubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sp = SP(this)
        submitAssignmentId = sp.getS("submitAssignmentId")
        assignmentId = sp.getS("assignmentId")
        courseId = sp.getS("courseId")
        db = MyDatabaseHelper(this)

        showDetails()

        binding.showPdf.setOnClickListener{
           var intent = Intent(this@TSubmissionActivity, PdfActivity::class.java)
            intent.putExtra("pdfByteArray",pdfByteArray)
            startActivity(intent)
        }

        binding.back.setOnClickListener{
            finish()
        }

        binding.deny.setOnClickListener{
            deny()
        }

        binding.accept.setOnClickListener{
            acceptt()
        }




    }

    private fun acceptt() {
        var submitAssignment = db.getSubmitAssignmentId(submitAssignmentId)
        if(submitAssignment!=null){
            var feedBack = binding.feedBack.text.toString()
            var mark = binding.mark.text.toString()

            if(mark.isEmpty()){
                Toast.makeText(this@TSubmissionActivity, "Please Enter mark.",Toast.LENGTH_SHORT).show()
                return
            }

            submitAssignment.feedBack = feedBack
            submitAssignment.mark = mark
            submitAssignment.status = "Accepted"

            var result =  db.updateSubmitAssignment(submitAssignment)
            if(result){
                Toast.makeText(this@TSubmissionActivity, "Assignment accepted successfully.",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this@TSubmissionActivity, "Error",Toast.LENGTH_SHORT).show()
            }



        }
    }

    private fun deny() {
        var submitAssignment = db.getSubmitAssignmentId(submitAssignmentId)
        if(submitAssignment!=null){
            var feedBack = binding.feedBack.text.toString()
            var mark = binding.mark.text.toString()

            submitAssignment.feedBack = feedBack
            submitAssignment.mark = mark

           var result =  db.updateSubmitAssignment(submitAssignment)
            if(result){
                Toast.makeText(this@TSubmissionActivity, "Assignment denied successfully.",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this@TSubmissionActivity, "Error",Toast.LENGTH_SHORT).show()
            }



        }
    }

    private fun showDetails() {
       var submitAssignment = db.getSubmitAssignmentId(submitAssignmentId)
        if(submitAssignment!=null){
            var student = db.getStudentById(submitAssignment.studentId.toString())
            if(student!=null){
                binding.studentName.text = student.name
                binding.studentId.text = student.studentId
                binding.status.text = submitAssignment.status
                binding.mark.setText(submitAssignment.mark)
                binding.feedBack.setText(submitAssignment.feedBack)
                pdfByteArray = submitAssignment.file
            }
        }
    }
}