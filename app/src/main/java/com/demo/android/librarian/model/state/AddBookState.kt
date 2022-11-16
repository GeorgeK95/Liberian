package com.demo.android.librarian.model.state

data class AddBookState(
  val name: String = "",
  val description: String = "",
  val genreId: String = ""
) {
  fun getError(): String? {
    return if (name.isEmpty()) {
      "No name"
    } else if (description.isEmpty()) {
      "No description"
    } else if (genreId.isEmpty()) {
      "No genre"
    } else null
  }
}