package com.example.pdffirebaseapp

import android.annotation.SuppressLint
import android.app.DownloadManager
import kotlinx.coroutines.delay


const val DOWNLOAD_SUCCESS = 100L
const val DOWNLOAD_FAILED = -100L

class DownloadProgressUpdater(
    private val manager: DownloadManager,
    private val downloadId: Long,
    private var listener: DownloadProgressListener
) {
    private val query = DownloadManager.Query()
    private var totalBytes = 0

    init {
        query.setFilterById(downloadId)
    }

    @SuppressLint("Range")
    suspend fun run() {

        while (downloadId > 0) {
            delay(250)

            manager.query(query).use { cursor ->

                if (cursor.moveToFirst()) {


                    if (totalBytes <= 0)
                        totalBytes =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                    val downloadStatus =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val bytesDownloadSoFar =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                    when (downloadStatus) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            listener.updateProgress(DOWNLOAD_SUCCESS)
                            return
                        }

                        DownloadManager.STATUS_FAILED -> {
                            listener.updateProgress(DOWNLOAD_FAILED)
                            return
                        }

                        else -> {
                            val downloadProgress = bytesDownloadSoFar * 100L / totalBytes
                            listener.updateProgress(downloadProgress)
                        }
                    }
                }
            }
        }
    }


    interface DownloadProgressListener {
        fun updateProgress(progress: Long)
    }
}