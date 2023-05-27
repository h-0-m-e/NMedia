package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val sharedByMe: Boolean = false,
    val likes: Long,
    val shares: Long,
    val views: Long,
    val video: String? = null,
    val attachment: Attachment? = null
)
