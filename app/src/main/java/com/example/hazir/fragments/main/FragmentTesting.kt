package com.example.hazir.fragments.main
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.R
import com.example.hazir.adapters.CategoriesAdapter
import com.example.hazir.adapters.PostsAdapter
import com.example.hazir.data.CategoriesData
import com.example.hazir.databinding.FragmentCategoriesBinding
import com.example.hazir.databinding.FragmentTestingBinding
import com.example.hazir.utils.HorizontalDecoration
import com.example.hazir.utils.Resource
import com.example.hazir.utils.constants
import com.example.hazir.viewModel.vm.CategoryViewModel
import com.example.hazir.viewModel.vm.CreateGigViewModel
import com.example.hazir.viewModel.vmf.CategoryFactory
import com.example.hazir.viewModel.vmf.CreateGigFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentTesting : Fragment(){
    private lateinit var binding: FragmentTestingBinding
    private lateinit var adapter: PostsAdapter
    val viewModel by viewModels<CategoryViewModel>{
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        CategoryFactory(auth,firestore)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestingBinding.inflate(inflater,container,false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       setupAllCoursesRv()
    }

    private fun setupAllCoursesRv() {
        adapter = PostsAdapter(requireContext())
        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(HorizontalDecoration(30))
        binding.rv.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,false)
        //binding.rvMain.isNestedScrollingEnabled = false
    }

}