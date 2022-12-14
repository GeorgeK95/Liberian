@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.demo.android.librarian.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.demo.android.librarian.ui.books.ui.BookFilter
import com.demo.android.librarian.ui.books.ui.BooksList
import com.demo.android.librarian.ui.composeUi.DeleteDialog
import com.demo.android.librarian.ui.composeUi.LibrarianTheme
import com.demo.android.librarian.ui.composeUi.TopBar
import com.demo.android.librarian.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val REQUEST_CODE_ADD_BOOK = 201

@AndroidEntryPoint
class BooksFragment : Fragment() {

  /*private val addBookContract by lazy {
    registerForActivityResult(AddBookContract()) { isBookCreated ->
      if (isBookCreated) {
        loadBooks()
        activity?.toast("Book added!")
      }
    }
  }*/

  //  @Inject
//  lateinit var repository: LibrarianRepository
//
//  private val _booksState = mutableStateOf(emptyList<BookAndGenre>())
//  private val _deleteBookState = mutableStateOf<Book?>(null)
//  private val _genresState = mutableStateOf<List<Genre>>(emptyList())
//  var filter: Filter? = null
//
  private var _addBookContract: ManagedActivityResultLauncher<Int, Boolean>? = null

  private val viewModel by viewModels<BooksViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    /*addBookContract*/

    return ComposeView(requireContext()).apply {
      setContent { LibrarianTheme { BooksContent() } }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
//    loadGenres()
//    loadBooks()
    viewModel.loadGenres()
    viewModel.loadBooks()
  }

  @Composable
  fun BooksContent() {
    _addBookContract = rememberLauncherForActivityResult(AddBookContract()) { isBookCreated ->
      if (isBookCreated) {
//        loadBooks()
        viewModel.loadBooks()
        activity?.toast("Book added!")
      }
    }

    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    Scaffold(
      topBar = { BooksTopBar(drawerState) },
      floatingActionButton = { AddNewBook(drawerState) }
    ) {
      BookFilterModalDrawer(drawerState)
    }
  }

  @Composable
  fun BookFilterModalDrawer(drawerState: BottomDrawerState) {
//    val books = _booksState.value
    val books by viewModel.booksState.observeAsState(emptyList())
    val deleteDialogBook by viewModel.deleteBookState.observeAsState()

    val bookToDelete = deleteDialogBook

    BottomDrawer(
      gesturesEnabled = false,
      drawerContent = { BooksFilterModalDrawerContent(drawerState) },
      drawerState = drawerState
    ) {
      Box(
        modifier = Modifier.fillMaxSize()
      ) {
//        val bookToDelete = _deleteBookState.value

        if (bookToDelete != null) {
          DeleteDialog(item = bookToDelete,
            message = stringResource(id = R.string.delete_message, bookToDelete.book.name),
            onDeleteItem = {
              viewModel.removeBook(it.book)
              viewModel.cancelDeleteBook()
            },
            onDismiss = { viewModel.cancelDeleteBook() })
        }

        BooksList(books, onLongItemTap = {
//          _deleteBookState.value = it
          viewModel.showDeleteBook(it)
        })

        if (bookToDelete != null) {
          DeleteDialog(
            item = bookToDelete,
            message = stringResource(id = R.string.delete_message, bookToDelete.book.name),
            onDeleteItem = { bookReview ->
//              removeBook(bookReview)
//              _deleteBookState.value = null
              viewModel.removeBook(bookReview.book)
              viewModel.cancelDeleteBook()
            },
            onDismiss = {
//              _deleteBookState.value = null
              viewModel.cancelDeleteBook()
            }
          )
        }
      }
    }
  }

  @Composable
  fun BooksFilterModalDrawerContent(drawerState: BottomDrawerState) {
    val scope = rememberCoroutineScope()
//    val genres = _genresState.value
    val genres by viewModel.genresState.observeAsState(emptyList())
    val filter = viewModel.filter

    BookFilter(filter = filter, genres = genres, onFilterSelected = {
      scope.launch {
        drawerState.close()
//        filter = it
//        loadBooks()
        viewModel.filter = it
        viewModel.loadBooks()
      }
    })
  }

  @Composable
  fun BooksTopBar(drawerState: BottomDrawerState) {
    TopBar(
      title = stringResource(id = R.string.my_books_title),
      actions = { FilterButton(drawerState) }
    )
    /*TopAppBar(
      title = { Text(text = stringResource(id = R.string.my_books_title)) },
      backgroundColor = colorResource(id = R.color.colorPrimary),
      contentColor = Color.White
    )*/
  }

  @Composable
  fun FilterButton(drawerState: BottomDrawerState) {
    val scope = rememberCoroutineScope()

    IconButton(onClick = {
      scope.launch {
        if (!drawerState.isClosed) {
          drawerState.close()
        } else {
          drawerState.expand()
        }
      }
    }) {
      Icon(Icons.Default.Edit, "", tint = Color.White)
    }
  }

  @Composable
  fun AddNewBook(drawerState: BottomDrawerState) {
    val scope = rememberCoroutineScope()

    FloatingActionButton(onClick = {
      scope.launch {
        drawerState.close()
        showAddBook()
      }
    }) {
      Icon(Icons.Filled.Add, "")
    }
  }

  /*private fun loadGenres() {
    lifecycleScope.launch {
      val genres = repository.getGenres()
      _genresState.value = genres
    }
  }

  private fun loadBooks() {
    lifecycleScope.launch {
      _booksState.value = when (val currentFilter = filter) {
        is ByGenre -> repository.getBooksByGenre(currentFilter.genreId)
        is ByRating -> repository.getBooksByRating(currentFilter.rating)
        else -> repository.getBooks()
      }
    }
  }*/

  /*private fun removeBook(book: Book) {
    lifecycleScope.launch {
      repository.removeBook(book)
      loadBooks()
    }
  }*/

  private fun showAddBook() {
    _addBookContract?.launch(REQUEST_CODE_ADD_BOOK)
  }
}