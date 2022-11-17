package com.demo.android.librarian.ui.readingListDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.demo.android.librarian.model.BookItem
import com.demo.android.librarian.model.ReadingList
import com.demo.android.librarian.model.relations.ReadingListsWithBooks
import com.demo.android.librarian.repository.LibrarianRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReadingListDetailsActivity : AppCompatActivity() {

  @Inject
  lateinit var repository: LibrarianRepository

  private val _addBookState = MutableLiveData<List<BookItem>>()
  private var readingListState: LiveData<ReadingListsWithBooks> = MutableLiveData()

  companion object {
    private const val KEY_READING_LIST = "reading_list"

    fun getIntent(context: Context, readingList: ReadingListsWithBooks): Intent {
      val intent = Intent(context, ReadingListDetailsActivity::class.java)

      intent.putExtra(KEY_READING_LIST, readingList)
      return intent
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val readingList = intent.getParcelableExtra<ReadingListsWithBooks>(KEY_READING_LIST)

    if (readingList != null) {
      setReadingList(readingList)
    } else {
      finish()
      return
    }
  }

  private fun setReadingList(readingListsWithBooks: ReadingListsWithBooks) {
    readingListState = repository.getReadingListById(readingListsWithBooks.id)
      .asLiveData(lifecycleScope.coroutineContext)

    refreshBooks()
  }

  private fun refreshBooks() {
    lifecycleScope.launch {
      val books = repository.getBooks()
      val readingListBooks = readingListState.value?.books?.map { it.book.id } ?: emptyList()

      val freshBooks = books.filter { it.book.id !in readingListBooks }

      _addBookState.value = freshBooks.map { BookItem(it.book.id, it.book.name, false) }
    }
  }

  private fun addBookToReadingList(bookId: String?) {
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

  private fun removeBookFromReadingList(bookId: String) {
    val data = readingListState.value

    if (data != null) {
      val bookIds = data.books.map { it.book.id } - bookId

      val newReadingList = ReadingList(
        data.id,
        data.name,
        bookIds
      )

      updateReadingList(newReadingList)
    }
  }

  private fun updateReadingList(newReadingList: ReadingList) {
    lifecycleScope.launch {
      repository.updateReadingList(newReadingList)

      refreshBooks()
    }
  }

}