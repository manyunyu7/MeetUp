package com.senjapagi.meetup.ModelAdapter

data class model_order_basic (
    val id: String,
    val room_name: String,
    val pax: String,
    val status: String,
    val start: String,
    val end: String,
    val thumbnails: String,
    var rate: String,
    var comment: String
)