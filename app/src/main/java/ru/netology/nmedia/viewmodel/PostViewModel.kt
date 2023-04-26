package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedPosts
import ru.netology.nmedia.repository.GeneralCallback
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent

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

    private val _smallErrorHappened = SingleLiveEvent<Unit>()
    val smallErrorHappened: LiveData<Unit>
        get() = _smallErrorHappened

    private val edited = MutableLiveData(empty)

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedPosts(loading = true)
        repository.getAll(object : GeneralCallback<List<Post>> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedPosts(posts = data, empty = data.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedPosts(error = true))
            }

        })
    }

    fun removeEdit() {
        edited.value = empty
    }


    fun save() {
        edited.value?.let {
            repository.save(it, object : GeneralCallback<Post> {
                override fun onSuccess(data: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _smallErrorHappened.postValue(Unit)
                }

            })

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

    fun likeById(post: Post) {
        if(!post.likedByMe) {
            repository.likeById(post.id, object : GeneralCallback<Post> {
                override fun onSuccess(data: Post) {
                    _data.postValue(FeedPosts(posts = _data.value?.posts.orEmpty().map {
                        if (it.id == post.id) data else it
                    }))
                }

                override fun onError(e: Exception) {
                    _smallErrorHappened.postValue(Unit)
                }

            })
        } else{
            repository.unlikeById(post.id, object : GeneralCallback<Post> {
                override fun onSuccess(data: Post) {
                    _data.postValue(FeedPosts(posts = _data.value?.posts.orEmpty().map {
                        if (it.id == post.id) data else it
                    }))
                }

                override fun onError(e: Exception) {
                    _smallErrorHappened.postValue(Unit)
                }

            })

        }


    }

    fun shareById(id: Long) {
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.removeById(id, object : GeneralCallback<Unit> {
            override fun onSuccess(data: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
                _smallErrorHappened.postValue(Unit)
            }
        })
    }
}
