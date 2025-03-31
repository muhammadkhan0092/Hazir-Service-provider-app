package com.example.hazir.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.hazir.R
import com.example.hazir.data.DataPost
import com.example.hazir.databinding.RvPostsBinding
import com.google.firebase.auth.FirebaseAuth

class PostsAdapter(val context: Context) : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>(){

    inner class PostsViewHolder(val binding : RvPostsBinding) : ViewHolder(binding.root){
    }

    private val diffUtil =object : DiffUtil.ItemCallback<DataPost>(){
        override fun areItemsTheSame(oldItem: DataPost, newItem: DataPost): Boolean {
            return oldItem.id===newItem.id
        }

        override fun areContentsTheSame(oldItem: DataPost, newItem: DataPost): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostsViewHolder {
        return PostsViewHolder(
            RvPostsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val item = differ.currentList[position]
        Log.d("khan","setting post ${item.content}")
        holder.binding.apply {
            tvName.text = item.name
            textView17.text = item.content
            textView44.text = item.comments.size.toString() + " comments"
            textView43.text = item.likes.size.toString()
            Glide.with(holder.itemView).load(item.thumbnail).into(imageView35)
            if(!item.userImage.isNullOrEmpty()){
                Glide.with(holder.itemView).load(item.thumbnail).into(imageEdit)
            }
            Log.d("khan","fir ${FirebaseAuth.getInstance().uid.toString()}")
            Log.d("khan","fir ${item.likes}")
            if(FirebaseAuth.getInstance().uid.toString() in item.likes){
                Log.d("khan","if")
                textView46.text = "UNLIKE"
                textView46.setTextColor(ContextCompat.getColor(context, R.color.blue))
            }
            else
            {
                textView46.text = "LIKE"
                textView46.setTextColor(ContextCompat.getColor(context, R.color.black))
            }


        }

        holder.binding.textView47.setOnClickListener {
            onCommentClicked?.invoke(item)
        }
        holder.binding.textView46.setOnClickListener {
            onLikedClicked?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        Log.d("PostsAdapter", "Total items: ${differ.currentList.size}")
        return differ.currentList.size
    }



    var onCommentClicked : ((DataPost) -> Unit)? = null
    var onLikedClicked : ((DataPost) -> Unit)? = null


}