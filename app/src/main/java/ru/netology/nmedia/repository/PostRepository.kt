package ru.netology.nmedia.repository


import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewer(id: Long): Flow<Int>
    suspend fun getAll()
    suspend fun getAllVisible()
    suspend fun showAll()
//    suspend fun countPosts(): Flow<Int>
    suspend fun likeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
}
