package work.arie.octopusgarden.di

import android.content.Context
import androidx.work.WorkManager
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
            url = "https://huggingface.co/arieridwans/gemma3-1B-it-lyrics-v1/resolve/main/gemma3_1b_it_q8_ekv1280.task",
            licenseUrl = "https://huggingface.co/arieridwans/gemma3-1B-it-lyrics-v1",
            preferredBackend = LlmInference.Backend.CPU,
            temperature = 0.8f,
            topK = 20,
            topP = 0.9f,
            maxToken = 512
        )
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)
}
