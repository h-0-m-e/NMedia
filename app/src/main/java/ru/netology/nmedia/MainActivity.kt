package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) {post ->
            binding.apply {
                nickname.text = post.author
                published.text = post.published
                postText.text = post.content
                likesCount.text = countShorting(post.likes)
                shareCount.text = countShorting(post.shares)
                likesButton.setImageResource(
                    if(post.likedByMe) R.drawable.ic_liked_24
                    else R.drawable.ic_baseline_favorite_border_24
                )
            }
        }
        binding.likesButton.setOnClickListener{
            viewModel.like()
        }
        binding.shareButton.setOnClickListener {
            viewModel.share()
        }
    }

    private fun countShorting(count: Long): String {
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
