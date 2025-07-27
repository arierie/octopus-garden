package work.arie.octopusgarden.core

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import work.arie.octopusgarden.model.Configuration
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.max
import androidx.core.net.toUri

@Singleton
internal class InferenceManager @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val configuration: Configuration,
) {

    private lateinit var llmInference: LlmInference
    private lateinit var llmInferenceSession: LlmInferenceSession

    fun initialize() {
        if (!modelExists()) {
            throw Throwable("Model not found at path: ${configuration.path}")
        }

        createEngine()
        createSession()
    }

    fun close() {
        llmInferenceSession.close()
        llmInference.close()
    }

    fun runInference(title: String, firstLine: String): Flow<String> = channelFlow {
        try {
            suspendCancellableCoroutine { continuation ->
                var output = ""
                val asyncInference = generateResponseAsync(title, firstLine) { partialResult, isDone ->
                        output += partialResult
                        trySend(output)
                        if (isDone) {
                            continuation.resume(Unit)
                        }
                    }

                continuation.invokeOnCancellation {
                    asyncInference.cancel(true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Inference error: ${e.message}", e)
            send(firstLine)
        }
    }.flowOn(Dispatchers.IO)

    private fun generateResponseAsync(
        title: String,
        firstLine: String,
        progressListener: ProgressListener<String>
    ) : ListenableFuture<String> {
        val formattedPrompt = String.format(SYSTEM_PROMPT, title, firstLine)
        Log.e(TAG, "generateResponseAsync: $formattedPrompt")
        llmInferenceSession.addQueryChunk(formattedPrompt)
        return llmInferenceSession.generateResponseAsync(progressListener)
    }

    private fun createEngine() {
        val inferenceOptions = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelPath())
            .setMaxTokens(configuration.maxToken)
            .apply { configuration.preferredBackend?.let { setPreferredBackend(it) } }
            .build()

        try {
            llmInference = LlmInference.createFromOptions(context, inferenceOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Load model error: ${e.message}", e)
            throw Throwable("Failed to load model, please try again")
        }
    }

    private fun createSession() {
        val sessionOptions = LlmInferenceSession.LlmInferenceSessionOptions.builder()
            .setTemperature(configuration.temperature)
            .setTopK(configuration.topK)
            .setTopP(configuration.topP)
            .build()

        try {
            llmInferenceSession =
                LlmInferenceSession.createFromOptions(llmInference, sessionOptions)
        } catch (e: Exception) {
            Log.e(TAG, "LlmInferenceSession create error: ${e.message}", e)
            throw Throwable("Failed to create model session, please try again")
        }
    }

    private fun modelPathFromUrl(context: Context): String {
        if (configuration.url.isNotEmpty()) {
            val urlFileName = configuration.url.toUri().lastPathSegment
            if (!urlFileName.isNullOrEmpty()) {
                return File(context.filesDir, urlFileName).absolutePath
            }
        }

        return ""
    }

    private fun modelPath(): String {
        val modelFile = File(configuration.path)
        if (modelFile.exists()) {
            return configuration.path
        }

        return modelPathFromUrl(context)
    }

    private fun modelExists(): Boolean {
        return File(modelPath()).exists()
    }

    private companion object {

        const val TAG = "InferenceManager"
        const val SYSTEM_PROMPT = "Write the lyrics with the title: %s and start with: %s"
    }
}
