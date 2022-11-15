package com.demo.android.librarian.ui.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.demo.android.librarian.model.relations.BookReview
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.bookReviewDetails.BookReviewDetailsActivity
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

  val bookReviewsState: LiveData<List<BookReview>> by lazy {
    repository.getReviewsFlow().asLiveData(
      lifecycleScope.coroutineContext
    )
  }

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

    }
  }

  fun deleteReview(bookReview: BookReview) {
    lifecycleScope.launch {
      repository.removeReview(bookReview.review)
    }
  }

  private fun startAddBookReview() {
    addReviewContract.launch(REQUEST_CODE_ADD_REVIEW)
  }

  private fun onItemSelected(item: BookReview) {
    startActivity(BookReviewDetailsActivity.getIntent(requireContext(), item))
  }
}