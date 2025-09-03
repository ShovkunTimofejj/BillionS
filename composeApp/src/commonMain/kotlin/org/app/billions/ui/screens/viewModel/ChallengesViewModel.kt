package org.app.billions.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.app.billions.data.local.ChallengeRepositoryImpl
import org.app.billions.data.model.Challenge
import org.app.billions.data.model.ChallengeStatus
import org.app.billions.data.model.ChallengeType
import org.app.billions.data.model.RewardType
import kotlinx.datetime.*
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
        _challenges, _selectedTab
    ) { challenges, tab ->
        challenges.filter { it.status == tab }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            challengeRepository.initializeDefaultChallenges()
            loadChallenges()
            while (isActive) {
                recalculateProgressForAllChallenges()
                delay(5_000L)
            }
        }
    }

    fun setTab(status: ChallengeStatus) {
        _selectedTab.value = status
    }

    fun restartChallenge() {
        _selectedChallenge.value?.let { challenge ->
            if (challenge.status == ChallengeStatus.Completed || challenge.status == ChallengeStatus.Active) {
                val restarted = challenge.copy(
                    status = ChallengeStatus.Active,
                    progress = 0.0,
                    daysLeft = challenge.daysLeft,
                    startDate = Clock.System.now().toEpochMilliseconds()
                )
                viewModelScope.launch {
                    challengeRepository.updateChallenge(restarted)
                    _selectedChallenge.value = restarted
                    updateChallengeInList(restarted)
                }
            }
        }
    }

    fun recalculateProgressForAllChallenges() {
        viewModelScope.launch {
            val list = challengeRepository.getChallenges()

            val updated = list.map { challenge ->
                async {
                    val progress = challengeRepository.calculateProgress(challenge)
                    if (challenge.status == ChallengeStatus.Active && progress >= 1.0) {
                        challenge.copy(progress = 1.0, status = ChallengeStatus.Completed)
                    } else {
                        challenge.copy(progress = progress)
                    }
                }
            }.awaitAll()

            _challenges.value = _challenges.value.map { old ->
                updated.find { it.id == old.id } ?: old
            }

            _selectedChallenge.value?.let { selected ->
                updated.find { it.id == selected.id }?.let { refreshed ->
                    _selectedChallenge.value = refreshed
                }
            }

            updated.forEach { challengeRepository.updateChallenge(it) }
        }
    }

    fun selectChallenge(challenge: Challenge) {
        _selectedChallenge.value = challenge
    }

    private suspend fun loadChallenges() {
        val list = challengeRepository.getChallenges()
        println("Challenges loaded from repo: ${list.size}")
        list.forEach {
            println("Challenge: ${it.type}, status: ${it.status}, progress: ${it.progress}")
        }

        val updated = coroutineScope {
            list.map { challenge ->
                async {
                    val progress = challengeRepository.calculateProgress(challenge)
                    if (challenge.status == ChallengeStatus.Active && progress >= 1.0) {
                        challenge.copy(progress = 1.0, status = ChallengeStatus.Completed)
                    } else {
                        challenge.copy(progress = progress)
                    }
                }
            }.awaitAll()
        }

        _challenges.value = updated
        println("Updated challenges in ViewModel: ${_challenges.value.size}")
    }

    fun joinChallenge() {
        _selectedChallenge.value?.let { challenge ->
            if (challenge.status == ChallengeStatus.Available) {
                val now = Clock.System.now().toEpochMilliseconds()
                val updated = challenge.copy(
                    status = ChallengeStatus.Active,
                    progress = 0.0,
                    startDate = now
                )
                _selectedChallenge.value = updated
                updateChallengeInList(updated)

                viewModelScope.launch {
                    challengeRepository.updateChallenge(updated)
                    val progress = challengeRepository.calculateProgress(updated)
                    val withProgress = updated.copy(progress = progress)
                    updateChallengeInList(withProgress)
                    _selectedChallenge.value = withProgress
                }
            }
        }
    }

    fun leaveChallenge() {
        _selectedChallenge.value?.let { challenge ->
            if (challenge.status == ChallengeStatus.Active) {
                val updated = challenge.copy(
                    status = ChallengeStatus.Available,
                    progress = 0.0,
                    daysLeft = challenge.daysLeft
                )
                _selectedChallenge.value = updated
                updateChallengeInList(updated)
                viewModelScope.launch { challengeRepository.updateChallenge(updated) }
            }
        }
    }

    private fun updateChallengeInList(updated: Challenge) {
        _challenges.value = _challenges.value.map { if (it.id == updated.id) updated else it }
    }
}
