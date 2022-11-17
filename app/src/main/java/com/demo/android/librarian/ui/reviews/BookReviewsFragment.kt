package com.demo.android.librarian.ui.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.demo.android.librarian.R
import com.demo.android.librarian.model.relations.BookReview
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.bookReviewDetails.BookReviewDetailsActivity
import com.demo.android.librarian.ui.composeUi.DeleteDialog
import com.demo.android.librarian.ui.composeUi.TopBar
import com.demo.android.librarian.ui.reviews.ui.BookReviewsList
import com.demo.android.librarian.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fetches and displays notes from the API.
 */


private const val REQUEST_CODE_ADD_REVIEW = 202

@AndroidEntryPoint
class BookReviewsFragment : Fragment() {

  @Inject
  lateinit var repository: LibrarianRepository

  private val bookReviewsState = mutableStateOf(emptyList<BookReview>())
  private val _deleteReviewState = mutableStateOf<BookReview?>(null)

  private val addReviewContract by lazy {
    registerForActivityResult(AddBookReviewContract()) { isReviewAdded ->
      if (isReviewAdded) {
        activity?.toast("Review added!")
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    addReviewContract

    return ComposeView(requireContext()).apply {
      setContent {
        BookReviewsContent()
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lifecycleScope.launch {
      bookReviewsState.value = repository.getReviews()
    }
  }

  @Composable
  fun BookReviewsContent() {
    Scaffold(
      topBar = { BookReviewsTopBar() },
      floatingActionButton = { AddBookReview() }
    ) {
      BookReviewsContentWrapper()
    }
  }

  @Composable
  fun BookReviewsTopBar() {
    TopBar(title = stringResource(id = R.string.book_reviews_title))
  }

  @Composable
  fun AddBookReview() {
    FloatingActionButton(onClick = { startAddBookReview() }) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Book Review")
    }
  }

  @Composable
  fun BookReviewsContentWrapper() {
    val bookReviews = bookReviewsState.value

    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {

      val reviewToDelete = _deleteReviewState.value

      BookReviewsList(
        bookReviews,
        onItemClick = ::onItemSelected,
        onItemLongClick = { _deleteReviewState.value = it }
      )

      if (reviewToDelete != null) {
        DeleteDialog(
          item = reviewToDelete,
          message = stringResource(id = R.string.delete_review_message, reviewToDelete.book.name),
          onDeleteItem = { bookReview ->
            deleteReview(bookReview)
            _deleteReviewState.value = null
          },
          onDismiss = {
            _deleteReviewState.value = null
          }
        )
      }
    }
  }

  private fun deleteReview(bookReview: BookReview) {
    lifecycleScope.launch {
      repository.removeReview(bookReview.review)
      bookReviewsState.value = repository.getReviews()
    }
  }

  private fun startAddBookReview() {
    addReviewContract.launch(REQUEST_CODE_ADD_REVIEW)
  }

  private fun onItemSelected(item: BookReview) {
    startActivity(BookReviewDetailsActivity.getIntent(requireContext(), item))
  }
}