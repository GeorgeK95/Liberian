package com.demo.android.librarian.ui.bookReviewDetails

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.demo.android.librarian.R
import com.demo.android.librarian.model.ReadingEntry
import com.demo.android.librarian.model.Review
import com.demo.android.librarian.model.relations.BookReview
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.bookReviewDetails.anim.*
import com.demo.android.librarian.ui.bookReviewDetails.readingEntries.AddReadingEntryDialog
import com.demo.android.librarian.ui.composeUi.DeleteDialog
import com.demo.android.librarian.ui.composeUi.LibrarianTheme
import com.demo.android.librarian.ui.composeUi.RatingBar
import com.demo.android.librarian.ui.composeUi.TopBar
import com.demo.android.librarian.utils.EMPTY_BOOK_REVIEW
import com.demo.android.librarian.utils.EMPTY_GENRE
import com.demo.android.librarian.utils.formatDateToText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BookReviewDetailsActivity : AppCompatActivity() {

  //  @Inject
//  lateinit var repository: LibrarianRepository
//  private val _bookReviewDetailsState = mutableStateOf(EMPTY_BOOK_REVIEW)
//  private val _genreState = mutableStateOf(EMPTY_GENRE)
//  private val _isShowingAddEntryState = mutableStateOf(false)

  private val _screenState = mutableStateOf<BookReviewDetailsScreenState>(Initial)

  private val viewModel by viewModels<BookReviewDetailsViewModel>()

  companion object {
    private const val KEY_BOOK_REVIEW = "book_review"

    fun getIntent(context: Context, review: BookReview): Intent {
      val intent = Intent(context, BookReviewDetailsActivity::class.java)

      intent.putExtra(KEY_BOOK_REVIEW, review)
      return intent
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val data = if (Build.VERSION.SDK_INT >= 33) {
      intent?.getParcelableExtra(KEY_BOOK_REVIEW, BookReview::class.java)
    } else {
      intent?.getParcelableExtra(KEY_BOOK_REVIEW)
    }

    if (data == null) {
      finish()
      return
    }

    viewModel.setReview(data)
    setContent { LibrarianTheme { BookReviewDetailsContent() } }
  }

  @Composable
  fun BookReviewDetailsContent() {
    val animationState by _screenState
    val state = animateBookReviewDetails(screenState = animationState)

    LaunchedEffect(Unit, block = { _screenState.value = Loaded })

    Scaffold(topBar = { BookReviewDetailsTopBar() },
      floatingActionButton = { AddReadingEntry(state) }) {
      BookReviewDetailsInformation(state)
    }
  }

  @Composable
  fun BookReviewDetailsTopBar() {
    val reviewState = viewModel.bookReviewDetailsState.value
//    val bookName = reviewState.book.name
    val bookName =
      reviewState?.book?.name ?: stringResource(id = R.string.book_review_details_title)
    TopBar(title = bookName, onBackPressed = { onBackPressedDispatcher.onBackPressed() })
  }

  @Composable
  fun AddReadingEntry(state: BookReviewDetailsTransitionState) {
    FloatingActionButton(
      modifier = Modifier.size(state.floatingButtonSize),
      onClick = { }) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reading Entry")
    }
  }

  @Composable
  fun BookReviewDetailsInformation(state: BookReviewDetailsTransitionState) {
//    val bookReview = viewModel.bookReviewDetailsState.value
//    val genre = viewModel.genreState.value

    val bookReview by viewModel.bookReviewDetailsState.observeAsState(EMPTY_BOOK_REVIEW)
    val genre by viewModel.genreState.observeAsState(EMPTY_GENRE)
    val deleteEntryState by viewModel.deleteEntryState.observeAsState()
    val isShowingAddEntry by viewModel.isShowingAddEntryState.observeAsState(false)

    val entryToDelete = deleteEntryState

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(state.imageMarginTop))

      Card(
        modifier = Modifier.size(200.dp, 300.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 16.dp
      ) {

        AsyncImage(
          model = bookReview.review.imageUrl,
          contentScale = ContentScale.FillWidth,
          contentDescription = null
        )
      }

      Spacer(modifier = Modifier.height(state.titleMarginTop))

      Text(
        text = bookReview.book.name,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colors.onPrimary
      )

      Spacer(modifier = Modifier.height(state.contentMarginTop))

      Text(
        text = genre.name,
        fontSize = 12.sp,
        color = MaterialTheme.colors.onPrimary,
        modifier = Modifier.alpha(state.contentAlpha),
      )

      Spacer(modifier = Modifier.height(state.contentMarginTop))

      RatingBar(
        modifier = Modifier.align(CenterHorizontally),
        range = 1..5,
        isSelectable = false,
        isLargeRating = false,
        currentRating = bookReview.review.rating,
        onRatingChanged = {}
      )

      Spacer(modifier = Modifier.height(state.contentMarginTop))

      Text(
        text =
        stringResource(
          id =
          R.string.last_updated_date, formatDateToText(bookReview.review.lastUpdatedDate)
        ),
        fontSize = 12.sp,
        color = MaterialTheme.colors.onPrimary,
        modifier = Modifier.alpha(state.contentAlpha),
      )

      Spacer(modifier = Modifier.height(8.dp))

      Spacer(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .height(1.dp)
          .background(brush = SolidColor(Color.LightGray), shape = RectangleShape)
      )

      Text(
        modifier = Modifier
          .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
          .alpha(state.contentAlpha),
        text = bookReview.review.notes,
        fontSize = 12.sp,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colors.onPrimary
      )

      Spacer(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .height(1.dp)
          .background(brush = SolidColor(Color.LightGray), shape = RectangleShape)
      )
    }

    if (isShowingAddEntry) {
      AddReadingEntryDialog(
        onDismiss = { viewModel.onDialogDismiss() },
        onReadingEntryFinished = { viewModel.addNewEntry(it) }
      )
    }

    if (entryToDelete != null) {
      DeleteDialog(
        item = entryToDelete,
        message = stringResource(id = R.string.delete_entry_message),
        onDeleteItem = { viewModel.removeReadingEntry(it) },
        onDismiss = { viewModel.onDialogDismiss() }
      )
    }
  }

//  private fun setReview(bookReview: BookReview) {
//    _bookReviewDetailsState.value = bookReview
//
//    lifecycleScope.launch {
//      _genreState.value = repository.getGenreById(bookReview.book.genreId)
//    }
//  }

//  private fun addNewEntry(entry: String) {
//    val data = _bookReviewDetailsState.value.review
//
//    val updatedReview = data.copy(
//      entries = data.entries + ReadingEntry(comment = entry),
//      lastUpdatedDate = Date()
//    )
//
//    updateReview(updatedReview)
//  }

//  private fun removeReadingEntry(readingEntry: ReadingEntry) {
//    val data = _bookReviewDetailsState.value.review
//
//    val updatedReview = data.copy(
//      entries = data.entries - readingEntry,
//      lastUpdatedDate = Date()
//    )
//
//    updateReview(updatedReview)
//  }

//  private fun updateReview(updatedReview: Review) {
//    lifecycleScope.launch {
//      repository.updateReview(updatedReview)
//
//      setReview(repository.getReviewById(updatedReview.id))
//    }
//  }
}