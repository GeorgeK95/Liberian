package com.demo.android.librarian.di

import com.demo.android.librarian.repository.LibrarianRepository
import com.demo.android.librarian.repository.LibrarianRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindRepository(repositoryImpl: LibrarianRepositoryImpl): LibrarianRepository
}