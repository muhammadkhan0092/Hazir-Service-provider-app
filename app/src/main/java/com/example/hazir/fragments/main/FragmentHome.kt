package com.example.hazir.fragments.main


import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.R
import com.example.hazir.adapters.PostsAdapter
import com.example.hazir.data.DataPost
import com.example.hazir.databinding.FragmentHomeBinding
import com.example.hazir.utils.HorizontalDecoration
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.HomeViewModel
import com.example.hazir.viewModel.vmf.HomeFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentHome : Fragment(){
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter : PostsAdapter
    val viewModel by viewModels<HomeViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        HomeFactory(firstore,firebaseAuth)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPosts()
        setupAllCoursesRv()
        onClickListeners()
        onSeeAllClick()
        observePosts()
    }

    private fun observePosts() {
        lifecycleScope.launch {
            viewModel.postData.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar13.visibility = View.INVISIBLE
                    }
                    is Resource.Loading ->{
                        binding.progressBar13.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d("khan","submitting list ${it.data?.size}")
                        binding.progressBar13.visibility = View.INVISIBLE
                        adapter.differ.submitList(it.data)
                        Log.d("khan","current list ${adapter.differ.currentList.size}")
                    }
                    is Resource.Unspecified -> {
                    }
                }
            }
        }
    }

    private fun getPosts() {
        viewModel.getPosts()
    }

    private fun onClickListeners() {
        onCvCreate()
        onCommentClicked()
        onLikedClicked()
    }

    private fun onLikedClicked() {
        adapter.onLikedClicked = {data->
            val newList : MutableList<DataPost> = mutableListOf()
            val list = adapter.differ.currentList.toMutableList()
            list.forEach {
                if(it.id==data.id){
                    if(FirebaseAuth.getInstance().uid.toString() in it.likes){
                        val likes = it.likes.toMutableList()
                        likes.remove(FirebaseAuth.getInstance().uid.toString())
                        val newData = it.copy(likes = likes)
                        newList.add(newData)
                        viewModel.updatePosts(newData)
                        Log.d("khan","after removal ${it.likes}")
                    }
                    else
                    {
                        val likes = it.likes.toMutableList()
                        likes.add(FirebaseAuth.getInstance().uid.toString())
                        val newData = it.copy(likes = likes)
                        newList.add(newData)
                        viewModel.updatePosts(newData)
                        Log.d("khan","after addition ${it.likes}")
                    }
                }
                else
                {
                    newList.add(it)
                }
            }
            adapter.differ.submitList(newList.toList())
        }
    }

    private fun onCommentClicked() {
        adapter.onCommentClicked = {
            val commentBottomSheet = CommentBottomView(it)
            commentBottomSheet.show(parentFragmentManager, "CommentBottomView")
        }
    }

    private fun onCvCreate() {
        binding.button10.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentCreatePost)
        }
    }

    private fun onSeeAllClick() {
        binding.ivMore.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentCategories)
        }
        binding.tvMore.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentCategories)
        }
        binding.catMore.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentCategories)
        }
    }

    private fun setupAllCoursesRv() {
        adapter = PostsAdapter(requireContext())
        binding.rvMain.adapter = adapter
        binding.rvMain.addItemDecoration(HorizontalDecoration(30))
        binding.rvMain.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        binding.rvMain.isNestedScrollingEnabled = false
    }
}