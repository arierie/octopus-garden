package work.arie.octopusgarden.model

data class UiState(
    val title: String,
    val body: String
) {

    companion object {

        val DEFAULT = UiState(
            title = "",
            body = ""
        )
    }
}
