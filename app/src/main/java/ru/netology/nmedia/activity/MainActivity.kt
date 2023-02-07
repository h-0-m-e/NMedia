package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.launch
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.repository.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {


    val viewModel: PostViewModel by viewModels()

    private val newPostContract = registerForActivityResult(
        NewPostActivity.NewPostContract){ content ->
        content ?: return@registerForActivityResult
        viewModel.changeContent(content.toString())
        viewModel.save()
    }

    // TODO: Tried to use same contract with different output
    private val editPostContract = registerForActivityResult(
        NewPostActivity.NewPostContract){ content ->
        content ?: return@registerForActivityResult
        viewModel.changeContent(content.toString())
        viewModel.save()
    }

    private val interactionListener = object : OnInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)
            viewModel.shareById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        // TODO: Send post.content to NewPostActivity with using contract and intent
        override fun onEdit(post: Post) {
            viewModel.edit(post)
            Intent(this@MainActivity, NewPostActivity::class.java).apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostsAdapter(interactionListener)

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = adapter.currentList.size < posts.size
            adapter.submitList(posts) {
                if (newPost) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

        binding.add.setOnClickListener{
            newPostContract.launch()
        }

//        binding.editingDeny.setOnClickListener {
//            with(binding.content) {
//                setText("")
//                editingGroup.visibility = View.GONE
//                clearFocus()
//                AndroidUtils.hideKeyboard(this)
//            }
//        }

//        binding.save.setOnClickListener {
//            with(binding.content) {
//                if (text.isNullOrBlank()) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        R.string.empty_content_warning,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                viewModel.changeContent(text.toString())
//                viewModel.save()
//
//                setText("")
//                editingGroup.visibility = View.GONE
//                clearFocus()
//                AndroidUtils.hideKeyboard(this)
//            }
//        }
    }
}
