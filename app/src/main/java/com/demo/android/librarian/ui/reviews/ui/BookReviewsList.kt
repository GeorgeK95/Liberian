package com.demo.android.librarian.ui.reviews.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.demo.android.librarian.R
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.demo.android.librarian.model.relations.BookReview
import com.demo.android.librarian.ui.composeUi.RatingBar

@Composable
fun BookReviewsList(
  bookReviews: List<BookReview>,
  onItemClick: (BookReview) -> Unit,
  onItemLongClick: (BookReview) -> Unit
) {
  LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(bookReviews) { bookReview ->
      BookReviewItem(bookReview, onItemClick, onItemLongClick)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookReviewItem(
  bookReview: BookReview,
  onItemClick: (BookReview) -> Unit,
  onItemLongClick: (BookReview) -> Unit
) {
  Card(
    elevation = 8.dp,
    border = BorderStroke(1.dp, MaterialTheme.colors.primary),
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .wrapContentHeight()
      .padding(16.dp)
      .combinedClickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = { onItemClick(bookReview) },
        onLongClick = { onItemLongClick(bookReview) }
      )
  ) {
    Row(modifier = Modifier.fillMaxSize()) {

      Spacer(modifier = Modifier.width(16.dp))

      Column(
        modifier = Modifier
          .weight(0.6f)
          .fillMaxHeight()
      ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = bookReview.book.name,
          color = MaterialTheme.colors.primary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
          Text(
            text = stringResource(id = R.string.rating_text),
            color = MaterialTheme.colors.onPrimary
          )

          RatingBar(
            modifier = Modifier.align(CenterVertically),
            range = 1..5,
            currentRating = bookReview.review.rating,
            isSelectable = false,
            isLargeRating = false,
            onRatingChanged = {})
        }

        Text(
          text = stringResource(
            id = R.string.number_of_reading_entries,
            bookReview.review.entries.size
          ),
          color = MaterialTheme.colors.onPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = bookReview.review.notes,
          fontSize = 12.sp,
          modifier = Modifier.fillMaxSize(),
          overflow = TextOverflow.Ellipsis,
          fontStyle = FontStyle.Italic,
          maxLines = 4,
          color = MaterialTheme.colors.onPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))
      }

      Spacer(modifier = Modifier.width(16.dp))

      Card(
        modifier = Modifier.weight(0.4f),
        shape = RoundedCornerShape(
          topEnd = 16.dp,
          topStart = 16.dp,
          bottomEnd = 16.dp,
          bottomStart = 0.dp
        ),
        elevation = 16.dp
      ) {

        /**
         * Update Note: CoilImage composable is old not compatible with current Jetpack Compose version.
         * Use AsyncImage instead of CoilImage composable with updated parameters.
         * */
        AsyncImage(
          model = bookReview.review.imageUrl,
          contentScale = ContentScale.FillWidth,
          contentDescription = null
        )
      }
    }
  }
}