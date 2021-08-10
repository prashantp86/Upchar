package com.android.upchaar

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.IOException
import java.net.URI

class GlideLoader(val context: Context) {
    fun loadUserPicture(imageURI: Uri, imageView: ImageView) {
        try {
            // load the user image in the imageView
            Glide
                    .with(context)
                    .load(Uri.parse(imageURI.toString())) //uri of the image
                    .centerCrop() //scale type of the image
                    .placeholder(R.drawable.user_placeholder) //a default place holder if image is failed to load
                    .into(imageView) //the view in which image will be loaded
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}