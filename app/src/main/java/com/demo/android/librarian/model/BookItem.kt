package com.demo.android.librarian.model

data class BookItem(
  val bookId: String,
  val name: String,
  var isSelected: Boolean = false
)