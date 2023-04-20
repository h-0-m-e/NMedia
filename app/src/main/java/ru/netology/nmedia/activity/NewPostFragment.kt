package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by viewModels(
            ownerProducer = ::requireParentFragment
        )

        arguments?.textArg?.let(binding.content::setText)
        binding.content.requestFocus()

        val animationSlideDown = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_down)
        val animationSlideUp = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_up)

        binding.buttonEnter.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isNotBlank()) {
                viewModel.changeContent(text)
                viewModel.save()
            } else {
                Toast.makeText(
                    this.context,
                    R.string.empty_content_warning,
                    Toast.LENGTH_SHORT
                ).show()
            }
            AndroidUtils.hideKeyboard(requireView())
        }
        viewModel.postCreated.observe(viewLifecycleOwner){
            viewModel.loadPosts()
            findNavController().navigateUp()
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

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}
