package com.example.pdffirebaseapp

import android.app.DownloadManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.pdffirebaseapp.databinding.ActivityPdfViewerBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.net.URL

class PdfViewerActivity : AppCompatActivity(), DownloadProgressUpdater.DownloadProgressListener {

    private lateinit var binding: ActivityPdfViewerBinding
    private lateinit var downloadManager: DownloadManager
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fileName = intent.extras?.getString("fileName")
        val downloadUrl = intent.extras?.getString("downloadUrl")

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        snackbar = Snackbar.make(binding.mainLayout, "", Snackbar.LENGTH_INDEFINITE)

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

        binding.floatingActionButton.setOnClickListener {
            downloadPdf(downloadUrl, fileName)
        }
    }

    private fun downloadPdf(downloadUrl: String?, fileName: String?) {
        try {
            val downloadUri = Uri.parse(downloadUrl)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setMimeType("application/pdf")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + fileName
                )
            val downloadId = downloadManager.enqueue(request)
            binding.progressBar.visibility = View.VISIBLE
            val downloadProgressHelper = DownloadProgressUpdater(downloadManager, downloadId, this)
            lifecycleScope.launch(Dispatchers.IO) {
                downloadProgressHelper.run()
            }
            snackbar.show()

        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun updateProgress(progress: Long) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (progress) {
                DOWNLOAD_SUCCESS -> {
                    snackbar.setText("Downloading ...... $progress%")
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@PdfViewerActivity,
                        " Downloaded Successfully !!",
                        Toast.LENGTH_SHORT
                    ).show()
                    snackbar.dismiss()
                }

                DOWNLOAD_FAILED -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@PdfViewerActivity,
                        " Downloaded Failed !!",
                        Toast.LENGTH_SHORT
                    ).show()
                    snackbar.dismiss()
                }

                else -> {
                    binding.progressBar.progress = progress.toInt()
                    snackbar.setText("Downloading ...... $progress%")
                }
            }
        }
    }

}