package com.example.nft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class CollectionFragment(private val collection: Collection) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false).apply {

            val title = findViewById<TextView>(R.id.text_collection_title).apply {
                text = collection.name
            }

            val back = findViewById<ImageView>(R.id.image_back).apply {
                setOnClickListener {
                    activity?.supportFragmentManager?.popBackStack()
                }
            }

            val image = findViewById<ImageView>(R.id.image)
            Glide.with(this)
                .load(collection.imageUrl)
                .into(image)

            val name = findViewById<TextView>(R.id.text_name).apply {
                text = collection.name
            }

            val description = findViewById<TextView>(R.id.text_description).apply {
                text = collection.description
            }

            val permalink = findViewById<ExtendedFloatingActionButton>(R.id.button_permalink).apply {
                setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(collection.permalink)))
                }
            }
        }
    }
    
    
    companion object {
        fun newInstance(collection: Collection) = CollectionFragment(collection)
    }
}