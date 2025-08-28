package org.app.billions.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.app.billions.data.BillionS
import org.app.billions.data.model.Subscription
import org.app.billions.data.model.Units
import org.app.billions.data.model.User
import org.app.billions.data.repository.UserRepository

class UserRepositoryImpl(private val db: BillionS) : UserRepository {

    val queries = db.billionSQueries

    override suspend fun getUser(): User? = withContext(Dispatchers.Default) {
        queries.getUser().executeAsOneOrNull()?.let {
            User(
                id = it.id,
                nickname = it.nickname,
                avatar = it.avatar,
                units = Units.valueOf(it.units),
                timezone = it.timezone,
                subscription = Subscription.valueOf(it.subscription)
            )
        }
    }

    override suspend fun saveUser(user: User) = withContext(Dispatchers.Default) {
        queries.clearUser()
        queries.insertUser(
            nickname = user.nickname,
            avatar = user.avatar,
            units = user.units.name,
            timezone = user.timezone,
            subscription = user.subscription.name
        )
    }
}