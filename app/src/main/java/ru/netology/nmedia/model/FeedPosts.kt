package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class FeedPosts (
    val posts: List<Post> = emptyList(),
    val error: Boolean = false,
    val smallError: Boolean = false,
    val loading: Boolean = false,
    val empty: Boolean = false,
)
