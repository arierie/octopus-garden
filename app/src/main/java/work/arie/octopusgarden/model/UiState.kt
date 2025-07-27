package work.arie.octopusgarden.model

data class UiState(
    val title: String,
    val body: String,
    val errorMessage: String,
    val isLoading: Boolean
) {

    companion object {

        val DEFAULT = UiState(
            title = "",
            body = "",
            errorMessage = "",
            isLoading = false
        )
    }
}
