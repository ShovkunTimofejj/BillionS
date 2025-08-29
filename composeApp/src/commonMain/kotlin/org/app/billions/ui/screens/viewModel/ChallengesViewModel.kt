package org.app.billions.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        viewModelScope.launch {
            loadChallenges()
        }
    }

    fun setTab(status: ChallengeStatus) {
        _selectedTab.value = status
    }

    fun selectChallenge(challenge: Challenge) {
        _selectedChallenge.value = challenge
    }

    private suspend fun loadChallenges() {
        val list = challengeRepository.getChallenges()
        val updated = if (list.isEmpty()) {
            listOf(
                Challenge(
                    id = 1,
                    type = ChallengeType.Marathon30,
                    status = ChallengeStatus.Active,
                    progress = 0.4,
                    goal = 300_000.0,
                    daysLeft = 30,
                    reward = RewardType.Bronze
                ),
                Challenge(
                    id = 2,
                    type = ChallengeType.Sprint7,
                    status = ChallengeStatus.Available,
                    progress = 0.0,
                    goal = 70_000.0,
                    daysLeft = 7,
                    reward = RewardType.Silver
                ),
                Challenge(
                    id = 3,
                    type = ChallengeType.StreakBuilder14,
                    status = ChallengeStatus.Completed,
                    progress = 1.0,
                    goal = 70_000.0,
                    daysLeft = 0,
                    reward = RewardType.Gold
                )
            )
        } else {
            list.map { challenge ->
                val progress = challengeRepository.calculateProgress(challenge)
                challenge.copy(progress = progress)
            }
        }

        _challenges.value = updated
    }

//    private suspend fun loadChallenges() {
//        val list = challengeRepository.getChallenges()
//        val updated = list.map { challenge ->
//            val progress = challengeRepository.calculateProgress(challenge)
//            challenge.copy(progress = progress)
//        }
//        _challenges.value = updated
//    }

    fun getFilteredChallenges(): List<Challenge> {
        return _challenges.value.filter { it.status == _selectedTab.value }
    }
}
