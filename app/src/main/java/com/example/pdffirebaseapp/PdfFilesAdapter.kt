package com.example.pdffirebaseapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pdffirebaseapp.databinding.EachPdfItemBinding

class PdfFilesAdapter(private val listener: PdfClickListener) :
    ListAdapter<PdfFile, PdfFilesAdapter.PdfFilesViewHolder>(PdfDiffCallback()) {

    inner class PdfFilesViewHolder(private val binding: EachPdfItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onPdfClicked(getItem(adapterPosition))
            }
        }

        fun bind(data: PdfFile) {
            binding.fileName.text = data.fileName
        }
    }

    class PdfDiffCallback : DiffUtil.ItemCallback<PdfFile>() {
        override fun areItemsTheSame(oldItem: PdfFile, newItem: PdfFile) =
            oldItem.downloadUrl == newItem.downloadUrl

        override fun areContentsTheSame(oldItem: PdfFile, newItem: PdfFile) = oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfFilesViewHolder {
        val binding = EachPdfItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfFilesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfFilesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface PdfClickListener {
        fun onPdfClicked(pdfFile: PdfFile)
    }
}