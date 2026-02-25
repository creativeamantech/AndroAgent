package com.localagent.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localagent.memory.MemoryRepository
import com.localagent.memory.TaskRun
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TaskHistoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    val taskRuns: StateFlow<List<TaskRun>> = memoryRepository.getAllTaskRuns()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
