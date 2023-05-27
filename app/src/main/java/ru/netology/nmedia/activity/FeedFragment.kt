package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.listener.OnInteractionListenerImpl
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.types.ErrorType
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val interactionListener by lazy {
        object : OnInteractionListenerImpl(
            this@FeedFragment.requireActivity(), viewModel
        ) {

            override fun onOpenPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
            }

            override fun onEdit(post: Post) {
                super.onEdit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
            }

            override fun onShowAttachment(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_photoFragment,
                    Bundle().apply {
                        textArg = "${BuildConfig.BASE_URL}media/${post.attachment!!.url}"

                    })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val swipeRefresh = binding.swiperefresh

        val adapter = PostsAdapter(interactionListener)

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { data ->
            val newPost = adapter.currentList.size < data.posts.size
            adapter.submitList(data.posts) {
                if (newPost) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
            binding.emptyText.isVisible = data.empty
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.swiperefresh.isRefreshing = state.refreshing
            when (state.error) {
                ErrorType.LOADING ->
                    Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.loadPosts() }
                        .show()
                ErrorType.SAVE ->
                    Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.save() }
                        .show()
                ErrorType.LIKE ->
                    Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.likeById(viewModel.lastPost.value!!) }
                        .show()
                ErrorType.REMOVE ->
                    Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.removeById(viewModel.lastId.value!!) }
                        .show()
                null -> Unit

            }
        }

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

        viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.newPostsButton.visibility = VISIBLE
            }
        }

        binding.newPostsButton.setOnClickListener {
            viewModel.showHiddenPosts()
            binding.list.smoothScrollToPosition(0)
            it.visibility = GONE
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.add.setOnClickListener {
            viewModel.removeEdit()
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }
}
