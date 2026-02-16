package com.example.hazir.fragments.main
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.hazir.R
import com.example.hazir.databinding.FragmentReviewCompleteBinding


class FragmentReviewComplete : Fragment(){
    private lateinit var binding: FragmentReviewCompleteBinding
    private val navArgs by navArgs<FragmentReviewCompleteArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewCompleteBinding.inflate(inflater,container,false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         getFrom()
    }

    private fun getFrom() {
        val from = navArgs.from
        onClickListeners(from)
        if(from=="creategig"){
            binding.textView38.text = "Gig Created Successfully"
        }
        else if(from=="rating"){
            binding.textView38.text = "Review Submitted Successfully"
        }
    }

    private fun onClickListeners(from: String) {
        binding.btnGoToHome.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentReviewComplete_to_fragmentHome)
        }
    }

}