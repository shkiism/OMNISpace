package amirparsa.shk.omnispace.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import amirparsa.shk.omnispace.core.database.dao.MeetingDao
import amirparsa.shk.omnispace.core.database.dao.NoteDao
import amirparsa.shk.omnispace.core.database.dao.PersonDao
import amirparsa.shk.omnispace.core.database.dao.ProjectDao
import amirparsa.shk.omnispace.core.database.dao.SyncDao
import amirparsa.shk.omnispace.core.database.dao.TaskDao
import amirparsa.shk.omnispace.core.database.dao.UserProfileDao
import amirparsa.shk.omnispace.core.database.entity.MeetingEntity
import amirparsa.shk.omnispace.core.database.entity.NoteEntity
import amirparsa.shk.omnispace.core.database.entity.PersonEntity
import amirparsa.shk.omnispace.core.database.entity.ProjectEntity
import amirparsa.shk.omnispace.core.database.entity.SyncOpEntity
import amirparsa.shk.omnispace.core.database.entity.TaskEntity
import amirparsa.shk.omnispace.core.database.entity.UserProfileEntity

@Database(
    entities = [
        TaskEntity::class,
        ProjectEntity::class,
        PersonEntity::class,
        UserProfileEntity::class,
        NoteEntity::class,
        MeetingEntity::class,
        SyncOpEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class OmniSpaceDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun personDao(): PersonDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun noteDao(): NoteDao
    abstract fun meetingDao(): MeetingDao
    abstract fun syncDao(): SyncDao

    companion object {
        const val DATABASE_NAME = "omnispace_db"
    }
}

