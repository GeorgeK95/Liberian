package com.demo.android.librarian.model.state

import com.demo.android.librarian.model.relations.BookAndGenre
import com.demo.android.librarian.utils.EMPTY_BOOK_AND_GENRE

data class AddBookReviewState(
  val bookAndGenre: BookAndGenre = EMPTY_BOOK_AND_GENRE,
  val bookImageUrl: String = "",
  val rating: Int = 0,
  val notes: String = ""
)