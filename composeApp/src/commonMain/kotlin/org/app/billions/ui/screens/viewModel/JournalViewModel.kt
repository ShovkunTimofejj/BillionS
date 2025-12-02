package org.app.billions.ui.screens.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.model.DailyGoals
import org.app.billions.data.repository.ActivityRepository
import org.app.billions.exportCsv.ShareManager
import org.app.billions.ui.screens.journa.FilterType
import org.app.billions.ui.screens.journa.MetricType

class JournalViewModel(
    private val repo: ActivityRepository,
    private val effects: PlatformEffects,
    private val share: ShareManager,
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

    private val _dailyGoals = mutableStateOf(DailyGoals())
    val dailyGoals: State<DailyGoals> = _dailyGoals
    private val _allEntries = mutableStateOf<List<ActivitySample>>(emptyList())
    val allEntries: State<List<ActivitySample>> = _allEntries

    init {
        loadEntries(FilterType.Today)
        loadDailyGoals()
    }

    fun loadAllEntries() {
        scope.launch {
            val entries = repo.getAllSamples()
            _allEntries.value = entries
        }
    }

    fun selectEntry(entry: ActivitySample) {
        _selectedEntry.value = entry
    }

    private fun loadDailyGoals() {
        scope.launch {
            val goals = repo.getDailyGoals()
            if (goals != null) {
                _dailyGoals.value = goals
            }
        }
    }

    fun updateGoals(goals: DailyGoals) {
        scope.launch {
            repo.saveDailyGoals(goals)
            _dailyGoals.value = goals
        }
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
                entries = sortedEntries.toMutableStateList(),
                filter = filter
            )
        }
    }

    fun setMetric(metric: MetricType) {
        _state.value = _state.value.copy(metric = metric)
        loadEntries(_state.value.filter)
    }

    fun showAddEntryDialog(
        type: String = "steps",
        prefill: ActivitySample? = null
    ) {
        _editingEntry.value = null
        _showAddDialog.value = true

        _state.value = _state.value.copy(
            activeAddType = type,
            prefillSample = prefill
        )
    }
    fun adjustEntry(type: String, delta: Double) {
        scope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val currentEntries = repo.getSamplesByDate(today.toString()).toMutableList()
            val existing = currentEntries.find { it.date.date == today }

            val updated = when {
                existing != null -> {
                    existing.copy(
                        steps = (existing.steps + if (type == "steps") delta.toLong() else 0L).coerceAtLeast(0),
                        distanceMeters = (existing.distanceMeters + if (type == "distance") delta else 0.0).coerceAtLeast(0.0),
                        activeEnergyKcal = (existing.activeEnergyKcal + if (type == "calories") delta else 0.0).coerceAtLeast(0.0)
                    )
                }
                else -> ActivitySample(
                    id = 0,
                    date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    steps = if (type == "steps") delta.toLong() else 0L,
                    distanceMeters = if (type == "distance") delta else 0.0,
                    activeEnergyKcal = if (type == "calories") delta else 0.0,
                    source = "manual",
                    note = ""
                )
            }

            if (existing != null) {
                repo.updateSample(updated)
            } else {
                repo.addSample(updated)
            }

            loadEntries(FilterType.Today)
        }
    }

    fun hideAddEntryDialog() {
        _editingEntry.value = null
        _showAddDialog.value = false
    }

    fun startEdit(entry: ActivitySample) {
        val type = when {
            entry.steps > 0 -> "steps"
            entry.distanceMeters > 0 -> "distance"
            entry.activeEnergyKcal > 0 -> "calories"
            else -> "steps"
        }

        _editingEntry.value = entry
        _showAddDialog.value = true

        _state.value = _state.value.copy(
            activeAddType = type,
            prefillSample = null
        )
    }

    fun saveEntry(entry: ActivitySample) {
        scope.launch {
            if (entry.steps < 0 || entry.distanceMeters < 0 || entry.activeEnergyKcal < 0) {
                _state.value = _state.value.copy(errorMessage = "Amount must be > 0")
                return@launch
            }

            if (entry.id == 0L) {
                repo.addSample(entry)
            } else {
                repo.updateSample(entry)
            }

            challengesViewModel.recalculateProgressForAllChallenges()

            val filter = _state.value.filter
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val updatedEntries = when (filter) {
                FilterType.Today -> repo.getSamplesByDate(now.toString())
                FilterType.Week -> repo.getSamplesBetween(
                    now.minus(DatePeriod(days = 7)).toString(), now.toString()
                )
                FilterType.Month -> repo.getSamplesBetween(
                    now.minus(DatePeriod(days = 30)).toString(), now.toString()
                )
                FilterType.All -> repo.getAllSamples()
            }

            val sorted = when (_state.value.metric) {
                MetricType.Steps -> updatedEntries.sortedByDescending { it.steps }
                MetricType.Distance -> updatedEntries.sortedByDescending { it.distanceMeters }
                MetricType.Calories -> updatedEntries.sortedByDescending { it.activeEnergyKcal }
            }

            _state.value = _state.value.copy(
                entries = sorted.toMutableStateList(),
                successEvent = true
            )

            effects.hapticSuccess()
            hideAddEntryDialog()
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

        val stamp = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        share.shareTextFile("journal_$stamp.csv", "text/csv", csv)
    }
}
data class JournalState(
    val entries: SnapshotStateList<ActivitySample> = mutableStateListOf(),
    val showAddDialog: Boolean = false,
    val editingEntry: ActivitySample? = null,
    val filter: FilterType = FilterType.Today,
    val metric: MetricType = MetricType.Steps,
    val showFilterSheet: Boolean = false,
    val showStatsSheet: Boolean = false,
    val errorMessage: String? = null,
    val successEvent: Boolean = false,
    val activeAddType: String? = null,
    val prefillSample: ActivitySample? = null
)

interface PlatformEffects { fun hapticSuccess(); fun hapticError() }
interface PlatformShare { fun shareTextFile(filename: String, mime: String, content: String) }

class NoOpEffects : PlatformEffects { override fun hapticSuccess() {} override fun hapticError() {} }
class NoOpShare : PlatformShare { override fun shareTextFile(filename: String, mime: String, content: String) {} }
