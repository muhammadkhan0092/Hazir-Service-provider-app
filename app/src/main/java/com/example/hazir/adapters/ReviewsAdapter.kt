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
import com.example.hazir.data.ReviewData
import com.example.hazir.databinding.RvCategoriesBinding
import com.example.hazir.databinding.RvReviewsBinding

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder>(){

    inner class ReviewsAdapterViewHolder(val binding : RvReviewsBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<ReviewData>(){
        override fun areItemsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewsAdapterViewHolder {
        return ReviewsAdapterViewHolder(
            RvReviewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ReviewsAdapterViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            //ivImage.setImageResource(item.image)
            tvName.text = item.name
           // tvReview.text = item.review
            tvRating.text = item.rating
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick : ((ReviewData) -> Unit)? = null


}