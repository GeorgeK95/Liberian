package com.demo.android.librarian.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.demo.android.librarian.model.Book
import com.demo.android.librarian.model.Genre

class BooksByGenre(
  @Embedded
  val genre: Genre,
  @Relation(
    parentColumn = "id",
    entityColumn = "bookGenreId"
  )
  val books: List<Book>?
)