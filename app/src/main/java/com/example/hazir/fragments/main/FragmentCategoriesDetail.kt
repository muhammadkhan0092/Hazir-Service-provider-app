package com.example.hazir.fragments.main
import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.R
import com.example.hazir.activity.MainActivity
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
import java.util.ArrayList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class FragmentCategoriesDetail : Fragment(){
    private lateinit var binding: FragmentCategoieDetailBinding
    private lateinit var gigs : List<GigData>
    private lateinit var categoiesDetailAdapter: CatgoriesDetailAdapter
    val viewModel by viewModels<CategoryDetailViewModel>{
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        CategoryDetailFactory(auth,firestore)
    }
    private val args by navArgs<FragmentCategoriesDetailArgs>()
    private var isLocationBased = 1
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
         hideBnB()
         getCategory()
         setupAllCoursesRv()
         onClickListeners()
         observeGigData()
     }

    private fun hideBnB() {
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.INVISIBLE
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
                        gigs = it.data!!
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
        Log.d("khan","received gigs is ${data?.size}")
        if (data != null) {
            data.forEach {
                if(it.uid==FirebaseAuth.getInstance().uid){
                    newList.add(it)
                }
            }
        }
        sortAndSubmit(newList)
    }

    private fun sortAndSubmit(newList: MutableList<GigData>) {
        if(isLocationBased==1){
            val sorted = sortByDistance(newList)
            Log.d("khan","sorted second is " + sorted[0].distance.toString())
            categoiesDetailAdapter.differ.submitList(sorted)
        }
        else
        {

        }
    }

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun sortByDistance(
        gigs: List<GigData>
    ): List<GigData> {
        val sharedPreferences = requireActivity().getSharedPreferences("locationData",MODE_PRIVATE)
        val lon = sharedPreferences.getFloat("lon",0f)
        val lat = sharedPreferences.getFloat("lat",0f)
        val newList : MutableList<GigData> = mutableListOf()
        Log.d("khan","LOCATION RETREIVED IS ${lon} and ${lat}")
        gigs.forEach { gigs ->
            val dist = haversineDistance(lat.toDouble(), lon.toDouble(), gigs.locationData.latitiude, gigs.locationData.longitude)
            val newGig = gigs.copy(distance =  String.format("%.1f", dist).toDouble())
            newList.add(newGig)
        }
        newList.sortBy { it.distance }
        return newList
    }

    private fun getCategory() {
        val c = args.category
        viewModel.getSpecificCategoryDetail(c)
    }

    @SuppressLint("NewApi")
    private fun onClickListeners() {
        binding.imageView21.setOnClickListener {
            findNavController().popBackStack()
        }
        categoiesDetailAdapter.onClick = {
            findNavController().navigate(R.id.action_fragmentCategoriesDetail_to_fragmentGigDetail2)
        }
        categoiesDetailAdapter.onClick = {
            val b = Bundle().apply {
                putParcelable("gig",it)
            }
            findNavController().navigate(R.id.action_fragmentCategoriesDetail_to_fragmentGigDetail2,b)
        }
        onFilterClicked()
        onBottomDismissed()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onBottomDismissed() {
        parentFragmentManager.setFragmentResultListener("data", viewLifecycleOwner) { _, bundle ->
            val result = bundle.getParcelableArrayList("gigs",GigData::class.java)
            updateRv(result!!)
        }
    }

    private fun updateRv(result: ArrayList<GigData>) {
        Log.d("khan","updating rv by ${result.size}")
        categoiesDetailAdapter.differ.submitList(result.toList())
    }

    private fun onFilterClicked() {
        binding.imageView25.setOnClickListener {
            val filterBottomView = FilterBottomView(gigs,requireContext())
            filterBottomView.show(parentFragmentManager, "FilterBottomView")
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