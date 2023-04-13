package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(post: Post): Post
    fun shareById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)
}
