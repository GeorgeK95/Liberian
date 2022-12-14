package com.demo.android.librarian.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.demo.android.librarian.database.converters.DateConverter
import com.demo.android.librarian.database.converters.ReadingEntryConverter
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "reviews")
@Parcelize
data class Review(
  @PrimaryKey
  val id: String = UUID.randomUUID().toString(),
  val bookId: String,
  val rating: Int,
  val notes: String,
  val imageUrl: String,
  @TypeConverters(DateConverter::class)
  val lastUpdatedDate: Date,
  @TypeConverters(ReadingEntryConverter::class)
  val entries: List<ReadingEntry>
) : Parcelable