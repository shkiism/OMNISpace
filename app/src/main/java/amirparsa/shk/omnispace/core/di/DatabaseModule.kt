package amirparsa.shk.omnispace.core.di

import android.content.Context
import androidx.room.Room
import amirparsa.shk.omnispace.core.database.OmniSpaceDatabase
import amirparsa.shk.omnispace.core.database.dao.MeetingDao
import amirparsa.shk.omnispace.core.database.dao.NoteDao
import amirparsa.shk.omnispace.core.database.dao.PersonDao
import amirparsa.shk.omnispace.core.database.dao.ProjectDao
import amirparsa.shk.omnispace.core.database.dao.SyncDao
import amirparsa.shk.omnispace.core.database.dao.TaskDao
import amirparsa.shk.omnispace.core.database.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOmniSpaceDatabase(
        @ApplicationContext context: Context
    ): OmniSpaceDatabase {
        return Room.databaseBuilder(
            context,
            OmniSpaceDatabase::class.java,
            OmniSpaceDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: OmniSpaceDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideProjectDao(database: OmniSpaceDatabase): ProjectDao {
        return database.projectDao()
    }

    @Provides
    @Singleton
    fun providePersonDao(database: OmniSpaceDatabase): PersonDao {
        return database.personDao()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(database: OmniSpaceDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: OmniSpaceDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideMeetingDao(database: OmniSpaceDatabase): MeetingDao {
        return database.meetingDao()
    }

    @Provides
    @Singleton
    fun provideSyncDao(database: OmniSpaceDatabase): SyncDao {
        return database.syncDao()
    }
}
