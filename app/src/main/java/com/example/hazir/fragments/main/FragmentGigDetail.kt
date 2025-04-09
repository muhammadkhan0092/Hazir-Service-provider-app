package com.example.hazir.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.hazir.utils.HorizontalDecoration
import com.example.hazir.R
import com.example.hazir.adapters.RetreiveServiceAdapter
import com.example.hazir.adapters.ReviewsAdapter
import com.example.hazir.data.GigData
import com.example.hazir.data.MessageModel
import com.example.hazir.data.ReviewData
import com.example.hazir.databinding.FragmentGigDetailBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.GigDetailViewModel
import com.example.hazir.viewModel.vmf.GigDetailFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentGigDetail : Fragment(){
    private lateinit var binding: FragmentGigDetailBinding
    private lateinit var retreiveServiceAdapter: RetreiveServiceAdapter
    private lateinit var reviewAdapter: ReviewsAdapter
    private lateinit var giga :GigData
    private  var shouldNavigate = false
    val args by navArgs<FragmentGigDetailArgs>()
    val viewModel by viewModels<GigDetailViewModel>{
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        GigDetailFactory(auth,firestore)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGigDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupServiceRecyclerView()
        setupData()
        setupReviewRv()
        onClickListeners()
        observeCreateChat()
    }

    private fun observeCreateChat() {
        lifecycleScope.launch {
            viewModel.messageCreate.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Log.d("khan","Error")
                        binding.progressBar4.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar4.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d("khan","chat is ${it.data}")
                        if(shouldNavigate) {
                            val chat = it.data
                            navigate(chat)
                        }
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun navigate(chat: MessageModel?) {
        binding.progressBar4.visibility = View.INVISIBLE
        val bundle = Bundle().also {
            it.putParcelable("chat", chat)
        }
        shouldNavigate = false
        findNavController().navigate(
            R.id.action_fragmentGigDetail2_to_fragmentMessageDetail2,
            bundle
        )
    }

    private fun onClickListeners() {
        binding.cvGoTochat.setOnClickListener {
            lifecycleScope.launch {
                shouldNavigate = true
                viewModel.createChatOrGetChat(giga)
            }
        }
    }





    private fun setupData() {
        giga = args.gig
        val gig = args.gig
        binding.tvDescriptionDetail.text = gig.description
        binding.tvJobTitle.text = gig.category
        binding.tvTotalReviews.text = gig.totalOrders.toString()
        Log.d("khan","uri is ${gig.gigImages[0]}")
        Glide.with(requireContext()).load(gig.gigImages[0]).into(binding.ivCover)
        Glide.with(requireContext()).load(gig.profileImage).into(binding.ivProfile)
        retreiveServiceAdapter.differ.submitList(gig.services)
    }

    private fun setupServiceRecyclerView() {
        retreiveServiceAdapter = RetreiveServiceAdapter()
        binding.rvServices.adapter = retreiveServiceAdapter
        binding.rvServices.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvServices.isNestedScrollingEnabled = false
    }

    private fun setupReviewRv() {
        reviewAdapter = ReviewsAdapter()
        binding.rvReview.adapter = reviewAdapter
        binding.rvReview.addItemDecoration(HorizontalDecoration(50))
        binding.rvReview.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    }


}