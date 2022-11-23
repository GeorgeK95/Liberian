package com.demo.android.librarian.ui.addReview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.demo.android.librarian.R
import com.demo.android.librarian.model.Review
import com.demo.android.librarian.model.relations.BookAndGenre
import com.demo.android.librarian.model.state.AddBookReviewState
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.addBook.AddBookViewModel
import com.demo.android.librarian.ui.composeUi.*
import com.demo.android.librarian.utils.EMPTY_BOOK_AND_GENRE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddBookReviewActivity : AppCompatActivity(), AddReviewView {

//  @Inject
//  lateinit var repository: LibrarianRepository
//
//  private val _bookReviewState = mutableStateOf(AddBookReviewState())
//  private val _books = mutableStateOf((emptyList<BookAndGenre>()))

  private val viewModel by viewModels<AddReviewViewModel>()

  companion object {
    fun getIntent(context: Context) = Intent(context, AddBookReviewActivity::class.java)
  }

  override fun onReviewAdded() {
    setResult(RESULT_OK)
    finish()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { LibrarianTheme { AddBookReviewContent() } }
//    loadBooks()
  }

  @Composable
  fun AddBookReviewContent() {
    Scaffold(
      topBar = { AddBookReviewTopBar() },
      floatingActionButton = { AddBookReview() }) {
      AddBookReviewForm()
    }
  }

  @Composable
  fun AddBookReview() {
    FloatingActionButton(onClick = {

    }) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Book Review")
    }
  }

  @Composable
  fun AddBookReviewTopBar() {
    TopBar(
      onBackPressed = { onBackPressedDispatcher.onBackPressed() },
      title = stringResource(id = R.string.add_review_title)
    )
  }

  @Composable
  fun AddBookReviewForm() {
//    val bookUrl = remember { mutableStateOf("") }
//    val bookNotes = remember { mutableStateOf("") }
//    val currentRatingFilter = remember { mutableStateOf(0) }
//    val currentlySelectedBook = remember { mutableStateOf(EMPTY_BOOK_AND_GENRE) }

    val books by viewModel.booksState.observeAsState(emptyList())
    val reviewState by viewModel.bookReviewState.observeAsState(AddBookReviewState())

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(state = rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(id = R.string.book_picker_hint),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.onPrimary
      )

      Spacer(modifier = Modifier.height(8.dp))

      SpinnerPicker(
//        pickerText = currentlySelectedBook.value.book.name,
        pickerText = stringResource(id = R.string.book_select),
//        items = _books.value,
        items = books,
        itemToName = { it.book.name },
        onItemPicked = {
//          _bookReviewState.value = _bookReviewState.value.copy(bookAndGenre = it)
          viewModel.onBookPicked(it)
        }
      )

      Spacer(modifier = Modifier.height(8.dp))

      InputField(
//        label = stringResource(id = R.string.book_image_url_input_hint),
        label = stringResource(id = R.string.book_image_url_input_hint),
//        value = bookUrl.value,
        value = reviewState.bookImageUrl,
        onStateChanged = { url ->
//          _bookReviewState.value = _bookReviewState.value.copy(bookImageUrl = url)
//          bookUrl.value = url
          viewModel.onImageUrlChanged(url)
        },
//        isInputValid = bookUrl.value.isNotEmpty()
        isInputValid = reviewState.bookImageUrl.isNotEmpty()
      )

      Spacer(modifier = Modifier.height(16.dp))

      RatingBar(
        range = 1..5,
        isLargeRating = true,
        onRatingChanged = {
//          _bookReviewState.value = _bookReviewState.value.copy(rating = it)
//          currentRatingFilter.value = it
          viewModel.onRatingSelected(it)
        })

      Spacer(modifier = Modifier.height(16.dp))

      InputField(
        label = stringResource(id = R.string.review_notes_hint),
//        value = bookNotes.value,
        value = reviewState.notes,
        onStateChanged = { notes ->
//          _bookReviewState.value = _bookReviewState.value.copy(notes = notes)
//          bookNotes.value = notes
          viewModel.onNotesChanged(notes)
        },
//        isInputValid = bookNotes.value.isNotEmpty()
        isInputValid = reviewState.notes.isNotEmpty()
      )

      Spacer(modifier = Modifier.height(16.dp))

//      val pickedBook = _bookReviewState.value.bookAndGenre

      ActionButton(
        modifier = Modifier.fillMaxWidth(0.7f),
        text = stringResource(id = R.string.add_book_review_text),
        onClick = viewModel::addBookReview,
        /*isEnabled = bookNotes.value.isNotEmpty()
            && bookUrl.value.isNotEmpty()
//            && pickedBook != null
            && pickedBook != EMPTY_BOOK_AND_GENRE*/
        isEnabled = reviewState.bookImageUrl.isNotEmpty() &&
            reviewState.notes.isNotEmpty() &&
            reviewState.bookAndGenre != EMPTY_BOOK_AND_GENRE
      )

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  /*private fun addBookReview() {
    val state = _bookReviewState.value

    lifecycleScope.launch {
      val bookId = state.bookAndGenre.book.id
      val imageUrl = state.bookImageUrl
      val notes = state.notes
      val rating = state.rating

      if (bookId.isNotEmpty() && imageUrl.isNotBlank() && notes.isNotBlank()) {
        val bookReview = Review(
          bookId = bookId,
          rating = rating,
          notes = notes,
          imageUrl = imageUrl,
          lastUpdatedDate = Date(),
          entries = emptyList()
        )
        repository.addReview(bookReview)

        onReviewAdded()
      }
    }
  }*/

  /*private fun loadBooks() {
    lifecycleScope.launch {
      _books.value = repository.getBooks()
    }
  }*/

}