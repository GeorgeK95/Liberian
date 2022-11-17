package com.demo.android.librarian.ui.composeUi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.demo.android.librarian.R
import com.demo.android.librarian.model.relations.BookAndGenre
import com.demo.android.librarian.model.relations.BookReview

@Composable
fun <T> DeleteDialog(
  item: T,
  message: String,
  onDeleteItem: (T) -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    title = { Text(text = stringResource(id = R.string.delete_title)) },
    text = { Text(text = message) },
    onDismissRequest = onDismiss,
    buttons = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        DialogButton(
          text = R.string.yes,
          onClickAction = { onDeleteItem(item) }
        )

        DialogButton(
          text = R.string.cancel,
          onClickAction = onDismiss
        )
      }
    }
  )
}

/*
@Composable
fun DeleteReviewDialog(
  item: BookReview,
  message: String,
  onDeleteItem: (BookReview) -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    title = { Text(text = stringResource(id = R.string.delete_title)) },
    text = { Text(text = message) },
    onDismissRequest = onDismiss,
    buttons = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        DialogButton(
          text = R.string.yes,
          onClickAction = { onDeleteItem(item) }
        )

        DialogButton(
          text = R.string.cancel,
          onClickAction = onDismiss
        )
      }
    }
  )
}

@Composable
fun DeleteBookDialog(
  item: BookAndGenre,
  message: String,
  onDeleteItem: (BookAndGenre) -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    title = { Text(text = stringResource(id = R.string.delete_title)) },
    text = { Text(text = message) },
    onDismissRequest = onDismiss,
    buttons = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        DialogButton(
          text = R.string.yes,
          onClickAction = { onDeleteItem(item) }
        )

        DialogButton(text = R.string.cancel, onClickAction = onDismiss)
      }
    })
}*/
