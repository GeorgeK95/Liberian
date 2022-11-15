package com.demo.android.librarian.ui.readingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.demo.android.librarian.R
import com.demo.android.librarian.model.ReadingList
import com.demo.android.librarian.model.relations.ReadingListsWithBooks
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.composeUi.TopBar
import com.demo.android.librarian.ui.readingListDetails.ReadingListDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReadingListFragment : Fragment() {

  @Inject
  lateinit var repository: LibrarianRepository

  val readingListsState: LiveData<List<ReadingListsWithBooks>> by lazy {
    repository.getReadingListsFlow().asLiveData()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return ComposeView(requireContext()).apply {
      setContent {
        ReadingListContent()
      }
    }
  }

  @Composable
  fun ReadingListContent() {
    Scaffold(
      topBar = { ReadingListTopBar() },
      floatingActionButton = { AddReadingListButton() }) {
      ReadingListContentWrapper()
    }
  }

  @Composable
  fun ReadingListContentWrapper() {
    // TODO fill this in!
  }

  @Composable
  fun AddReadingListButton() {
    FloatingActionButton(onClick = {
      // TODO show dialog to add reading list
    }) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reading List")
    }
  }

  @Composable
  fun ReadingListTopBar() {
    TopBar(title = stringResource(id = R.string.reading_lists_title))
  }

  fun deleteReadingList(readingListsWithBooks: ReadingListsWithBooks) {
    lifecycleScope.launch {
      repository.removeReadingList(
        ReadingList(
          readingListsWithBooks.id,
          readingListsWithBooks.name,
          readingListsWithBooks.books.map { it.book.id }
        )
      )
    }
  }

  fun addReadingList(readingListName: String) {
    lifecycleScope.launch {
      repository.addReadingList(ReadingList(name = readingListName, bookIds = emptyList()))
    }
  }

  private fun onItemSelected(readingList: ReadingListsWithBooks) {
    startActivity(ReadingListDetailsActivity.getIntent(requireContext(), readingList))
  }
}