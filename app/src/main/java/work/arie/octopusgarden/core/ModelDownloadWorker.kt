package work.arie.octopusgarden.core

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

internal class ModelDownloadWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(URL) ?: return Result.failure()
        val path = inputData.getString(PATH) ?: return Result.failure()

        return try {
            downloadFile(url, path)
            Result.success()
        } catch (e: Exception) {
            Log.e("ModelDownloadWorker", "Download failed: ${e.message}")
            Result.failure()
        }
    }

    private fun downloadFile(url: String, destinationPath: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Download failed: ${response.code}")

            File(destinationPath).outputStream().use { output ->
                response.body?.byteStream()?.copyTo(output)
            }
        }
    }

    private companion object {

        const val URL = "url"
        const val PATH = "path"
    }
}
