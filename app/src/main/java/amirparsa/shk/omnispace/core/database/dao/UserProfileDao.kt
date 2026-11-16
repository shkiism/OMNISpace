package amirparsa.shk.omnispace.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import amirparsa.shk.omnispace.core.database.entity.UserProfileEntity

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profiles ORDER BY lastUpdated DESC LIMIT 1")
    fun getCurrentUserProfile(): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE id = :userId LIMIT 1")
    fun getUserProfileById(userId: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(profile: UserProfileEntity): Long

    @Query("DELETE FROM user_profiles")
    fun clearAll(): Int
}
