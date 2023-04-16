package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(postsCallback: GeneralCallback<List<Post>>)
    fun likeById(post: Post, postsCallback: GeneralCallback<Post>)
    fun shareById(id: Long)
    fun save(post: Post, postsCallback: GeneralCallback<Post>)
    fun removeById(id: Long,postsCallback: GeneralCallback<Unit>)
}

interface GeneralCallback<T> {
    fun onSuccess(data:T)
    fun onError(e: Exception)
}
