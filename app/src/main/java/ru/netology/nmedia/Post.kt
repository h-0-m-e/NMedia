package ru.netology.nmedia

data class Post(
    var id: Long,
    var author: String,
    var content: String,
    var published: String,
    var likedByMe: Boolean = false,
    var likes: Long,
    var shares: Long
)
