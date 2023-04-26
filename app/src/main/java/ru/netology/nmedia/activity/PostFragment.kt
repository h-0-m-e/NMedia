package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.listener.OnInteractionListenerImpl
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by viewModels(
            ownerProducer = ::requireParentFragment
        )

        val currentPostId = requireArguments().textArg?.toLong()

        val animationSlideDown = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_down)
        val animationSlideUp = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_up)

        val interactionListener by lazy {
            object : OnInteractionListenerImpl(this.requireActivity(), viewModel) {

                override fun onRemove(post: Post) {
                    super.onRemove(post)
                    findNavController().navigate(R.id.action_postFragment_to_feedFragment)
                }

                override fun onEdit(post: Post) {
                    super.onEdit(post)
                    findNavController().navigate(
                        R.id.action_postFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        })
                }
            }
        }

        binding.post.apply {
            viewModel.data.observe(viewLifecycleOwner) { it ->
                val viewHolder = PostViewHolder(binding.post, interactionListener)
                val post = it.posts.find { it.id == currentPostId }
                post?.let { viewHolder.bind(post) }
            }
        }

        binding.banner.positiveButton.setOnClickListener {
            viewModel.loadPosts()
            findNavController().navigateUp()
            binding.banner.root.startAnimation(animationSlideUp)
            binding.banner.root.isVisible = false
        }
        binding.banner.negativeButton.setOnClickListener {
            ActivityCompat.finishAffinity(this.requireActivity())
        }

        viewModel.smallErrorHappened.observe(viewLifecycleOwner){

            binding.banner.root.isVisible = true
            binding.banner.root.startAnimation(animationSlideDown)
        }
        return binding.root
    }
}
