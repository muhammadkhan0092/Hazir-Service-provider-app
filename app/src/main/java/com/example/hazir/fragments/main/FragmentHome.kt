package com.example.hazir.fragments.main


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.R
import com.example.hazir.activity.MainActivity
import com.example.hazir.adapters.CategoriesAdapter
import com.example.hazir.adapters.PostsAdapter
import com.example.hazir.data.CategoriesData
import com.example.hazir.data.DataPost
import com.example.hazir.databinding.FragmentHomeBinding
import com.example.hazir.utils.HorizontalDecoration
import com.example.hazir.utils.Resource
import com.example.hazir.utils.constants.allCategories
import com.example.hazir.viewModel.vm.HomeViewModel
import com.example.hazir.viewModel.vmf.HomeFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentHome : Fragment(){
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter : PostsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
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
        hideBnB()
        getUserName()
        getPosts()
        setupAllCoursesRv()
        setupCategoriesAdapter()
        onClickListeners()
        onSeeAllClick()
        observePosts()
        observeMessageClicked()
    }

    private fun hideBnB() {
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun setupCategoriesAdapter() {
        categoriesAdapter = CategoriesAdapter()
        binding.rvCat.adapter = categoriesAdapter
        binding.rvCat.addItemDecoration(HorizontalDecoration(30))
        binding.rvCat.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rvCat.isNestedScrollingEnabled = false
        val list = allCategories.subList(0,3).toMutableList()
        list.add(CategoriesData(11,R.drawable.ic_forward,"#FFD88D","See All"))
        categoriesAdapter.differ.submitList(list.toList())
    }

    private fun getUserName() {
        val sharedPreferences = requireActivity().getSharedPreferences("mydata", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")
        binding.textView13.text = "Welcome $name"
    }

    private fun observeMessageClicked() {
        lifecycleScope.launch {
            viewModel.message.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar13.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{
                        binding.progressBar13.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        val data = it.data
                        val bundle = Bundle().also {
                            it.putParcelable("chat",data)
                        }
                        findNavController().navigate(R.id.action_fragmentHome_to_fragmentMessageDetail,bundle)
                        Toast.makeText(requireContext(), "Success ${it.data}", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Unspecified -> {
                    }
                }
            }
        }
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
        onMessageClicked()
        onCategoryClicked()
        onProviderClicked()
        onServiceGetterClicked()
    }

    private fun onServiceGetterClicked() {
        binding.btnGetter.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentCategories)
        }
    }

    private fun onProviderClicked() {
        binding.btnProvider.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmation")
                .setMessage("Do you want to create a gig?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_fragmentHome_to_fragmentCreateGig)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    private fun onCategoryClicked() {
        categoriesAdapter.onClick = {
            if(it.categories=="See All"){
                findNavController().navigate(R.id.action_fragmentHome_to_fragmentCategories)
            }
            else
            {
                val bundle = Bundle()
                bundle.putString("category",it.categories)
                findNavController().navigate(R.id.action_fragmentHome_to_fragmentCategoriesDetail2,bundle)
            }
        }
    }

    private fun onMessageClicked() {
        adapter.onMessageClicked = {
            Log.d("khan","message clicked")
            if(it.uuid==FirebaseAuth.getInstance().uid){
                Toast.makeText(requireContext(), "This is your own post. Sorry", Toast.LENGTH_SHORT).show()
            }
            else
            {
                viewModel.getGigs(it)
            }
        }
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

    }

    private fun setupAllCoursesRv() {
        adapter = PostsAdapter(requireContext())
        binding.rvMain.adapter = adapter
        binding.rvMain.addItemDecoration(HorizontalDecoration(30))
        binding.rvMain.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
     //   binding.rvMain.isNestedScrollingEnabled = false
    }
}