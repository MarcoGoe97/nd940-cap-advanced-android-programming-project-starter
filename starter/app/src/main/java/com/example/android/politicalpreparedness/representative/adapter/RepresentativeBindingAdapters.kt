package com.example.android.politicalpreparedness.representative.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R

@BindingAdapter("profileImage")
fun fetchImage(imgView: ImageView, src: String?) {
    src?.let {
        val imgUri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop())
            .into(imgView)
    }
}
