package amirparsa.shk.omnispace.core.di

import amirparsa.shk.omnispace.core.network.AuthRepository
import amirparsa.shk.omnispace.core.network.AuthRepositoryImpl
import amirparsa.shk.omnispace.feature.connect.data.repository.ConnectRepositoryImpl
import amirparsa.shk.omnispace.feature.connect.domain.repository.ConnectRepository
import amirparsa.shk.omnispace.feature.growth.data.repository.GrowthRepositoryImpl
import amirparsa.shk.omnispace.feature.growth.domain.repository.GrowthRepository
import amirparsa.shk.omnispace.feature.notification.data.repository.NotificationRepositoryImpl
import amirparsa.shk.omnispace.feature.notification.domain.repository.NotificationRepository
import amirparsa.shk.omnispace.feature.search.data.repository.SearchRepositoryImpl
import amirparsa.shk.omnispace.feature.search.domain.repository.SearchRepository
import amirparsa.shk.omnispace.feature.today.data.repository.TodayRepositoryImpl
import amirparsa.shk.omnispace.feature.today.domain.repository.TodayRepository
import amirparsa.shk.omnispace.feature.wiki.data.repository.WikiRepositoryImpl
import amirparsa.shk.omnispace.feature.wiki.domain.repository.WikiRepository
import amirparsa.shk.omnispace.feature.work.data.repository.WorkRepositoryImpl
import amirparsa.shk.omnispace.feature.work.domain.repository.WorkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTodayRepository(impl: TodayRepositoryImpl): TodayRepository

    @Binds
    @Singleton
    abstract fun bindGrowthRepository(impl: GrowthRepositoryImpl): GrowthRepository

    @Binds
    @Singleton
    abstract fun bindConnectRepository(impl: ConnectRepositoryImpl): ConnectRepository

    @Binds
    @Singleton
    abstract fun bindWorkRepository(impl: WorkRepositoryImpl): WorkRepository

    @Binds
    @Singleton
    abstract fun bindWikiRepository(impl: WikiRepositoryImpl): WikiRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
}

