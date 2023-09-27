package com.example.pdffirebaseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pdffirebaseapp.databinding.ActivityAllPdfsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllPdfsActivity : AppCompatActivity(), PdfFilesAdapter.PdfClickListener {
    private lateinit var binding: ActivityAllPdfsBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: PdfFilesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAllPdfsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")
        initRecyclerView()
        getAllPdfs()
    }

    private fun getAllPdfs() {

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val tempList = mutableListOf<PdfFile>()
                snapshot.children.forEach {
                    val pdfFile = it.getValue(PdfFile::class.java)
                    if (pdfFile != null) {
                        tempList.add(pdfFile)
                    }
                }
                if (tempList.isEmpty())
                    Toast.makeText(this@AllPdfsActivity, "No Data Found", Toast.LENGTH_SHORT)
                        .show()
                adapter.submitList(tempList)
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllPdfsActivity, error.message, Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
            }

        })
    }

    private fun initRecyclerView() {
        binding.pdfsRecyclerView.setHasFixedSize(true)
        binding.pdfsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = PdfFilesAdapter(this)
        binding.pdfsRecyclerView.adapter = adapter
    }

    override fun onPdfClicked(pdfFile: PdfFile) {
        val intent = Intent(this, PdfViewerActivity::class.java)
        intent.putExtra("fileName", pdfFile.fileName)
        intent.putExtra("downloadUrl", pdfFile.downloadUrl)
        startActivity(intent)
    }
}