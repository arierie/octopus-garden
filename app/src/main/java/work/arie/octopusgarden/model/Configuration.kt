package work.arie.octopusgarden.model

import com.google.mediapipe.tasks.genai.llminference.LlmInference

data class Configuration(
    val path: String,
    val url: String,
    val licenseUrl: String,
    val preferredBackend: LlmInference.Backend?,
    val temperature: Float,
    val topK: Int,
    val topP: Float,
    val maxToken: Int = 2048
)
