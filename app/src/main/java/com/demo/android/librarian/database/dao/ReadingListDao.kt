package com.demo.android.librarian.database.dao

import androidx.room.*
import com.demo.android.librarian.model.ReadingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingListDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addReadingList(readingList: ReadingList)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  suspend fun updateReadingList(readingList: ReadingList)

  @Query("SELECT * FROM readinglist")
  suspend fun getReadingLists(): List<ReadingList>

  @Query("SELECT * FROM readinglist")
  fun getReadingListsFlow(): Flow<List<ReadingList>>

  @Delete
  suspend fun removeReadingList(readingList: ReadingList)

  @Delete
  suspend fun removeReadingLists(readingLists: List<ReadingList>)

  @Query("SELECT * FROM readinglist WHERE id = :id")
  fun getReadingListById(id: String): Flow<ReadingList>
}