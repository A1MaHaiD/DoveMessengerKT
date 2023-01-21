package com.handroid.dovemessengerkt.di

import com.google.firebase.auth.FirebaseAuth
import com.handroid.dovemessengerkt.data.repository.NoteRepository
import com.handroid.dovemessengerkt.data.repository.NoteRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.handroid.dovemessengerkt.data.repository.AuthRepository
import com.handroid.dovemessengerkt.data.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNoteRepository(
        database: FirebaseFirestore
    ): NoteRepository {
        return NoteRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth
    ): AuthRepository {
        return AuthRepositoryImpl(auth, database)
    }
}