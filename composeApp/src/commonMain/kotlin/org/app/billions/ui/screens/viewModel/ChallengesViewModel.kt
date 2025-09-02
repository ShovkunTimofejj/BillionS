package org.app.billions.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.app.billions.data.local.ChallengeRepositoryImpl
import org.app.billions.data.model.Challenge
import org.app.billions.data.model.ChallengeStatus
import org.app.billions.data.model.ChallengeType
import org.app.billions.data.model.RewardType
import org.app.billions.data.repository.ChallengeRepository

class ChallengesViewModel(
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges

    private val _selectedTab = MutableStateFlow(ChallengeStatus.Active)
    val selectedTab: StateFlow<ChallengeStatus> = _selectedTab

    private val _selectedChallenge = MutableStateFlow<Challenge?>(null)
    val selectedChallenge: StateFlow<Challenge?> = _selectedChallenge

    val filteredChallenges: StateFlow<List<Challenge>> = combine(
        _challenges,
        _selectedTab
    ) { challenges, tab ->
        challenges.filter { it.status == tab }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            challengeRepository.initializeDefaultChallenges()
            loadChallenges()
            recalculateProgressForAllChallenges()
        }
    }

    fun setTab(status: ChallengeStatus) {
        _selectedTab.value = status
    }

    fun recalculateProgressForAllChallenges() {
        viewModelScope.launch {
            val list = challengeRepository.getChallenges()
            println("Recalculating progress for ${list.size} challenges")
            val updated = list.map { challenge ->
                val progress = challengeRepository.calculateProgress(challenge)
                println("Challenge ${challenge.type} progress: $progress")
                challenge.copy(progress = progress)
            }

            _challenges.value = updated
            updated.forEach { challengeRepository.updateChallenge(it) }

            _selectedChallenge.value?.let { selected ->
                val newSelected = updated.find { it.id == selected.id }
                if (newSelected != null) _selectedChallenge.value = newSelected
            }
        }
    }

    fun selectChallenge(challenge: Challenge) {
        _selectedChallenge.value = challenge
    }

    private suspend fun loadChallenges() {
        val list = challengeRepository.getChallenges()
        println("Challenges loaded from repo: ${list.size}")
        list.forEach { println("Challenge: ${it.type}, status: ${it.status}, progress: ${it.progress}") }

        val updated = list.map { challenge ->
            val progress = challengeRepository.calculateProgress(challenge)
            println("Calculated progress for ${challenge.type}: $progress")
            challenge.copy(progress = progress)
        }

        _challenges.value = updated
        println("Updated challenges in ViewModel: ${_challenges.value.size}")
    }

    fun joinChallenge() {
        _selectedChallenge.value?.let { challenge ->
            if (challenge.status == ChallengeStatus.Available) {
                val updated = challenge.copy(status = ChallengeStatus.Active)
                _selectedChallenge.value = updated
                updateChallengeInList(updated)
                viewModelScope.launch { challengeRepository.updateChallenge(updated) }
            }
        }
    }

    fun leaveChallenge() {
        _selectedChallenge.value?.let { challenge ->
            if (challenge.status == ChallengeStatus.Active) {
                val updated = challenge.copy(status = ChallengeStatus.Available, progress = 0.0, daysLeft = challenge.daysLeft)
                _selectedChallenge.value = updated
                updateChallengeInList(updated)
                viewModelScope.launch { challengeRepository.updateChallenge(updated) }
            }
        }
    }

    private fun updateChallengeInList(updated: Challenge) {
        _challenges.value = _challenges.value.map {
            if (it.id == updated.id) updated else it
        }
    }
}

