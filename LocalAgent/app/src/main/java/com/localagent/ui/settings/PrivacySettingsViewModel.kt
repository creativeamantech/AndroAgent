package com.localagent.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.data.repository.SettingsRepository
import com.localagent.privacy.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacySettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _logSmsCall = MutableStateFlow(settingsRepository.logSmsCall)
    val logSmsCall = _logSmsCall.asStateFlow()

    private val _autoDeleteInterval = MutableStateFlow(settingsRepository.autoDeleteInterval)
    val autoDeleteInterval = _autoDeleteInterval.asStateFlow()

    private val _isStealthMode = MutableStateFlow(settingsRepository.isStealthMode)
    val isStealthMode = _isStealthMode.asStateFlow()

    private val _backupStatus = MutableStateFlow("")
    val backupStatus = _backupStatus.asStateFlow()

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

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _backupStatus.value = "Exporting..."
            val success = backupManager.exportBackup(uri)
            _backupStatus.value = if (success) "Export Successful" else "Export Failed"
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _backupStatus.value = "Importing..."
            val success = backupManager.importBackup(uri)
            _backupStatus.value = if (success) "Import Successful (Restart App)" else "Import Failed"
        }
    }
}
