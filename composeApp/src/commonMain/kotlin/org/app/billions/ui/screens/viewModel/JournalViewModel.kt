package org.app.billions.ui.screens.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.repository.ActivityRepository
import org.app.billions.ui.screens.journa.FilterType
import org.app.billions.ui.screens.journa.MetricType

class JournalViewModel(
    private val repo: ActivityRepository,
    private val effects: PlatformEffects,
    private val share: PlatformShare,
    private val challengesViewModel: ChallengesViewModel
) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val _state = mutableStateOf(JournalState())
    val state: State<JournalState> = _state

    private val _showAddDialog = mutableStateOf(false)
    val showAddDialog: State<Boolean> = _showAddDialog

    private val _editingEntry = mutableStateOf<ActivitySample?>(null)
    val editingEntry: State<ActivitySample?> = _editingEntry

    private val _selectedEntry = MutableStateFlow<ActivitySample?>(null)
    val selectedEntry: StateFlow<ActivitySample?> = _selectedEntry

    init {
        loadEntries(FilterType.Today)
    }

    fun selectEntry(entry: ActivitySample) {
        _selectedEntry.value = entry
    }

    fun loadEntries(filter: FilterType = _state.value.filter) {
        scope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val entries = when (filter) {
                FilterType.Today -> repo.getSamplesByDate(now.toString())
                FilterType.Week -> repo.getSamplesBetween(
                    now.minus(DatePeriod(days = 7)).toString(),
                    now.toString()
                )
                FilterType.Month -> repo.getSamplesBetween(
                    now.minus(DatePeriod(days = 30)).toString(),
                    now.toString()
                )
                FilterType.All -> repo.getAllSamples()
            }
            val sortedEntries = when (_state.value.metric) {
                MetricType.Steps -> entries.sortedByDescending { it.steps }
                MetricType.Distance -> entries.sortedByDescending { it.distanceMeters }
                MetricType.Calories -> entries.sortedByDescending { it.activeEnergyKcal }
            }
            _state.value = _state.value.copy(
                entries = sortedEntries,
                filter = filter
            )
        }
    }

    fun setMetric(metric: MetricType) {
        _state.value = _state.value.copy(metric = metric)
        loadEntries(_state.value.filter)
    }

    fun showAddEntryDialog(editing: ActivitySample? = null) {
        _editingEntry.value = editing
        _showAddDialog.value = true
    }

    fun hideAddEntryDialog() {
        _editingEntry.value = null
        _showAddDialog.value = false
    }

    fun startEdit(entry: ActivitySample) {
        showAddEntryDialog(entry)
    }

    fun saveEntry(entry: ActivitySample) {
        scope.launch {
            if (entry.steps < 0 || entry.distanceMeters < 0 || entry.activeEnergyKcal < 0) {
                _state.value = _state.value.copy(errorMessage = "Amount must be > 0")
                return@launch
            }

            scope.launch {
                if (entry.id == 0L) {
                    repo.addSample(entry)
                } else {
                    repo.updateSample(entry)
                }

                challengesViewModel.recalculateProgressForAllChallenges()

                hideAddEntryDialog()
                loadEntries(_state.value.filter)
                effects.hapticSuccess()
                _state.value = _state.value.copy(successEvent = true)
            }
        }
    }
    fun deleteEntry(id: Long) {
        scope.launch {
            repo.deleteSample(id)
            challengesViewModel.recalculateProgressForAllChallenges()
            loadEntries(_state.value.filter)
        }
    }
    fun toggleFilterSheet() {
        _state.value = _state.value.copy(showFilterSheet = !_state.value.showFilterSheet)
    }

    fun openStats() {
        _state.value = _state.value.copy(showStatsSheet = true)
    }

    fun closeStats() {
        _state.value = _state.value.copy(showStatsSheet = false)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun selectFilterAndReload(filter: FilterType) {
        loadEntries(filter)
        _state.value = _state.value.copy(showFilterSheet = false)
    }

    fun consumeSuccess() {
        _state.value = _state.value.copy(successEvent = false)
    }

    fun exportCsv() {
        val csv = buildString {
            appendLine("date,type,amount,note,source")
            _state.value.entries.forEach { e ->
                val (type, amount) = when {
                    e.steps > 0 -> "Steps" to e.steps.toString()
                    e.distanceMeters > 0 -> "Distance" to e.distanceMeters.toString()
                    else -> "Calories" to e.activeEnergyKcal.toString()
                }
                appendLine("${e.date},$type,$amount,${e.note},${e.source}")
            }
        }
        val stamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        share.shareTextFile("journal_$stamp.csv", "text/csv", csv)
    }
}
data class JournalState(
    val entries: List<ActivitySample> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingEntry: ActivitySample? = null,
    val filter: FilterType = FilterType.Today,
    val metric: MetricType = MetricType.Steps,
    val showFilterSheet: Boolean = false,
    val showStatsSheet: Boolean = false,
    val errorMessage: String? = null,
    val successEvent: Boolean = false
)

interface PlatformEffects { fun hapticSuccess(); fun hapticError() }
interface PlatformShare { fun shareTextFile(filename: String, mime: String, content: String) }

class NoOpEffects : PlatformEffects { override fun hapticSuccess() {} override fun hapticError() {} }
class NoOpShare : PlatformShare { override fun shareTextFile(filename: String, mime: String, content: String) {} }
