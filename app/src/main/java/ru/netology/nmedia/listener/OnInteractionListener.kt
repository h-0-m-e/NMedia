package ru.netology.nmedia.listener

import ru.netology.nmedia.repository.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onPlayVideo(post: Post) {}
    fun onOpenPost(post:Post) {}
}
