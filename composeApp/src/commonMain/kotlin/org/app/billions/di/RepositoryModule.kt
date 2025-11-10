package org.app.billions.di

import org.app.billions.data.BillionS
import org.app.billions.data.local.ActivityRepositoryImpl
import org.app.billions.data.local.ChallengeRepositoryImpl
import org.app.billions.data.local.ThemeRepositoryImpl
import org.app.billions.data.local.UserRepositoryImpl
import org.app.billions.data.repository.ActivityRepository
import org.app.billions.data.repository.ChallengeRepository
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.data.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get<BillionS>()) }
    single<ActivityRepository> { ActivityRepositoryImpl(get<BillionS>()) }
    single<ChallengeRepository> {
        ChallengeRepositoryImpl(
            activityRepository = get(),
            db = get()
        )
    }
    single<ThemeRepository> { ThemeRepositoryImpl(get<BillionS>()) }
}