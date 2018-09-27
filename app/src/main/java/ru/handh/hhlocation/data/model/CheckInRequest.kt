package ru.handh.hhlocation.data.model

data class CheckInRequest(
        val uid: String,
        val posx: Int,
        val posy: Int
)