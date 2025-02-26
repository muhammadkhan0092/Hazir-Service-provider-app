package com.example.hazir.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.utils.HorizontalDecoration
import com.example.hazir.R
import com.example.hazir.adapters.HomeCleaningAdapter
import com.example.hazir.data.CleaningData
import com.example.hazir.databinding.FragmentHomeBinding


class FragmentHome : Fragment(){
    private lateinit var binding: FragmentHomeBinding
    private lateinit var cleaningAdapter: HomeCleaningAdapter
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
        setupAllCoursesRv()
        onSeeAllClick()
        val list = listOf(
            CleaningData(1,R.drawable.testing,"Home Cleaning"),
            CleaningData(2,R.drawable.testing,"Home Cleaning"),
            CleaningData(3,R.drawable.testing,"Home Cleaning"),
            CleaningData(4,R.drawable.testing,"Home Cleaning"),
            CleaningData(5,R.drawable.testing,"Home Cleaning")
        )
        cleaningAdapter.differ.submitList(list)
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
        cleaningAdapter = HomeCleaningAdapter()
        binding.rvMain.adapter = cleaningAdapter
        binding.rvMain.addItemDecoration(HorizontalDecoration(30))
        binding.rvMain.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,false)
        //binding.rvMain.isNestedScrollingEnabled = false
    }
}