package com.example.hazir.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.hazir.databinding.RvAddServiceBinding
import com.example.hazir.databinding.RvRetreiveServiceBinding

class RetreiveServiceAdapter : RecyclerView.Adapter<RetreiveServiceAdapter.RetreiveServiceAdapterViewHolder>(){

    inner class RetreiveServiceAdapterViewHolder(val binding : RvRetreiveServiceBinding) : ViewHolder(binding.root){
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
    ): RetreiveServiceAdapterViewHolder {
        return RetreiveServiceAdapterViewHolder(
            RvRetreiveServiceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: RetreiveServiceAdapterViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvService.text = "✅  " + item
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