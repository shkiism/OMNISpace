package amirparsa.shk.omnispace.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import amirparsa.shk.omnispace.core.database.entity.SyncOpEntity

@Dao
interface SyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOp(op: SyncOpEntity): Long

    @Query("SELECT * FROM sync_outbox WHERE status = 'pending' ORDER BY createdAt ASC")
    fun getPendingOps(): List<SyncOpEntity>

    @Query("DELETE FROM sync_outbox WHERE opId = :opId")
    fun deleteOp(opId: String): Int

    @Query("DELETE FROM sync_outbox WHERE opId IN (:opIds)")
    fun deleteOps(opIds: List<String>): Int

    @Query("UPDATE sync_outbox SET status = 'completed' WHERE opId = :opId")
    fun markOpCompleted(opId: String): Int

    @Query("UPDATE sync_outbox SET status = 'failed' WHERE opId = :opId")
    fun markOpFailed(opId: String): Int

    @Query("DELETE FROM sync_outbox WHERE status = 'completed'")
    fun clearCompletedOps(): Int
}
