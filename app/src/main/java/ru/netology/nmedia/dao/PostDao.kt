package ru.netology.nmedia.dao

import ru.netology.nmedia.repository.Post

interface PostDao {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun save(post: Post): Post
    fun removeById(id: Long)
}
