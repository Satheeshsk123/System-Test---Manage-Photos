package com.test.systemtest

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.systemtest.databinding.MainAdapterViewBinding
import com.test.systemtest.model.ImageData
import com.test.systemtest.view.ImageClickListener
import com.test.systemtest.viewmodel.MainViewModel

class MainAdapter : RecyclerView.Adapter<MainViewHolder>() {
    var imageDatas = mutableListOf<String>()
    var imageClickListener:ImageClickListener?=null


    fun setMovieList(imageData: List<String>, itemClickListener: ImageClickListener) {
        this.imageDatas = imageData.toMutableList()
        this.imageClickListener=itemClickListener
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MainAdapterViewBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val imageData = imageDatas[position]

        Glide.with(holder.itemView.context).load(Uri.parse(imageData)).into(holder.binding.myPhotos)
        holder.binding.cancelImage.setOnClickListener {
            imageClickListener!!.onImageCancelClick(position)
        }
        if (position==0){
            holder.binding.profilePicTxtLay.visibility=View.VISIBLE
        }else{
            holder.binding.profilePicTxtLay.visibility=View.GONE
        }
    }
    override fun getItemCount(): Int {
        return imageDatas.size
    }
}
class MainViewHolder(val binding: MainAdapterViewBinding) : RecyclerView.ViewHolder(binding.root) {
}