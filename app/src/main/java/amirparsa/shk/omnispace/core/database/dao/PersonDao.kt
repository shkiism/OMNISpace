package amirparsa.shk.omnispace.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import amirparsa.shk.omnispace.core.database.entity.PersonEntity

@Dao
interface PersonDao {

    @Query("SELECT * FROM people ORDER BY displayName ASC")
    fun getAllPeople(): List<PersonEntity>

    @Query("SELECT * FROM people WHERE id = :id LIMIT 1")
    fun getPersonById(id: String): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(people: List<PersonEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(person: PersonEntity): Long

    @Query("DELETE FROM people")
    fun clearAll(): Int

    @Query("SELECT COUNT(*) FROM people")
    fun getCount(): Int
}
