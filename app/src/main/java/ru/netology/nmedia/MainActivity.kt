package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Интернет-университет который смог",
            content = "Всем привет, меня зовут Саша, я диктор канала «Мастерская Настроения»",
            published = "14 января в 22:32",
            likedByMe = false,
            likes = 996,
            shares = 9999
        )
        binding.apply {
            nickname.text = post.author
            published.text = post.published
            postText.text = post.content

            if (post.likedByMe) {
                likesButton.setImageResource(R.drawable.ic_liked_24)
            }

            likesCount.text = countShorting(post.likes)
            shareCount.text = countShorting(post.shares)

            likesButton.setOnClickListener {
                Log.d("stuff", "Like")
                post.likedByMe = !post.likedByMe
                likesButton.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24
                    else R.drawable.ic_baseline_favorite_border_24
                )

                if (post.likedByMe) post.likes++ else post.likes--
                likesCount.text = countShorting(post.likes)
            }

            shareButton.setOnClickListener {
                Log.d("stuff", "Share")
                post.shares++
                shareCount.text = countShorting(post.shares)
            }

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
