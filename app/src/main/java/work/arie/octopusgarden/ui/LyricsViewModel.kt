package work.arie.octopusgarden.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import work.arie.octopusgarden.core.InferenceManager
import work.arie.octopusgarden.model.InferenceState
import work.arie.octopusgarden.model.InitState
import work.arie.octopusgarden.model.UiState
import javax.inject.Inject

@HiltViewModel
internal class LyricsViewModel @Inject constructor(
    private val inferenceManager: InferenceManager,
) : ViewModel() {

    private val _uiStateFlow: MutableStateFlow<UiState> = MutableStateFlow(UiState.DEFAULT)
    val uiState: StateFlow<UiState> = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            inferenceManager.initialize()
                .collect { state ->
                    when (state) {
                        is InitState.Loading -> {
                            _uiStateFlow.emit(_uiStateFlow.value.copy(isLoading = true))
                        }

                        is InitState.Success -> {
                            _uiStateFlow.emit(_uiStateFlow.value.copy(isLoading = false))
                        }

                        is InitState.Error -> {
                            _uiStateFlow.emit(
                                _uiStateFlow.value.copy(
                                    isLoading = false,
                                    errorMessage = state.message
                                )
                            )
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        inferenceManager.close()
    }

    fun runInference() {
        viewModelScope.launch(Dispatchers.IO) {
            inferenceManager.runInference(_uiStateFlow.value.title, _uiStateFlow.value.body)
                .collect { result ->
                    when (result) {
                        is InferenceState.Loading -> {
                            _uiStateFlow.emit(_uiStateFlow.value.copy(isLoading = true))
                        }

                        is InferenceState.Success -> {
                            _uiStateFlow.emit(
                                _uiStateFlow.value.copy(
                                    isLoading = false,
                                    body = result.text
                                )
                            )
                        }

                        is InferenceState.Error -> {
                            _uiStateFlow.emit(
                                _uiStateFlow.value.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                            )
                        }
                    }
                }
        }
    }

    fun updateTitle(newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiStateFlow.emit(_uiStateFlow.value.copy(title = newTitle))
        }
    }

    fun updateBody(newBody: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiStateFlow.emit(_uiStateFlow.value.copy(body = newBody))
        }
    }
}
