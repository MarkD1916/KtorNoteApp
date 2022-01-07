package com.vmakdandroiddev.data

import com.vmakdandroiddev.data.collections.Note
import com.vmakdandroiddev.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.coroutine.toList as KMongoToList
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

/*
* Файл содежит функции для взаимодействия с БД
* File contains methods for interact with database
* */

private val client = KMongo.createClient().coroutine // Mongo will use coroutine operation for db
private val database = client.getDatabase("NotesDatabase")

private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User): Boolean{
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean{
    return users.findOne(User::email eq email) != null
}

suspend fun checkPasswordForEmail(email:String, password:String): Boolean{
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return actualPassword == password
}

suspend fun getNotesForUser(email: String): List<Note>{
    return notes.find(Note::owners contains email).publisher.KMongoToList()
}