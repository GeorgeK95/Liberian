package com.demo.android.librarian.ui.bookReviewDetails

import androidx.lifecycle.*
import com.demo.android.librarian.model.Genre
import com.demo.android.librarian.model.ReadingEntry
import com.demo.android.librarian.model.Review
import com.demo.android.librarian.model.relations.BookReview
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.bookReviewDetails.anim.BookReviewDetailsScreenState
import com.demo.android.librarian.ui.bookReviewDetails.anim.Initial
import com.demo.android.librarian.ui.bookReviewDetails.anim.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookReviewDetailsViewModel @Inject constructor(
  private val repository: LibrarianRepository
) : ViewModel() {

  private val _bookReviewDetailsState = MutableLiveData<BookReview>()
  val bookReviewDetailsState: LiveData<BookReview> = _bookReviewDetailsState

  private val _genreState = MutableLiveData<Genre>()
  val genreState: LiveData<Genre> = _genreState

  private val _deleteEntryState = MutableLiveData<ReadingEntry?>()
  val deleteEntryState: LiveData<ReadingEntry?> = _deleteEntryState

  private val _isShowingAddEntryState = MutableLiveData(false)
  val isShowingAddEntryState: LiveData<Boolean> = _isShowingAddEntryState

  private val _screenAnimationState = MutableLiveData<BookReviewDetailsScreenState>(Initial)
  val screenAnimationState: LiveData<BookReviewDetailsScreenState> = _screenAnimationState

  fun setReview(bookReview: BookReview) {
    _bookReviewDetailsState.value = bookReview

    viewModelScope.launch {
      _genreState.value = repository.getGenreById(bookReview.book.genreId)
    }
  }

  fun onFirstLoad() {
    _screenAnimationState.value = Loaded
  }

  fun addNewEntry(entry: String) {
    val data = _bookReviewDetailsState.value?.review ?: return

    val updatedReview = data.copy(
      entries = data.entries + ReadingEntry(comment = entry),
      lastUpdatedDate = Date()
    )

    updateReview(updatedReview)
    onDialogDismiss()
  }

  private fun updateReview(review: Review) {
    viewModelScope.launch {
      repository.updateReview(review)

      setReview(repository.getReviewById(review.id))
    }
  }

  fun onDialogDismiss() {
    _deleteEntryState.value = null
    _isShowingAddEntryState.value = false
  }

  fun removeReadingEntry(readingEntry: ReadingEntry) {
    val data = _bookReviewDetailsState.value?.review ?: return

    val updatedReview = data.copy(
      entries = data.entries - readingEntry,
      lastUpdatedDate = Date()
    )

    updateReview(updatedReview)
    onDialogDismiss()
  }

  fun onItemLongTapped(readingEntry: ReadingEntry) {
    _deleteEntryState.value = readingEntry
  }

  fun onAddEntryTapped() {
    _isShowingAddEntryState.value = true
  }
}