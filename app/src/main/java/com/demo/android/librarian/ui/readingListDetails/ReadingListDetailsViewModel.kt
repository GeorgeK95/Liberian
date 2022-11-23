package com.demo.android.librarian.ui.readingListDetails

import androidx.lifecycle.*
import com.demo.android.librarian.model.BookItem
import com.demo.android.librarian.model.ReadingList
import com.demo.android.librarian.model.relations.BookAndGenre
import com.demo.android.librarian.model.relations.ReadingListsWithBooks
import com.demo.android.librarian.repository.LibrarianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingListDetailsViewModel @Inject constructor(
  private val repository: LibrarianRepository
) : ViewModel() {

  private val _addBookState = MutableLiveData<List<BookItem>>()
  val addBookState: LiveData<List<BookItem>> = _addBookState

  var readingListState: LiveData<ReadingListsWithBooks> = MutableLiveData()
    private set

  private val _deleteBookState = MutableLiveData<BookAndGenre?>()
  val deleteBookState: LiveData<BookAndGenre?> = _deleteBookState

  fun setReadingList(readingListsWithBooks: ReadingListsWithBooks) {
    readingListState = repository.getReadingListById(readingListsWithBooks.id)
      .asLiveData(viewModelScope.coroutineContext)

    refreshBooks()
  }

  fun refreshBooks() {
    viewModelScope.launch {
      val books = repository.getBooks()
      val readingListBooks = readingListState.value?.books?.map { it.book.id } ?: emptyList()

      val freshBooks = books.filter { it.book.id !in readingListBooks }

      _addBookState.value = freshBooks.map { BookItem(it.book.id, it.book.name, false) }
    }
  }

  fun addBookToReadingList(bookId: String?) {
    val data = readingListState.value

    if (data != null && bookId != null) {
      val bookIds = (data.books.map { it.book.id } + bookId).distinct()

      val newReadingList = ReadingList(
        data.id,
        data.name,
        bookIds
      )

      updateReadingList(newReadingList)
    }
  }

  fun onItemLongTapped(bookAndGenre: BookAndGenre) {
    _deleteBookState.value = bookAndGenre
  }

  fun onDialogDismiss() {
    _deleteBookState.value = null
  }

  fun removeBookFromReadingList(bookId: String) {
    val data = readingListState.value

    if (data != null) {
      val bookIds = data.books.map { it.book.id } - bookId

      val newReadingList = ReadingList(
        data.id,
        data.name,
        bookIds
      )

      updateReadingList(newReadingList)
      onDialogDismiss()
    }
  }

  private fun updateReadingList(newReadingList: ReadingList) {
    viewModelScope.launch {
      repository.updateReadingList(newReadingList)

      refreshBooks()
    }
  }

  fun bookPickerItemSelected(bookItem: BookItem) {
    val books = _addBookState.value ?: return
    val newBooks = books.map { BookItem(it.bookId, it.name, it.bookId == bookItem.bookId) }

    _addBookState.value = newBooks
  }
}