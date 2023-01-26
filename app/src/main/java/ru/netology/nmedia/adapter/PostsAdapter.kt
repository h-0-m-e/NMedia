package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.repository.Post

typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit

class PostsAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.ViewHolder(binding.root) {

    private val countOperator = CountOperator()

    fun bind(post: Post) {
        binding.apply {
            nickname.text = post.author
            published.text = post.published
            postText.text = post.content
            likesCount.text = countOperator.shortingByLetters(post.likes)
            shareCount.text = countOperator.shortingByLetters(post.shares)
            likesButton.setImageResource(
                if (post.likedByMe) R.drawable.ic_liked_24
                else R.drawable.ic_baseline_favorite_border_24
            )
            likesButton.setOnClickListener {
                onLikeListener(post)
            }
            shareButton.setOnClickListener {
                onShareListener(post)
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
