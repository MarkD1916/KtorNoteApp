package com.vmakdandroiddev.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Note(
    val title: String,
    val content: String,
    val date: Long,
    val owners: List<String>, // list of owners Email
    val color: String,
    @BsonId
    val id: String = ObjectId().toString()
)
