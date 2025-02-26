package com.example.hazir.fragments.main
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hazir.R
import com.example.hazir.databinding.FragmentReviewCompleteBinding


class FragmentReviewComplete : Fragment(){
    private lateinit var binding: FragmentReviewCompleteBinding
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
         binding.btnGoToHome.setOnClickListener {
             findNavController().navigate(R.id.action_fragmentReviewComplete_to_fragmentHome)
         }
    }

}