package com.demo.android.librarian.ui.readingList.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.android.librarian.R
import com.demo.android.librarian.model.relations.ReadingListsWithBooks

@Composable
fun ReadingLists(
  readingLists: List<ReadingListsWithBooks>,
  onItemClick: (ReadingListsWithBooks) -> Unit
) {
  LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(readingLists) { readingList ->
      ReadingListItem(readingList, onItemClick)
    }
  }
}

@Composable
fun ReadingListItem(
  readingList: ReadingListsWithBooks,
  onItemClick: (ReadingListsWithBooks) -> Unit
) {
  Card(
    modifier = Modifier
      .height(75.dp)
      .fillMaxWidth()
      .padding(8.dp)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        onClick = { onItemClick(readingList) },
        indication = null
      ),
    elevation = 8.dp,
    border = BorderStroke(1.dp, color = MaterialTheme.colors.primary),
    shape = RoundedCornerShape(16.dp),
  ) {
    Column {
      Spacer(modifier = Modifier.height(16.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(16.dp))

        Text(
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colors.primary,
          text = readingList.name
        )

        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = stringResource(
            id = R.string.reading_list_number_of_books,
            readingList.books.size
          ),
          color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.width(16.dp))
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}