package org.app.billions.data.repository

import app.cash.sqldelight.db.QueryResult
import org.app.billions.data.model.User

interface UserRepository {
    suspend fun getUser(): User?
    suspend fun saveUser(user: User): QueryResult<Long>
}