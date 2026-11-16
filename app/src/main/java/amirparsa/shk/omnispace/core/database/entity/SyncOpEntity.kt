package amirparsa.shk.omnispace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_outbox")
data class SyncOpEntity(
    @PrimaryKey
    val opId: String,
    val tableName: String,
    val recordId: String,
    val baseVersion: Long = 0L,
    val payloadJson: String,
    val status: String = "pending", // "pending", "completed", "failed"
    val createdAt: Long = System.currentTimeMillis()
)
