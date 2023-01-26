package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post (
        id = 1,
        author = "Нетология. Интернет-университет который смог",
        content = "Всем привет, меня зовут Саша, я диктор канала «Мастерская Настроения»",
        published = "14 января в 22:32",
        likedByMe = false,
        likes = 996,
        shares = 9999
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data
    override fun like() {
        post = post.copy(
            likes = if (post.likedByMe)post.likes-1 else post.likes+1,
            likedByMe = !post.likedByMe

        )

        data.value = post
    }

    override fun share() {
        post = post.copy(shares = post.shares+1)
        data.value = post
    }
}
