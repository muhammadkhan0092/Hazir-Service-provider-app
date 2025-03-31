package com.example.hazir.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.adapters.CommentsAdapter
import com.example.hazir.data.DataComments
import com.example.hazir.data.DataPost
import com.example.hazir.databinding.CommentBottomViewBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.CommentViewModel
import com.example.hazir.viewModel.vmf.CommentVmf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class CommentBottomView(val post : DataPost) : BottomSheetDialogFragment() {
    private lateinit var binding : CommentBottomViewBinding
    private lateinit var adapter : CommentsAdapter
    val viewModel by viewModels<CommentViewModel>{
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        CommentVmf(auth,firestore)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CommentBottomViewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRv()
        onCommentCLicked()
        observeUpdateComments()
    }

    private fun observeUpdateComments() {
        lifecycleScope.launch {
            viewModel.postData.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar14.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Comment Failed", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar14.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar14.visibility = View.INVISIBLE
                        val list = it.data!!
                        binding.editTextText7.text.clear()
                        adapter.differ.submitList(list.comments)
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun onCommentCLicked() {
        binding.button9.setOnClickListener {
            val content = binding.editTextText7.text.toString()
            val comment = DataComments(UUID.randomUUID().toString(),content,FirebaseAuth.getInstance().uid!!)
            val list = post.comments.toMutableList()
            list.add(comment)
            val newData = post.copy(comments = list)
            viewModel.getGigs(newData)
        }
    }

    private fun setupRv() {
        adapter = CommentsAdapter()
        binding.rvComments.adapter = adapter
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        adapter.differ.submitList(post.comments)
        adapter.notifyDataSetChanged()
    }
}