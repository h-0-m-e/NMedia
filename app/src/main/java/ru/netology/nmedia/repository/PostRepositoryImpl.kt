package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository{
    override val data: LiveData<List<Post>> =
        postDao.getAll().map {it.map(PostEntity::toDto)}

    override suspend fun getAll() {
        val response = PostApi.service.getAll()
        if (!response.isSuccessful) {
            throw RuntimeException(response.message())
        }
        val posts = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(posts.map { PostEntity.fromDto(it) })
    }

    override suspend fun likeById(id: Long) {
        try{
            postDao.likeById(id)
            val response = PostApi.service.unlikeById(id)
            if (!response.isSuccessful){
                postDao.unlikeById(id)
                throw RuntimeException(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.nmedia.error.UnknownError
        }
    }

    override suspend fun unlikeById(id: Long) {
        try{
            postDao.unlikeById(id)
            val response = PostApi.service.unlikeById(id)
            if (!response.isSuccessful){
                postDao.likeById(id)
                throw RuntimeException(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.nmedia.error.UnknownError
        }


    }

    override suspend fun shareById(id: Long) {
        //Works not good, cause of not implemented on server
        postDao.shareById(id)
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.nmedia.error.UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            postDao.removeById(id)
            val response = PostApi.service.likeById(id)
            if (!response.isSuccessful){
                throw RuntimeException(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.nmedia.error.UnknownError
        }
    }

}
