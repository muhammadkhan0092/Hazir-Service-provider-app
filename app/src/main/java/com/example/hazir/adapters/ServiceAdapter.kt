package com.example.hazir.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.hazir.data.CategoriesData
import com.example.hazir.data.CleaningData
import com.example.hazir.databinding.RvAddServiceBinding
import com.example.hazir.databinding.RvCategoriesBinding

class ServiceAdapter : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>(){

    inner class ServiceViewHolder(val binding : RvAddServiceBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem== newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServiceViewHolder {
        return ServiceViewHolder(
            RvAddServiceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        Log.d("khan","hehe ${differ.currentList}")
        val item = differ.currentList[position]
        holder.binding.apply {
            tvService.text = item
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick : ((String) -> Unit)? = null


}