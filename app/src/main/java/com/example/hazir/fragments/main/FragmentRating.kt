package com.example.hazir.fragments.main


import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.hazir.R
import com.example.hazir.data.GigData
import com.example.hazir.data.MessageModel
import com.example.hazir.data.ReviewData
import com.example.hazir.databinding.FragmentRatingBinding
import com.example.hazir.utils.Resource
import com.example.hazir.viewModel.vm.RatingViewModel
import com.example.hazir.viewModel.vmf.RatingFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID


class FragmentRating : Fragment(){
    private lateinit var binding: FragmentRatingBinding
    val viewModel by viewModels<RatingViewModel>{
        val firstore = FirebaseFirestore.getInstance()
        val firebaseStorage = FirebaseStorage.getInstance()
        RatingFactory(firstore,firebaseStorage)
    }
    private val navArgs by navArgs<FragmentRatingArgs>()
    private lateinit var messageModel: MessageModel
    private  var gigData: GigData? = null
    private var currentStar : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRatingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiveData()
        onClickListeners()
        observeGigDetail()
        observeSetGig()
        observeMessageModel()
    }

    private fun observeMessageModel() {
        lifecycleScope.launch {
            viewModel.setModel.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar7.visibility  = View.INVISIBLE
                        Toast.makeText(requireContext(), "Review could not be submitted", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar7.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        findNavController().navigate(R.id.action_fragmentRating_to_fragmentReviewComplete)
                        binding.progressBar7.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun observeSetGig() {
        lifecycleScope.launch {
            viewModel.setGig.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar7.visibility = View.INVISIBLE
                        Log.d("khan","ERROR WHILE SETTING DATA")
                    }
                    is Resource.Loading -> {
                        binding.progressBar7.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d("khan","DATA UPDATED SUCCESSFULLY")
                        binding.progressBar7.visibility = View.INVISIBLE
                        viewModel.updateMessageModel(messageModel)

                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun onClickListeners() {
        onButtonClick()
        binding.starOne.setOnClickListener {
            if(currentStar!=1){
                currentStar = 1
                updateStars(1)
            }
        }
        binding.starTwo.setOnClickListener {
            if(currentStar!=2){
                currentStar = 2
                updateStars(2)
            }
        }
        binding.startThree.setOnClickListener {
           if(currentStar!=3){
               currentStar = 3
               updateStars(3)
           }
        }
        binding.starFour.setOnClickListener {
           if(currentStar!=4){
               currentStar = 5
               updateStars(4)
           }
        }
        binding.starFive.setOnClickListener {
            if(currentStar!=5){
                currentStar = 5
                updateStars(5)
            }
        }
    }



    private fun updateStars(selectedStars: Int) {
        val context = binding.root.context
        val goldenStar = ContextCompat.getColor(context, R.color.goldenStar)
        val silverStar = ContextCompat.getColor(context, R.color.silverStar)

        val stars = listOf(binding.starOne, binding.starTwo, binding.startThree, binding.starFour, binding.starFive)

        stars.forEachIndexed { index, star ->
            if (index < selectedStars) {
                star.setColorFilter(goldenStar, PorterDuff.Mode.SRC_ATOP)
            } else {
                star.setColorFilter(silverStar, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }





    private fun onButtonClick() {
        binding.button3.setOnClickListener {
            val image = messageModel.userImage
            val content = binding.editTextText5.text.toString()
            val name = messageModel.userName
            val review = ReviewData(randomId(),image,name,content,currentStar.toString())
            gigData?.let {
                it.reviews.add(review)
                viewModel.setGigData(messageModel.gigId,it)
            }
        }
    }

    private fun receiveData() {
        messageModel = navArgs.mode
        viewModel.getGigDetail(messageModel.gigId)
        setData()
    }

    private fun setData() {
        binding.textView27.text = "How was your expereience with ${messageModel.providerName}"
        Glide.with(requireContext()).load(messageModel.providerImage).into(binding.ivProfile)
    }
    private fun randomId() : String{
        return UUID.randomUUID().toString()
    }
    private fun observeGigDetail(){
        lifecycleScope.launch {
            viewModel.getGig.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar7.visibility = View.INVISIBLE
                        Log.d("khan","errr")
                    }
                    is Resource.Loading -> {
                        binding.progressBar7.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar7.visibility = View.INVISIBLE
                        gigData = it.data
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }


}