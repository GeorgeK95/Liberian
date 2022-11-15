package com.demo.android.librarian.model.relations

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.demo.android.librarian.model.Book
import com.demo.android.librarian.model.Genre
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookAndGenre(
  @Embedded
  val book: Book,
  @Relation(parentColumn = "bookGenreId", entityColumn = "id")
  val genre: Genre
) : Parcelable