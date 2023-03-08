package ru.netology.nmedia.service.actions

data class Like (
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String
    )
