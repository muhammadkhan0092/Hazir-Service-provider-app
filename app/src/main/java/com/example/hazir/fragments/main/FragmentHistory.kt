package com.example.hazir.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.activity.MainActivity
import com.example.hazir.utils.VerticalDecoration
import com.example.hazir.adapters.HistoryAdapter
import com.example.hazir.data.HistoryData
import com.example.hazir.databinding.FragmentHistoryBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.HistoryViewModel
import com.example.hazir.viewModel.vm.RatingViewModel
import com.example.hazir.viewModel.vmf.HistoryFactory
import com.example.hazir.viewModel.vmf.RatingFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentHistory : Fragment(){
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyData: MutableList<HistoryData>
    val viewModel by viewModels<HistoryViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseStorage = FirebaseStorage.getInstance()
        HistoryFactory(firstore,firebaseStorage)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBnB()
        setupRv()
        getData()
        observeHistoryData()
    }

    private fun hideBnB() {
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }
    private fun observeHistoryData() {
        lifecycleScope.launch {
            viewModel.get.collectLatest {
                when(it){
                    is Resource.Error ->{
                        binding.progressBar10.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar10.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        val data =it.data
                        if(data!=null){
                            historyData = data
                            historyAdapter.differ.submitList(historyData)
                        }
                        binding.progressBar10.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun getData() {
        viewModel.updateMessageModelAndupdateGigAndUpdateHistory(FirebaseAuth.getInstance().uid!!)
    }


    private fun setupRv() {
        historyAdapter = HistoryAdapter()
        binding.rvHistory.adapter = historyAdapter
        binding.rvHistory.layoutManager  = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvHistory.addItemDecoration(VerticalDecoration(50))
    }


}