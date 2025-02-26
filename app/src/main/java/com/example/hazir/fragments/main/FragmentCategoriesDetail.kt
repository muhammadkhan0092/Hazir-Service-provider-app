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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.R
import com.example.hazir.utils.VerticalDecoration
import com.example.hazir.adapters.CatgoriesDetailAdapter
import com.example.hazir.data.GigData
import com.example.hazir.databinding.FragmentCategoieDetailBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.CategoryDetailViewModel
import com.example.hazir.viewModel.vmf.CategoryDetailFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentCategoriesDetail : Fragment(){
    private lateinit var binding: FragmentCategoieDetailBinding
    private lateinit var categoiesDetailAdapter: CatgoriesDetailAdapter
    val viewModel by viewModels<CategoryDetailViewModel>{
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        CategoryDetailFactory(auth,firestore)
    }
    private val args by navArgs<FragmentCategoriesDetailArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoieDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         getCategory()
         setupAllCoursesRv()
         onClickListeners()
         observeGigData()
     }

    private fun observeGigData() {
        lifecycleScope.launch {
            viewModel.gigData.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar3.visibility = View.INVISIBLE
                        Log.d("khan","Error Fetching Data")
                        Toast.makeText(requireContext(), "Cannot fetch Information right now", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar3.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar3.visibility = View.INVISIBLE
                        filterResults(it.data)
                    }
                    is Resource.Unspecified -> {

                    }
                }

            }
        }
    }

    private fun filterResults(data: List<GigData>?) {
        val newList : MutableList<GigData> = mutableListOf()
        if (data != null) {
            data.forEach {
                if(it.uid!=FirebaseAuth.getInstance().uid){
                    newList.add(it)
                }
            }
        }
        categoiesDetailAdapter.differ.submitList(newList)
    }

    private fun getCategory() {
        val c = args.category
        viewModel.getSpecificCategoryDetail(c)
    }

    private fun onClickListeners() {
        binding.imageView21.setOnClickListener {
            findNavController().popBackStack()
        }
        categoiesDetailAdapter.onClick = {
            findNavController().navigate(R.id.action_fragmentCategoriesDetail_to_fragmentGigDetail2)
        }
        binding.cvCreateGig.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentCategoriesDetail_to_fragmentCreateGig)
        }
        categoiesDetailAdapter.onClick = {
            val b = Bundle().apply {
                putParcelable("gig",it)
            }
            findNavController().navigate(R.id.action_fragmentCategoriesDetail_to_fragmentGigDetail2,b)
        }
    }




    private fun setupAllCoursesRv() {
        categoiesDetailAdapter = CatgoriesDetailAdapter()
        binding.rvCategoryDetail.adapter = categoiesDetailAdapter
        binding.rvCategoryDetail.addItemDecoration(VerticalDecoration(30))
        binding.rvCategoryDetail.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        //binding.rvMain.isNestedScrollingEnabled = false
    }
}