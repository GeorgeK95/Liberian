package com.demo.android.librarian.ui.addBook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.demo.android.librarian.R
import com.demo.android.librarian.model.Book
import com.demo.android.librarian.model.Genre
import com.demo.android.librarian.model.state.AddBookState
import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.ui.composeUi.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddBookActivity : AppCompatActivity(), AddBookView {

  private val _addBookState = mutableStateOf(AddBookState())
  private val _genresState = MutableLiveData(emptyList<Genre>())

  @Inject
  lateinit var repository: LibrarianRepository

  companion object {
    fun getIntent(context: Context): Intent = Intent(context, AddBookActivity::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { LibrarianTheme { AddBookContent() } }
    loadGenres()
  }

  override fun onBookAdded() {
    setResult(RESULT_OK)
    finish()
  }

  @Composable
  fun AddBookContent() {
    Scaffold(topBar = { AddBookTopBar() }) {
      AddBookFormContent()
    }
  }

  @Composable
  fun AddBookTopBar() {
    TopBar(
      title = stringResource(id = R.string.add_book_title),
      onBackPressed = { onBackPressed() }
    )

    /*TopAppBar(
      title = { Text(text = stringResource(id = R.string.add_book_title)) },
      navigationIcon = {
        IconButton(
          onClick = { onBackPressed() }) {
          Icon(Icons.Default.ArrowBack, "")
        }
      },
      contentColor = Color.White,
      backgroundColor = colorResource(id = R.color.colorPrimary)
    )*/
  }

  @Composable
  fun AddBookFormContent() {
    val genres = _genresState.value ?: emptyList()
//    val isGenresPickerOpen = remember { mutableStateOf(false) }
    val bookNameState = remember { mutableStateOf("") }
    val bookDescState = remember { mutableStateOf("") }
    val selectedGenreName =
      genres.firstOrNull { it.id == _addBookState.value.genreId }?.name ?: "None"

    val isButtonEnabled = derivedStateOf { (_addBookState.value.getError() == null) }

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      InputField(
        value = bookNameState.value,
        onStateChanged = {
          bookNameState.value = it
          _addBookState.value = _addBookState.value.copy(name = it)
        },
        label = stringResource(id = R.string.book_title_hint),
        isInputValid = bookNameState.value.isNotEmpty()
      )

      InputField(
        value = bookDescState.value,
        onStateChanged = {
          bookDescState.value = it
          _addBookState.value = _addBookState.value.copy(description = it)
        },
        label = stringResource(id = R.string.book_description_hint),
        isInputValid = bookDescState.value.isNotEmpty()
      )

      /*OutlinedTextField(
        value = bookNameState.value,
        onValueChange = {
          bookNameState.value = it
          _addBookState.value = _addBookState.value?.copy(description = it)
        },
        label = { Text(text = stringResource(id = R.string.book_title_hint)) }
      )

      OutlinedTextField(
        value = bookDescState.value,
        onValueChange = {
          bookDescState.value = it
          _addBookState.value = _addBookState.value?.copy(description = it)
        },
        label = { Text(text = stringResource(id = R.string.book_description_hint)) }
      )*/

      SpinnerPicker(
        pickerText = selectedGenreName,
        items = genres,
        itemToName = { currentGenre -> currentGenre.name },
        onItemPicked = {
          _addBookState.value = _addBookState.value.copy(genreId = it.id)
        }
      )

      /*Row {
        Text(
          text = "Selected genre: ",
          fontSize = 16.sp
        )
        Text(
          text = selectedGenreName,
          modifier = Modifier.clickable(onClick = {
            isGenresPickerOpen.value = !isGenresPickerOpen.value
          }),
          fontSize = 16.sp
        )

        DropdownMenu(
          expanded = isGenresPickerOpen.value,
          onDismissRequest = { isGenresPickerOpen.value = false },
        ) {
          genres.forEach { genre ->
            DropdownMenuItem(onClick = {
              isGenresPickerOpen.value = false
              _addBookState.value = _addBookState.value?.copy(genreId = genre.id)
            }) {
              Text(text = genre.name)
            }
          }
        }
      }*/

      ActionButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.add_book_button_text),
        isEnabled = isButtonEnabled.value,
        onClick = { onAddBookTapped() }
      )
      /*TextButton(onClick = { onAddBookTapped() }) {
        Text(text = stringResource(id = R.string.add_book_button_text))
      }*/
    }
  }

  private fun onAddBookTapped() {
    val bookState = _addBookState.value ?: return

    if (bookState.name.isNotEmpty() &&
      bookState.description.isNotEmpty() &&
      bookState.genreId.isNotEmpty()
    ) {
      lifecycleScope.launch {
        repository.addBook(
          Book(
            name = bookState.name,
            description = bookState.description,
            genreId = bookState.genreId
          )
        )

        onBookAdded()
      }
    } else {
      Toast.makeText(this, bookState.getError(), Toast.LENGTH_SHORT).show()
    }
  }

  private fun loadGenres() {
    lifecycleScope.launch {
      _genresState.value = repository.getGenres()
    }
  }

}