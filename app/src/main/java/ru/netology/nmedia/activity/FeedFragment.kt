package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.listener.OnInteractionListenerImpl
import ru.netology.nmedia.dto.Post
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

        val animationSlideDown = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_down)
        val animationSlideUp = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_up)

        val swipeRefresh = binding.swiperefresh

        val adapter = PostsAdapter(interactionListener)

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            val newPost = adapter.currentList.size < state.posts.size
            adapter.submitList(state.posts) {
                if (newPost) {
                    binding.list.smoothScrollToPosition(0)
                }
            }

            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
            binding.loading.isVisible = state.loading
        }

        binding.banner.positiveButton.setOnClickListener {
            viewModel.loadPosts()
            binding.banner.root.startAnimation(animationSlideUp)
            binding.banner.root.isVisible = false
        }
        binding.banner.negativeButton.setOnClickListener {
            finishAffinity(this.requireActivity())
        }

        viewModel.smallErrorHappened.observe(viewLifecycleOwner){

            binding.banner.root.isVisible = true
            binding.banner.root.startAnimation(animationSlideDown)
        }

        swipeRefresh.setOnRefreshListener{
            swipeRefresh.isRefreshing = true
            viewModel.loadPosts()
            swipeRefresh.isRefreshing = false
        }



        binding.retryButton.setOnClickListener { viewModel.loadPosts() }

        binding.add.setOnClickListener {
            viewModel.removeEdit()
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }
}
