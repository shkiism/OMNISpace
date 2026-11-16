package amirparsa.shk.omnispace.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import amirparsa.shk.omnispace.core.database.entity.TaskEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY id ASC")
    fun getTasksByProject(projectId: String): List<TaskEntity>

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id OR key = :id LIMIT 1")
    fun getTaskByIdOrKey(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tasks: List<TaskEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: TaskEntity): Long

    @Query("DELETE FROM tasks WHERE projectId = :projectId")
    fun deleteByProject(projectId: String): Int

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteTask(id: String): Int

    @Query("DELETE FROM tasks")
    fun clearAll(): Int

    @Query("SELECT COUNT(*) FROM tasks")
    fun getCount(): Int
}
