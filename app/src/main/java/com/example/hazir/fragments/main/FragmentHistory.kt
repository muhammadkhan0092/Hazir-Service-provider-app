package com.example.hazir.fragments.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hazir.utils.VerticalDecoration
import com.example.hazir.adapters.HistoryAdapter
import com.example.hazir.data.HistoryData
import com.example.hazir.databinding.FragmentHistoryBinding


class FragmentHistory : Fragment(){
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
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
        setupRv()
        setupData()

    }

    private fun setupData() {
        val list = listOf(
            HistoryData(1,"Graphic Designer","24-02-2002","Purchased by Jahangir"),
            HistoryData(2,"Graphic Designer","24-02-2002","Purchased by Jahangir"),
            HistoryData(3,"Graphic Designer","24-02-2002","Purchased by Jahangir"),
            HistoryData(4,"Graphic Designer","24-02-2002","Purchased by Jahangir"),
            HistoryData(5,"Graphic Designer","24-02-2002","Purchased by Jahangir"),
        )
        historyAdapter.differ.submitList(list)
    }

    private fun setupRv() {
        historyAdapter = HistoryAdapter()
        binding.rvHistory.adapter = historyAdapter
        binding.rvHistory.layoutManager  = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvHistory.addItemDecoration(VerticalDecoration(50))
    }


}