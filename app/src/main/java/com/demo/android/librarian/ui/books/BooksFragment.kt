package com.demo.android.librarian.ui.books

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.demo.android.librarian.R
import com.demo.android.librarian.model.Book
import com.demo.android.librarian.model.Genre
import com.demo.android.librarian.model.relations.BookAndGenre
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.books.filter.ByGenre
import com.demo.android.librarian.ui.books.filter.ByRating
import com.demo.android.librarian.ui.books.filter.Filter
import com.demo.android.librarian.utils.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val REQUEST_CODE_ADD_BOOK = 201

@AndroidEntryPoint
class BooksFragment : Fragment() {

  private val addBookContract by lazy {
    registerForActivityResult(AddBookContract()) { isBookCreated ->
      if (isBookCreated) {
        loadBooks()
        activity?.toast("Book added!")
      }
    }
  }

  @Inject
  lateinit var repository: LibrarianRepository

  private val _booksState = MutableLiveData(emptyList<BookAndGenre>())
  private val _genresState = MutableLiveData<List<Genre>>()
  var filter: Filter? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    addBookContract

    return ComposeView(requireContext()).apply {
      setContent {
        BooksContent()
      }
    }
  }

  @Composable
  fun BooksContent() {
    Scaffold(topBar = { BooksTopBar() }, floatingActionButton = { AddNewBook() }) {

    }
  }

  @Composable
  fun BooksTopBar() {
    TopAppBar(
      title = { Text(text = stringResource(id = R.string.my_books_title)) },
      backgroundColor = colorResource(id = R.color.colorPrimary),
      contentColor = Color.White
    )
  }


  @Composable
  @Preview
  fun AddNewBook() {
    FloatingActionButton(onClick = { showAddBook() }) { Icon(Icons.Filled.Add, "") }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loadGenres()
    loadBooks()
  }

  fun loadGenres() {
    lifecycleScope.launch {
      val genres = repository.getGenres()

      _genresState.value = genres
    }
  }

  fun loadBooks() {
    lifecycleScope.launch {

      val books = when (val currentFilter = filter) {
        is ByGenre -> repository.getBooksByGenre(currentFilter.genreId)
        is ByRating -> repository.getBooksByRating(currentFilter.rating)
        else -> repository.getBooks()
      }

      _booksState.value = books
    }
  }

  fun removeBook(book: Book) {
    lifecycleScope.launch {
      repository.removeBook(book)
      loadBooks()
    }
  }

  private fun showAddBook() {
    addBookContract.launch(REQUEST_CODE_ADD_BOOK)
  }
}