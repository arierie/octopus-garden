package work.arie.octopusgarden.model

sealed class InitState {
    data object Loading : InitState()
    data object Success : InitState()
    data class Error(val message: String) : InitState()
}
