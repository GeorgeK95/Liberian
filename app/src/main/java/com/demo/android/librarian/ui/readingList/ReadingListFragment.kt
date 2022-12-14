package com.demo.android.librarian.ui.readingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.demo.android.librarian.R
import com.demo.android.librarian.model.relations.ReadingListsWithBooks
import com.demo.android.librarian.ui.composeUi.DeleteDialog
import com.demo.android.librarian.ui.composeUi.LibrarianTheme
import com.demo.android.librarian.ui.composeUi.TopBar
import com.demo.android.librarian.ui.readingList.ui.AddReadingList
import com.demo.android.librarian.ui.readingList.ui.ReadingLists
import com.demo.android.librarian.ui.readingListDetails.ReadingListDetailsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadingListFragment : Fragment() {

//  @Inject
//  lateinit var repository: LibrarianRepository

  /*val readingListsState: LiveData<List<ReadingListsWithBooks>> by lazy {
    repository.getReadingListsFlow().asLiveData()
  }*/
//  private val _readingListsState = mutableStateOf<List<ReadingListsWithBooks>>(emptyList())
//  private val _isShowingAddReadingListState = mutableStateOf(false)

  private val viewModel by viewModels<ReadingListViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return ComposeView(requireContext()).apply {
      setContent { LibrarianTheme { ReadingListContent() } }
    }
  }

  /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loadReadingList()
  }*/

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
//    val readingLists = _readingListsState.value
    val readingLists by viewModel.readingListsState.observeAsState(emptyList())
    val readingListToDelete by viewModel.deleteReadingListState.observeAsState()

    val deleteList = readingListToDelete

    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {

      ReadingLists(readingLists = readingLists, onItemClick = { onItemSelected(it) })

//      val isShowingAddList = _isShowingAddReadingListState.value
      val isShowingAddList by viewModel.isShowingAddReadingListState.observeAsState(false)

      if (isShowingAddList) {
        AddReadingList(
          onDismiss = {
//            _isShowingAddReadingListState.value = false
            viewModel.onDialogDismiss()
          },
          onAddList = { name ->
//            addReadingList(name)
//            _isShowingAddReadingListState.value = false
            viewModel.addReadingList(name)
            viewModel.onDialogDismiss()
          }
        )

        if (deleteList != null) {
          DeleteDialog(
            item = deleteList,
            message = stringResource(id = R.string.delete_message, deleteList.name),
            onDeleteItem = { readingList ->
//              readingListViewModel.deleteReadingList(readingList)
//              readingListViewModel.onDialogDismiss()
              viewModel.deleteReadingList(readingList)
              viewModel.onDialogDismiss()
            },
            onDismiss = {
//              readingListViewModel.onDialogDismiss()
              viewModel.onDialogDismiss()
            }
          )
        }
      }
    }
  }

  @Composable
  fun AddReadingListButton() {
//    val isShowingAddReadingList = _isShowingAddReadingListState.value
    val isShowingAddReadingList = viewModel.isShowingAddReadingListState.value ?: false
    val size by animateDpAsState(targetValue = if (isShowingAddReadingList) 1.dp else 56.dp)

    FloatingActionButton(
      modifier = Modifier.size(size),
      onClick = {
//        _isShowingAddReadingListState.value = true
        viewModel.onAddReadingListTapped()
      }) {
      Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reading List")
    }
  }

  @Composable
  fun ReadingListTopBar() {
    TopBar(title = stringResource(id = R.string.reading_lists_title))
  }

  /*private fun loadReadingList() {
    lifecycleScope.launch {
      _readingListsState.value = repository.getReadingLists()
    }
  }

  private fun deleteReadingList(readingListsWithBooks: ReadingListsWithBooks) {
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

  private fun addReadingList(readingListName: String) {
    lifecycleScope.launch {
      repository.addReadingList(ReadingList(name = readingListName, bookIds = emptyList()))
      _readingListsState.value = repository.getReadingLists()
    }
  }*/

  private fun onItemSelected(readingList: ReadingListsWithBooks) {
    startActivity(ReadingListDetailsActivity.getIntent(requireContext(), readingList))
  }
}