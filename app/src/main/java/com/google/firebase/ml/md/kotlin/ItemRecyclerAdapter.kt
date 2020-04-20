package com.google.firebase.ml.md.kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ml.md.R
import com.google.firebase.ml.md.kotlin.models.itempost
import kotlinx.android.synthetic.main.layout_rewards_list_item.view.*

class ItemRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<itempost> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.layout_rewards_list_item,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ItemViewHolder ->{
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(itemList: List<itempost>){
        items = itemList
    }

    class ItemViewHolder constructor(
            itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val itemImage = itemView.blog_image
        val itemTitle = itemView.blog_title
        val itemAuthor = itemView.blog_author

        fun bind(itempost: itempost){

            itemTitle.setText(itempost.title)
            itemAuthor.setText(itempost.name)

            val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(itempost.image)
                    .into(itemImage)
        }

    }

}