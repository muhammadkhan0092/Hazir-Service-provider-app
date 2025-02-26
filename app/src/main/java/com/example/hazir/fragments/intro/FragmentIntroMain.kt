package com.example.hazir.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hazir.adapters.IntroViewPagerAdapter
import com.example.hazir.databinding.FragmentIntroMainBinding


class FragmentIntroMain : Fragment(){
    private lateinit var binding: FragmentIntroMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesFragments = arrayListOf<Fragment>(
            FragmentIntroA(),
            FragmentIntroB()
        )
        val viewPager2Adapter = IntroViewPagerAdapter(
            categoriesFragments,
            childFragmentManager,
            lifecycle
        )

        binding.viewPagerHome.adapter = viewPager2Adapter
    }



}