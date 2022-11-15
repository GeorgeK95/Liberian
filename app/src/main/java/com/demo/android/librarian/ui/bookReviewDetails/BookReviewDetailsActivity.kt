package com.demo.android.librarian.ui.bookReviewDetails

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.demo.android.librarian.R
import com.demo.android.librarian.model.Genre
import com.demo.android.librarian.model.ReadingEntry
import com.demo.android.librarian.model.Review
import com.demo.android.librarian.model.relations.BookReview
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.composeUi.TopBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BookReviewDetailsActivity : AppCompatActivity() {

  @Inject
  lateinit var repository: LibrarianRepository
  private val _bookReviewDetailsState = MutableLiveData<BookReview>()
  private val _genreState = MutableLiveData<Genre>()

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

    setReview(data)
    setContent { BookReviewDetailsContent() }
  }

  @Composable
  fun BookReviewDetailsContent() {
    Scaffold(topBar = { BookReviewDetailsTopBar() },
      floatingActionButton = { AddReadingEntry() }) {
      BookReviewDetailsInformation()
    }
  }

  @Composable
  fun BookReviewDetailsTopBar() {
    val reviewState = _bookReviewDetailsState.value
    val bookName =
      reviewState?.book?.name ?: stringResource(id = R.string.book_review_details_title)

    TopBar(title = bookName, onBackPressed = { onBackPressedDispatcher.onBackPressed() })
  }

  @Composable
  fun AddReadingEntry() {
    FloatingActionButton(onClick = { }) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reading Entry")
    }
  }

  @Composable
  fun BookReviewDetailsInformation() {

  }

  fun setReview(bookReview: BookReview) {
    _bookReviewDetailsState.value = bookReview

    lifecycleScope.launch {
      _genreState.value = repository.getGenreById(bookReview.book.genreId)
    }
  }

  fun addNewEntry(entry: String) {
    val data = _bookReviewDetailsState.value?.review ?: return

    val updatedReview = data.copy(
      entries = data.entries + ReadingEntry(comment = entry),
      lastUpdatedDate = Date()
    )

    updateReview(updatedReview)
  }

  fun removeReadingEntry(readingEntry: ReadingEntry) {
    val data = _bookReviewDetailsState.value?.review ?: return

    val updatedReview = data.copy(
      entries = data.entries - readingEntry,
      lastUpdatedDate = Date()
    )

    updateReview(updatedReview)
  }

  private fun updateReview(updatedReview: Review) {
    lifecycleScope.launch {
      repository.updateReview(updatedReview)

      setReview(repository.getReviewById(updatedReview.id))
    }
  }
}