package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedPosts
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import kotlin.concurrent.thread

private val empty = Post(
    id = 0L,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    sharedByMe = false,
    likes = 0,
    shares = 0,
    views = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
    )
    private val _data = MutableLiveData<FeedPosts>()
    val data: LiveData<FeedPosts>
        get() = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val edited = MutableLiveData(empty)

    init{
        loadPosts()
    }

    fun loadPosts(){
        _data.value = FeedPosts(loading = true)
        thread {
            try {
                val posts = repository.getAll()
                _data.postValue(FeedPosts(posts = posts, empty = posts.isEmpty()))
            }catch (e: Exception){
                _data.postValue(FeedPosts(error = true))
            }

        }
    }

    fun removeEdit() {
        edited.value = empty
    }


    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content !== text) {
            edited.value = edited.value?.copy(content = text)
        }
    }

    fun likeById(post: Post){
        thread {
            val likedPost = repository.likeById(post)
            _data.postValue(FeedPosts(posts=_data.value?.posts.orEmpty().map { if (it.id == post.id) likedPost else it }))
        }
    }
    fun shareById(id: Long) = repository.shareById(id)

    fun removeById(id: Long){
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            }catch (e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
}
