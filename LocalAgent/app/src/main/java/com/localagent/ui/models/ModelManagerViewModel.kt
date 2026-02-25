package com.localagent.ui.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.llm.ModelInfo
import com.localagent.llm.OllamaModelManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelManagerViewModel @Inject constructor(
    private val modelManager: OllamaModelManager
) : ViewModel() {

    private val _models = MutableStateFlow<List<ModelInfo>>(emptyList())
    val models = _models.asStateFlow()

    private val _pullStatus = MutableStateFlow<String>("")
    val pullStatus = _pullStatus.asStateFlow()

    private val _isPulling = MutableStateFlow(false)
    val isPulling = _isPulling.asStateFlow()

    init {
        loadModels()
    }

    fun loadModels() {
        viewModelScope.launch {
            _models.value = modelManager.listModels()
        }
    }

    fun pullModel(name: String) {
        viewModelScope.launch {
            _isPulling.value = true
            modelManager.pullModel(name).collect { progress ->
                _pullStatus.value = "${progress.status} ${if (progress.total > 0) "${(progress.completed * 100 / progress.total)}%" else ""}"
                if (progress.status == "success") {
                    _isPulling.value = false
                    loadModels()
                }
            }
            _isPulling.value = false
        }
    }

    fun deleteModel(name: String) {
        viewModelScope.launch {
            if (modelManager.deleteModel(name)) {
                loadModels()
            }
        }
    }
}
