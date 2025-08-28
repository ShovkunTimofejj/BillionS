package org.app.billions.data.repository

import org.app.billions.data.model.Challenge

interface ChallengeRepository {
    suspend fun getChallenges(): List<Challenge>
    suspend fun updateChallenge(challenge: Challenge)
}