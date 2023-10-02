package com.example.pdffirebaseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.pdffirebaseapp.databinding.ActivityPdfViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fileName = intent.extras?.getString("fileName")
        val downloadUrl = intent.extras?.getString("downloadUrl")

        lifecycleScope.launch(Dispatchers.IO) {

            val inputStream = URL(downloadUrl).openStream()
            withContext(Dispatchers.Main) {
                binding.pdfView.fromStream(inputStream).onRender { pages, pageWidth, pageHeight ->
                    if (pages >= 1) {
                        binding.progressBar.visibility = View.GONE
                    }
                }.load()
            }
        }
    }

}