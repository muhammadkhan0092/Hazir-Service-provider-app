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
import com.example.hazir.R
import com.example.hazir.activity.MainActivity
import com.example.hazir.adapters.CategoriesAdapter
import com.example.hazir.data.CategoriesData
import com.example.hazir.databinding.FragmentCategoriesBinding
import com.example.hazir.utils.Resource
import com.example.hazir.utils.constants
import com.example.hazir.utils.constants.allCategories
import com.example.hazir.viewModel.vm.CategoryViewModel
import com.example.hazir.viewModel.vm.CreateGigViewModel
import com.example.hazir.viewModel.vmf.CategoryFactory
import com.example.hazir.viewModel.vmf.CreateGigFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentCategories : Fragment(){
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
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
        binding = FragmentCategoriesBinding.inflate(inflater,container,false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         hideBnB()
         setupAllCoursesRv()
         onClickListeners()
    }

    private fun hideBnB() {
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.INVISIBLE
    }


    private fun onClickListeners() {
        categoriesAdapter.onClick ={
            val bundle = Bundle()
            bundle.putString("category",it.categories)
            findNavController().navigate(R.id.action_fragmentCategories_to_fragmentCategoriesDetail,bundle)
        }
        binding.imageView21.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.button11.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentCategories_to_fragmentCreateGig)
        }
    }

    private fun setupAllCoursesRv() {
        categoriesAdapter = CategoriesAdapter()
        binding.rvCategories.adapter = categoriesAdapter
       // binding.rvCategories.addItemDecoration(HorizontalDecoration(30))
        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(),3)
        binding.rvCategories.isNestedScrollingEnabled = false
        categoriesAdapter.differ.submitList(allCategories)
    }
}