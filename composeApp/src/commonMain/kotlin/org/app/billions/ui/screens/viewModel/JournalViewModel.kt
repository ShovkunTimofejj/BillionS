package org.app.billions.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.repository.ActivityRepository

class JournalViewModel(
    private val repo: ActivityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JournalState())
    val state = _state.asStateFlow()

    fun loadEntries() {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
            val entries = repo.getSamplesByDate(today)
            _state.update { it.copy(entries = entries) }
        }
    }

    fun showAddEntryDialog() { _state.update { it.copy(showAddDialog = true) } }
    fun hideAddEntryDialog() { _state.update { it.copy(showAddDialog = false) } }

    fun saveEntry(entry: ActivitySample) {
        viewModelScope.launch {
            repo.addSample(entry)
            hideAddEntryDialog()
            loadEntries()
            // TODO: haptic + confetti
        }
    }

    fun deleteEntry(id: Long) {
        // TODO: implement delete in repo
    }

    fun editEntry(entry: ActivitySample) {
        // TODO: open Edit dialog
    }

    fun toggleFilter() {
        // TODO: open filter sheet
    }
}

data class JournalState(
    val entries: List<ActivitySample> = emptyList(),
    val showAddDialog: Boolean = false
)
