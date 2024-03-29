package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.listener.OnInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.extensions.load
import ru.netology.nmedia.extensions.loadCircle


class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    private val countOperator = CountOperator()

    fun bind(post: Post) {
        binding.apply {
            nickname.text = post.author
            published.text = post.published
            postText.text = post.content
            views.text = countOperator.shortingByLetters(post.views)
            share.text = countOperator.shortingByLetters(post.shares)
            share.isChecked = post.sharedByMe
            like.text = countOperator.shortingByLetters(post.likes)
            like.isChecked = post.likedByMe
            postAvatar.loadCircle("${BuildConfig.BASE_URL}avatars/${post.authorAvatar}")
            attachment.load("${BuildConfig.BASE_URL}media/${post.attachment?.url}")
            attachment.isVisible = !post.attachment?.url.isNullOrBlank()
            videoGroup.isVisible = !post.video.isNullOrEmpty()

            videoPreview.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }

            attachment.setOnClickListener {
                onInteractionListener.onShowAttachment(post)
            }

            playVideo.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }

            like.setOnClickListener {
                    onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                    onInteractionListener.onShare(post)
            }

            menu.isVisible = post.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            root.setOnClickListener {
                onInteractionListener.onOpenPost(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class CountOperator {
    fun shortingByLetters(count: Long): String {
        val out = when ((count / 10) >= 100 && (count / 10) < 100_000) {
            true -> when ((count % 1000) / 100 < 1 || (count >= 10_000)) {
                true -> (count / 1000).toString() + "K"
                false -> (count / 1000).toString() +
                        "." + ((count % 1000) / 100).toString() + "K"
            }

            false -> when ((count / 10) >= 100_000) {
                true -> when ((count % 1000_000) / 100_000 < 1) {
                    true -> (count / 1000_000).toString() + "M"
                    false -> (count / 1000_000).toString() +
                            "." + ((count % 1000_000) / 100_000).toString() + "M"
                }
                false -> "$count"
            }
        }
        return out
    }
}
