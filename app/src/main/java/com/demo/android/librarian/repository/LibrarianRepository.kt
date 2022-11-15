package com.demo.android.librarian.repository

import com.demo.android.librarian.model.Book
import com.demo.android.librarian.model.Genre
import com.demo.android.librarian.model.ReadingList
import com.demo.android.librarian.model.Review
import com.demo.android.librarian.model.relations.BookAndGenre
import com.demo.android.librarian.model.relations.BookReview
import com.demo.android.librarian.model.relations.ReadingListsWithBooks
import kotlinx.coroutines.flow.Flow

interface LibrarianRepository {

  suspend fun addBook(book: Book)

  suspend fun removeBook(book: Book)

  suspend fun getBooks(): List<BookAndGenre>

  fun getBooksFlow(): Flow<List<BookAndGenre>>

  suspend fun getBookById(bookId: String): BookAndGenre

  suspend fun getBooksByGenre(genreId: String): List<BookAndGenre>

  suspend fun getGenres(): List<Genre>

  suspend fun getGenreById(genreId: String): Genre

  suspend fun addGenres(genres: List<Genre>)

  suspend fun getReviews(): List<BookReview>

  fun getReviewsFlow(): Flow<List<BookReview>>

  suspend fun getBooksByRating(rating: Int): List<BookAndGenre>

  suspend fun getReviewById(reviewId: String): BookReview

  suspend fun addReview(review: Review)

  suspend fun updateReview(review: Review)

  suspend fun removeReview(review: Review)

  suspend fun removeReviews(reviews: List<Review>)

  suspend fun getReviewsForBook(bookId: String): List<Review>

  suspend fun getReadingLists(): List<ReadingListsWithBooks>

  fun getReadingListsFlow(): Flow<List<ReadingListsWithBooks>>

  suspend fun removeReadingList(readingList: ReadingList)

  suspend fun updateReadingList(readingList: ReadingList)

  suspend fun removeReadingLists(readingLists: List<ReadingList>)

  suspend fun addReadingList(readingList: ReadingList)

  fun getReadingListById(id: String): Flow<ReadingListsWithBooks>
}