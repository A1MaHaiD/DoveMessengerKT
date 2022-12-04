package com.handroid.dovemessengerkt.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.util.UiState
import java.util.*

class NoteRepositoryImpl(
    val database: FirebaseFirestore
) : NoteRepository {

    override fun getNotes(): UiState<List<Note>> {
        //We will get data from firebase
        val data = listOf<Note>()

        if (data.isNullOrEmpty()) {
            return UiState.Failure("Data is Empty")
        } else {
            return UiState.Success(data)
        }
    }
}