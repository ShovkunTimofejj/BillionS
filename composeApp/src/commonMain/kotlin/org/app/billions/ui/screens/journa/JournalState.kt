package org.app.billions.ui.screens.journa

import org.app.billions.data.model.ActivitySample

data class JournalState(
    val entries: List<ActivitySample> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingEntry: ActivitySample? = null,
    val showFilterSheet: Boolean = false,
    val showStatsSheet: Boolean = false,
    val filter: FilterType = FilterType.Today,
    val metric: MetricType = MetricType.Steps,
    val successEvent: Boolean = false,
    val errorMessage: String? = null
)

enum class FilterType { Today, Week, Month, All }


enum class MetricType { Steps, Distance, Calories }