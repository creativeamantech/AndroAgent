package com.localagent.ui.settings

import androidx.lifecycle.ViewModel
import com.localagent.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _ollamaUrl = MutableStateFlow(settingsRepository.ollamaBaseUrl)
    val ollamaUrl = _ollamaUrl.asStateFlow()

    fun updateOllamaUrl(url: String) {
        settingsRepository.ollamaBaseUrl = url
        _ollamaUrl.value = url
    }
}
