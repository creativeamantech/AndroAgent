package com.localagent.ui.settings

import androidx.lifecycle.ViewModel
import com.localagent.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PrivacySettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _logSmsCall = MutableStateFlow(settingsRepository.logSmsCall)
    val logSmsCall = _logSmsCall.asStateFlow()

    private val _autoDeleteInterval = MutableStateFlow(settingsRepository.autoDeleteInterval)
    val autoDeleteInterval = _autoDeleteInterval.asStateFlow()

    private val _isStealthMode = MutableStateFlow(settingsRepository.isStealthMode)
    val isStealthMode = _isStealthMode.asStateFlow()

    fun toggleLogSmsCall(enabled: Boolean) {
        settingsRepository.logSmsCall = enabled
        _logSmsCall.value = enabled
    }

    fun updateAutoDeleteInterval(days: Int) {
        settingsRepository.autoDeleteInterval = days
        _autoDeleteInterval.value = days
    }

    fun toggleStealthMode(enabled: Boolean) {
        settingsRepository.isStealthMode = enabled
        _isStealthMode.value = enabled
    }
}
