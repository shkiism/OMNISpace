package amirparsa.shk.omnispace.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import amirparsa.shk.omnispace.core.database.entity.ProjectEntity

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects ORDER BY weight ASC, name ASC")
    fun getAllProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(projects: List<ProjectEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(project: ProjectEntity): Long

    @Query("DELETE FROM projects WHERE id = :id")
    fun deleteById(id: String): Int

    @Query("DELETE FROM projects")
    fun clearAll(): Int

    @Query("SELECT COUNT(*) FROM projects")
    fun getCount(): Int
}
