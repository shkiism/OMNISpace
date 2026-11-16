package amirparsa.shk.omnispace.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import amirparsa.shk.omnispace.core.database.entity.MeetingEntity

@Dao
interface MeetingDao {

    @Query("SELECT * FROM meetings LIMIT 1")
    fun getUpcomingMeeting(): MeetingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeeting(meeting: MeetingEntity): Long

    @Query("DELETE FROM meetings")
    fun clearAll(): Int
}
