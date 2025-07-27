package work.arie.octopusgarden.model

sealed class InferenceState {
    data object Loading : InferenceState()
    data class Success(val text: String) : InferenceState()
    data class Error(val message: String) : InferenceState()
}
