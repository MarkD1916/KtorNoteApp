package com.vmakdandroiddev.data

import com.vmakdandroiddev.data.collections.Note
import com.vmakdandroiddev.data.collections.User
import com.vmakdandroiddev.security.checkHashForPassword
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.coroutine.toList as KMongoToList
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

/*
* Файл содежит функции для взаимодействия с БД
* File contains methods for interact with database
* */

private val client = KMongo.createClient().coroutine // Mongo will use coroutine operation for db
private val database = client.getDatabase("NotesDatabase")

private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean {
    return users.findOne(User::email eq email) != null
}

suspend fun checkPasswordForEmail(email: String, password: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return checkHashForPassword(password, actualPassword)
}

suspend fun getNotesForUser(email: String): List<Note> {
    println("testcase ${notes.find(Note::owners contains email).publisher.KMongoToList()}")
    return notes.find(Note::owners contains email).toList()
}

suspend fun saveNote(note: Note): Boolean {
    val noteExists = notes.findOneById(note.id) != null
    return if(noteExists){
        notes.updateOneById(note.id, note).wasAcknowledged()
    } else{
        notes.insertOne(note).wasAcknowledged()
    }
}

suspend fun deleteNoteForUser(email: String, noteID: String): Boolean{
    val note = notes.findOne(Note::id eq noteID, Note::owners contains email)
    note?.let{ note->
        if (note.owners.size > 1){
            // the note has multiple owners, just delete the email from the owners list
            val newOwners = note.owners - email
            val updateResult = notes.updateOne(Note::id eq note.id, setValue(Note::owners, newOwners))
            return updateResult.wasAcknowledged()
        }
        return notes.deleteOneById(note.id).wasAcknowledged()
    } ?: return false
}

suspend fun isOwnerOfNote(noteID:String, owner:String): Boolean{
    val note = notes.findOneById(noteID) ?: return false
    return owner in note.owners
}

suspend fun addOwnerToNote(noteID: String, owner:String): Boolean{
    val owners = notes.findOneById(noteID)?.owners ?: return false
    return notes.updateOneById(noteID, setValue(Note::owners, owners + owner)).wasAcknowledged()
}