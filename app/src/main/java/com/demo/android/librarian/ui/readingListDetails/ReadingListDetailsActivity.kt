@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.demo.android.librarian.ui.readingListDetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.demo.android.librarian.model.BookItem
import com.demo.android.librarian.model.relations.ReadingListsWithBooks
import com.demo.android.librarian.ui.books.ui.BooksList
import com.demo.android.librarian.ui.composeUi.DeleteDialog
import com.demo.android.librarian.ui.composeUi.LibrarianTheme
import com.demo.android.librarian.ui.composeUi.TopBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.demo.android.librarian.R
import com.demo.android.librarian.ui.readingListDetails.ui.BookPicker

@AndroidEntryPoint
class ReadingListDetailsActivity : AppCompatActivity() {

//  @Inject
//  lateinit var repository: LibrarianRepository
//
//  private val _addBookState = MutableLiveData<List<BookItem>>()
//  private var readingListState: LiveData<ReadingListsWithBooks> = MutableLiveData()

  private val readingListDetailsViewModel by viewModels<ReadingListDetailsViewModel>()

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
//      setReadingList(readingList)
      readingListDetailsViewModel.setReadingList(readingList)
    } else {
      finish()
      return
    }

    setContent {
      LibrarianTheme {
        ReadingListDetailsContent()
      }
    }
  }

  @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
  @Composable
  fun ReadingListDetailsContent() {
    val readingListState by readingListDetailsViewModel.readingListState.observeAsState()
    val bottomDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    Scaffold(
      topBar = { ReadingListDetailsTopBar(readingListState) },
      floatingActionButton = { AddBookToReadingList(bottomDrawerState) }
    ) {
      ReadingListDetailsModalDrawer(bottomDrawerState, readingListState)
    }
  }

  @ExperimentalMaterialApi
  @Composable
  fun AddBookToReadingList(bottomDrawerState: BottomDrawerState) {
    val coroutineScope = rememberCoroutineScope()

    FloatingActionButton(onClick = {
      if (bottomDrawerState.isClosed) {
        readingListDetailsViewModel.refreshBooks()

        coroutineScope.launch {
          bottomDrawerState.expand()
        }
      }
    }) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add Books",
        tint = MaterialTheme.colors.onSecondary
      )
    }
  }

  @Composable
  fun ReadingListDetailsTopBar(readingList: ReadingListsWithBooks?) {
    val title = readingList?.name ?: stringResource(id = R.string.reading_list)

    TopBar(title = title, onBackPressed = { onBackPressed() })
  }

  @ExperimentalFoundationApi
  @ExperimentalMaterialApi
  @Composable
  fun ReadingListDetailsModalDrawer(
    drawerState: BottomDrawerState,
    readingList: ReadingListsWithBooks?
  ) {
    val deleteBookState by readingListDetailsViewModel.deleteBookState.observeAsState()
    val addBookState by readingListDetailsViewModel.addBookState.observeAsState(emptyList())

    val bookToDelete = deleteBookState

    BottomDrawer(
      modifier = Modifier.fillMaxWidth(),
      drawerState = drawerState,
      gesturesEnabled = false,
      drawerContent = {
        ReadingListDetailsModalDrawerContent(
          modifier = Modifier.align(Alignment.CenterHorizontally),
          drawerState = drawerState,
          addBookState
        )
      }) {
      Box(
        modifier =
        Modifier
          .fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        BooksList(
          readingList?.books ?: emptyList(),
          onLongItemTap = { book -> readingListDetailsViewModel.onItemLongTapped(book) }
        )

        if (bookToDelete != null) {
          DeleteDialog(
            item = bookToDelete,
            message = stringResource(id = R.string.delete_message, bookToDelete.book.name),
            onDeleteItem = {
              readingListDetailsViewModel.removeBookFromReadingList(it.book.id)
              readingListDetailsViewModel.onDialogDismiss()
            },
            onDismiss = { readingListDetailsViewModel.onDialogDismiss() }
          )
        }
      }
    }
  }

  @ExperimentalMaterialApi
  @Composable
  fun ReadingListDetailsModalDrawerContent(
    modifier: Modifier,
    drawerState: BottomDrawerState,
    addBookState: List<BookItem>
  ) {
    val coroutineScope = rememberCoroutineScope()

    BookPicker(
      modifier = modifier,
      books = addBookState,
      onBookSelected = { readingListDetailsViewModel.bookPickerItemSelected(it) },
      onBookPicked = {
        readingListDetailsViewModel.addBookToReadingList(addBookState.firstOrNull { it.isSelected }?.bookId)

        coroutineScope.launch { drawerState.close() }
      }, onDismiss = { coroutineScope.launch { drawerState.close() } })
  }

  /*private fun setReadingList(readingListsWithBooks: ReadingListsWithBooks) {
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
*/
}