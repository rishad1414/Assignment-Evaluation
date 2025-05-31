package com.example.assignmentevaluation.Activity

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmentevaluation.databinding.ActivityPdfBinding
import com.example.assinment_evaluation.Class.SP
import java.io.File
import java.io.FileOutputStream

class PdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfBinding
    lateinit var sp: SP
    var pdfByteArray: ByteArray? = null

    private var currentPageIndex = 0
    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var fileDescriptor: ParcelFileDescriptor

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1f
    private var matrix: Matrix = Matrix()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sp = SP(this)
        pdfByteArray = intent.getByteArrayExtra("pdfByteArray")

        if (pdfByteArray == null) {
            Toast.makeText(this, "No PDF data received", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tempFile = File(cacheDir, "temp_pdf_blob.pdf")
        FileOutputStream(tempFile).use { it.write(pdfByteArray) }

        fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)

        showPage(currentPageIndex)
        setupPageButtons(pdfRenderer.pageCount)

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
    }

    private fun showPage(pageIndex: Int) {
        if (pageIndex < 0 || pageIndex >= pdfRenderer.pageCount) return

        val page = pdfRenderer.openPage(pageIndex)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        binding.bal.setImageBitmap(bitmap)
        page.close()

        // Update the page button highlight
        updatePageButtonHighlight(pageIndex)
    }

    private fun setupPageButtons(pageCount: Int) {
        binding.pageButtonContainer.removeAllViews()

        for (i in 0 until pageCount) {
            val button = Button(this)
            button.text = "${i + 1}"
            button.textSize = 14f
            button.setPadding(20, 10, 20, 10)

            button.setOnClickListener {
                currentPageIndex = i
                showPage(currentPageIndex)
            }

            // Initially, add the button to the container
            binding.pageButtonContainer.addView(button)
        }

        // Initially highlight the first page button
        updatePageButtonHighlight(0)
    }

    private fun updatePageButtonHighlight(pageIndex: Int) {
        // Iterate through all buttons and remove highlight from others
        for (i in 0 until binding.pageButtonContainer.childCount) {
            val button = binding.pageButtonContainer.getChildAt(i) as Button
            if (i == pageIndex) {
                // Highlight the current button (e.g., change background color or text color)
                button.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light)) // Highlight color
                button.setTextColor(resources.getColor(android.R.color.white)) // Text color for highlighted button
            } else {
                // Reset the other buttons to default state
                button.setBackgroundColor(resources.getColor(android.R.color.transparent)) // Default color
                button.setTextColor(resources.getColor(android.R.color.black)) // Default text color
            }
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.5f, 5f)
            applyZoom(scaleFactor)
            return true
        }
    }

    private fun applyZoom(scaleFactor: Float) {
        matrix.setScale(scaleFactor, scaleFactor)
        matrix.postTranslate(
            (binding.bal.width - binding.bal.drawable.intrinsicWidth * scaleFactor) / 2f,
            (binding.bal.height - binding.bal.drawable.intrinsicHeight * scaleFactor) / 2f
        )
        binding.bal.imageMatrix = matrix
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer.close()
        fileDescriptor.close()
    }
}
