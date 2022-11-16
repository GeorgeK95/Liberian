package com.demo.android.librarian.ui.books.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.android.librarian.model.relations.BookAndGenre

@Composable
fun BooksList(
  books: List<BookAndGenre>
) {
  LazyColumn(
    modifier = Modifier.padding(top = 16.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    items(books) { bookAndGenre ->
      BookListItem(bookAndGenre)
    }
  }
}

@Composable
fun BookListItem(bookAndGenre: BookAndGenre) {
  Card(
    modifier = Modifier
      .wrapContentHeight()
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    elevation = 8.dp,
    border = BorderStroke(1.dp, MaterialTheme.colors.primary),
    shape = RoundedCornerShape(16.dp)
  ) {

    Row(modifier = Modifier.fillMaxSize()) {
      Spacer(modifier = Modifier.width(16.dp))

      Column {
        Text(
          modifier = Modifier.padding(top = 16.dp),
          text = bookAndGenre.book.name,
          color = MaterialTheme.colors.primary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = bookAndGenre.genre.name,
          fontSize = 16.sp,
          fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = bookAndGenre.book.description,
          fontSize = 12.sp,
          overflow = TextOverflow.Ellipsis,
          fontStyle = FontStyle.Italic,
          modifier = Modifier
            .fillMaxHeight()
            .padding(end = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}