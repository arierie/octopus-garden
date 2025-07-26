package work.arie.octopusgarden.di

import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import work.arie.octopusgarden.model.Configuration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideModel(): Configuration {
        return Configuration(
            path = "/data/local/tmp/gemma3-1b-it-q8.task",
            url = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.task",
            licenseUrl = "https://huggingface.co/litert-community/Gemma3-1B-IT",
            needsAuth = true,
            preferredBackend = LlmInference.Backend.GPU,
            temperature = 1f,
            topK = 64,
            topP = 0.95f,
            maxToken = 512
        )
    }
}
