package com.example.hazir.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.hazir.data.CategoriesData
import com.example.hazir.data.HistoryData
import com.example.hazir.databinding.RvHistoryItemBinding

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryAdapterViewHolder>(){

    inner class HistoryAdapterViewHolder(val binding : RvHistoryItemBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<HistoryData>(){
        override fun areItemsTheSame(oldItem: HistoryData, newItem: HistoryData): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryData, newItem: HistoryData): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapterViewHolder {
        return HistoryAdapterViewHolder(
            RvHistoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: HistoryAdapterViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvCategory.text = item.title
            tvDated.text = item.dated
            tvContext.text = item.context
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick : ((HistoryData) -> Unit)? = null


}