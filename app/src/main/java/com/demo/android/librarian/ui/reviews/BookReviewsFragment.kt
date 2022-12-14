package com.demo.android.librarian.ui.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.demo.android.librarian.R
import com.demo.android.librarian.model.relations.BookReview
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.bookReviewDetails.BookReviewDetailsActivity
import com.demo.android.librarian.ui.bookReviewDetails.BookReviewDetailsViewModel
import com.demo.android.librarian.ui.composeUi.DeleteDialog
import com.demo.android.librarian.ui.composeUi.LibrarianTheme
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

//  @Inject
//  lateinit var repository: LibrarianRepository

//  private val bookReviewsState = mutableStateOf(emptyList<BookReview>())

  private val viewModel by viewModels<BookReviewsViewModel>()

//  private val _deleteReviewState = mutableStateOf<BookReview?>(null)

  /*private val addReviewContract by lazy {
    registerForActivityResult(AddBookReviewContract()) { isReviewAdded ->
      if (isReviewAdded) {
        activity?.toast("Review added!")
      }
    }
  }*/

  private var _addReviewContract: ManagedActivityResultLauncher<Int, Boolean>? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    /*addReviewContract*/

    return ComposeView(requireContext()).apply {
      setContent { LibrarianTheme { BookReviewsContent() } }
    }
  }

  /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lifecycleScope.launch {
      bookReviewsState.value = repository.getReviews()
    }
  }*/

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
    _addReviewContract =
      rememberLauncherForActivityResult(AddBookReviewContract()) { isReviewAdded ->
        if (isReviewAdded) {
          activity?.toast("Review added!")
        }
      }

//    val bookReviews = viewModel.bookReviewsState.value

    val bookReviews by viewModel.bookReviewsState.observeAsState(emptyList())
    val deleteReviewState by viewModel.deleteReviewState.observeAsState()

    val reviewToDelete = deleteReviewState

    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {

//      val reviewToDelete = _deleteReviewState.value

      BookReviewsList(
        bookReviews,
        onItemClick = ::onItemSelected,
        onItemLongClick = {
//          _deleteReviewState.value = it
          viewModel.onItemLongTapped(it)
        }
      )

      if (reviewToDelete != null) {
        DeleteDialog(
          item = reviewToDelete,
          message = stringResource(id = R.string.delete_review_message, reviewToDelete.book.name),
          onDeleteItem = { bookReview ->
//            viewModel.removeReadingEntry(bookReview)
            viewModel.deleteReview(bookReview)
          },
          onDismiss = {
//            _deleteReviewState.value = null
            viewModel.onDialogDismissed()
          }
        )
      }
    }
  }

//  private fun deleteReview(bookReview: BookReview) {
//    lifecycleScope.launch {
//      repository.removeReview(bookReview.review)
//      bookReviewsState.value = repository.getReviews()
//    }
//  }

  private fun startAddBookReview() {
    _addReviewContract?.launch(REQUEST_CODE_ADD_REVIEW)
  }

  private fun onItemSelected(item: BookReview) {
    startActivity(BookReviewDetailsActivity.getIntent(requireContext(), item))
  }
}