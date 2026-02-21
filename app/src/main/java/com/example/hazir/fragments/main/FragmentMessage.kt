package com.example.hazir.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.R
import com.example.hazir.activity.MainActivity
import com.example.hazir.adapters.MessageAdapter
import com.example.hazir.databinding.FragmentMessageBinding
import com.example.hazir.utils.Resource
import com.example.hazir.utils.VerticalDecoration
import com.example.hazir.viewModel.vm.MessageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentMessage : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    val viewModel by viewModels<MessageViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAllCoursesRv()
        hideBnB()
        onClickListeners()
        observeAllChats()
    }

    private fun hideBnB() {
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun observeAllChats() {
        viewLifecycleOwner.lifecycleScope.apply {
            launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.events.collectLatest {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest {
                        when (it.isLoading) {
                            true -> binding.progressBar6.visibility = View.VISIBLE
                            false -> binding.progressBar6.visibility = View.INVISIBLE
                        }
                        messageAdapter.differ.submitList(it.messages)
                    }
                }
            }
        }
    }


    private fun onClickListeners() {
        binding.imageView21.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMessage_to_fragmentHistory)
        }
        messageAdapter.onClick = { model ->
            val bundle = Bundle().also {
                it.putParcelable("chat", model)
            }
            findNavController().navigate(
                R.id.action_fragmentMessage_to_fragmentMessageDetail,
                bundle
            )
        }
    }


    private fun showBottomNavigationBar() {
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }


    private fun setupAllCoursesRv() {
        messageAdapter = MessageAdapter()
        binding.rvMessage.adapter = messageAdapter
        binding.rvMessage.addItemDecoration(VerticalDecoration(90))
        binding.rvMessage.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
    }
}