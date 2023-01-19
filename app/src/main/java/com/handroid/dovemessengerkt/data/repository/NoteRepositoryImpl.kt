package com.handroid.dovemessengerkt.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.handroid.dovemessengerkt.data.model.Note
import com.handroid.dovemessengerkt.util.FireStoreDocumentField
import com.handroid.dovemessengerkt.util.FireStoreTables
import com.handroid.dovemessengerkt.util.UiState

class NoteRepositoryImpl(
    val database: FirebaseFirestore
) : NoteRepository {

    override fun getNotes(result: (UiState<List<Note>>) -> Unit) {
        database.collection(FireStoreTables.NOTE)
            .orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Note>()
                for (document in it) {
                    val note = document.toObject(Note::class.java)
                    notes.add(note)
                }
                result.invoke(
                    UiState.Success(notes)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun addNote(note: Note, result: (UiState<Pair<Note, String>>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE)
            .document()
        note.id = document.id
        document.set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(note, "Note has been created successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }

    override fun updateNote(note: Note, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE)
            .document(note.id)
        document.set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun deleteNote(note: Note, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE)
            .document(note.id)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("Note has been delete successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
}