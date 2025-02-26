package com.example.hazir.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.hazir.data.CategoriesData
import com.example.hazir.data.CleaningData
import com.example.hazir.databinding.RvCategoriesBinding

class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>(){

    inner class CategoriesViewHolder(val binding : RvCategoriesBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<CategoriesData>(){
        override fun areItemsTheSame(oldItem: CategoriesData, newItem: CategoriesData): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoriesData, newItem: CategoriesData): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesViewHolder {
        return CategoriesViewHolder(
            RvCategoriesBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            imageView14.setBackgroundResource(item.image)
            textView15.text = item.categories
            catAc.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item.color))

        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick : ((CategoriesData) -> Unit)? = null


}