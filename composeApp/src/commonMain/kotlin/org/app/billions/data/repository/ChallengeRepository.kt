package org.app.billions.data.repository

import app.cash.sqldelight.db.QueryResult
import org.app.billions.data.model.Challenge

interface ChallengeRepository {
    suspend fun getChallenges(): List<Challenge>
    suspend fun updateChallenge(challenge: Challenge): QueryResult<Long>
    suspend fun calculateProgress(challenge: Challenge): Double
    suspend fun initializeDefaultChallenges()

}
