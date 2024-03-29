package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.types.ErrorType
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.utils.SingleLiveEvent
import javax.inject.Inject

private val empty = Post(
    id = 0L,
    authorId = 0L,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    likedByMe = false,
    sharedByMe = false,
    likes = 0,
    shares = 0,
    views = 0
)

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
) : ViewModel() {
//    private val repository: PostRepository = PostRepositoryImpl(
//        AppDb.getInstance(application).postDao()
//    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: LiveData<FeedModel> = appAuth.data.flatMapLatest { token ->
        repository.data
            .map{ posts ->
                posts.map{
                    it.copy(ownedByMe = it.authorId == token?.id)
                }
            }
            .map { FeedModel(it) }
    }
        .asLiveData(Dispatchers.Default)


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewer(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }
        .distinctUntilChanged()

    private val edited = MutableLiveData(empty)

    private val _lastId = MutableLiveData<Long>()
    val lastId: LiveData<Long>
        get() = _lastId

    private val _lastPost =
        MutableLiveData(
            Post(
                0,
                0,
                "",
                "",
                "",
                "",
                likedByMe = false,
                sharedByMe = false,
                0,
                0,
                0
            )
        )
    val lastPost: LiveData<Post>
        get() = _lastPost

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        _dataState.value = FeedModelState(loading = true)
        try {
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LOADING)
        }
    }

    fun refresh() = viewModelScope.launch {
        _dataState.value = FeedModelState(refreshing = true)
        try {
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LOADING)
        }
    }

    fun showHiddenPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.showAll()
            _dataState.value = FeedModelState()
        } catch (e: java.lang.Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LOADING)
        }
    }

    fun removeEdit() {
        edited.value = empty
    }


    fun save() = viewModelScope.launch {
        try {
            edited.value?.let {
                when (val photo = _photo.value) {
                    null -> repository.save(it.copy(ownedByMe = true))
                    else -> repository.saveWithAttachment(it.copy(ownedByMe = true), photo)
                }

            }
            edited.value = empty
            _postCreated.postValue(Unit)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.SAVE)
        }
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

    fun likeById(post: Post) = viewModelScope.launch {
        _lastPost.value = post
        try {
            repository.likeById(lastPost.value!!.id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LIKE)
        }
    }

    fun shareById(id: Long) = viewModelScope.launch {
        _lastId.value = id
        repository.shareById(_lastId.value!!)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        _lastId.value = id
        try {
            repository.removeById(_lastId.value!!)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.REMOVE)
        }
    }

    fun setPhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clearPhoto() {
        _photo.value = null
    }


}
