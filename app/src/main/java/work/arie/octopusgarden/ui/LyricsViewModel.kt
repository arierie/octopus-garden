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
import work.arie.octopusgarden.model.UiState
import javax.inject.Inject

@HiltViewModel
internal class LyricsViewModel @Inject constructor(
    private val inferenceManager: InferenceManager,
) : ViewModel() {

    private val _uiStateFlow: MutableStateFlow<UiState> = MutableStateFlow(UiState.DEFAULT)
    val uiState: StateFlow<UiState> = _uiStateFlow.asStateFlow()

    init {
        inferenceManager.initialize()
    }

    override fun onCleared() {
        super.onCleared()
        inferenceManager.close()
    }

    fun runInference() {
        viewModelScope.launch(Dispatchers.IO) {
            inferenceManager.runInference(_uiStateFlow.value.body)
                .collect { result ->
                    _uiStateFlow.emit(_uiStateFlow.value.copy(body = result))
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
