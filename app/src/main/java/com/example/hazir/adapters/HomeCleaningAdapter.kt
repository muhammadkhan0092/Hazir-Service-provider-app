package com.example.hazir.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.hazir.data.CleaningData
import com.example.hazir.databinding.RvCleaningBinding

class HomeCleaningAdapter : RecyclerView.Adapter<HomeCleaningAdapter.HomeCleaningViewHolder>(){

    inner class HomeCleaningViewHolder(val binding : RvCleaningBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<CleaningData>(){
        override fun areItemsTheSame(oldItem: CleaningData, newItem: CleaningData): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: CleaningData, newItem: CleaningData): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeCleaningViewHolder {
        return HomeCleaningViewHolder(
            RvCleaningBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: HomeCleaningViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            imageView14.setBackgroundResource(item.image)
            textView15.text = item.title
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}