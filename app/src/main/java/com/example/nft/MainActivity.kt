package com.example.nft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ahmadrosid.svgloader.SvgLoader
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var collectionList: RecyclerView
    private val collectionAdapter = CollectionAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        collectionList = findViewById<RecyclerView>(R.id.recyclerview_collection_list).apply {
            adapter = collectionAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int) = when ((adapter as CollectionAdapter).getItemViewType(position.coerceAtMost(itemCount))) {
                        ITEM_TYPE_PROGRESS_BAR -> 2
                        else -> 1
                    }
                }
            }
        }

        val refreshContainer = findViewById<SwipeRefreshLayout>(R.id.refresh_container).apply {
            setOnRefreshListener {
                if (viewModel.progressing.value == false) {
                    isRefreshing = true
                    viewModel.getCollections(collectionAdapter.itemCount)
                }
            }
        }

        val noResult = findViewById<TextView>(R.id.text_no_result)

        val progressBar = ListItemProgressBar()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.collections.observe(this) { collections ->
            collectionAdapter.apply {
                items.addAll(collections)
                collectionList.post {
                    if (collections.isEmpty()) { // remove progress bar
                        notifyItemRemoved(itemCount)
                    } else { // append new collections
                        notifyItemRangeInserted(itemCount, collections.size)
                    }
                }
                noResult.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
            }
            refreshContainer.isRefreshing = false
        }

        viewModel.progressing.observe(this) { progressing ->
            collectionAdapter.apply {
                if (progressing) {
                    items.add(progressBar)
                    collectionList.post { notifyItemInserted(itemCount) }
                } else {
                    items.remove(progressBar)
                }
            }
        }

        viewModel.toastMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                // remove progress bars
                collectionList.post {
                    collectionAdapter.apply {
                        notifyItemRemoved(itemCount)
                    }
                }
                refreshContainer.isRefreshing = false

                val messageText = when (message) {
                    MainViewModel.TOAST_MESSAGE_NETWORK_ERROR -> getString(R.string.toast_message_network_error)
                    else -> getString(R.string.toast_message_others)
                }
                Toast.makeText(this, messageText, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getCollections(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        SvgLoader.pluck().close()
    }


    inner class CollectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val items = ArrayList<Any>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                ITEM_TYPE_COLLECTION -> CollectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_collection, parent, false))
                else -> ProgressBarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_progress_bar, parent, false))
            }
        }

        override fun getItemCount() = items.size

        override fun getItemViewType(position: Int) = when (items[position]) {
            is Collection -> ITEM_TYPE_COLLECTION
            else -> ITEM_TYPE_PROGRESS_BAR
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder.itemViewType == ITEM_TYPE_COLLECTION) {
                val item = items[position]
                if (item is Collection) {
                    (holder as CollectionViewHolder).bind(item)
                }
                if (viewModel.reachEnd.value == false && position > itemCount - 3 && viewModel.progressing.value == false) {
                    viewModel.getCollections(itemCount)
                }
            }
        }
    }


    inner class ListItemProgressBar


    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val image: ImageView = itemView.findViewById(R.id.image)
        private val name: TextView = itemView.findViewById(R.id.name)

        fun bind(item: Collection) {
            if (item.imageUrl.endsWith(".svg", true)) {
                SvgLoader.pluck()
                    .with(this@MainActivity)
                    .setPlaceHolder(android.R.color.darker_gray, android.R.color.darker_gray)
                    .load(item.imageUrl, image)
            } else {
                Glide.with(this@MainActivity)
                    .load(item.imageUrl)
                    .placeholder(android.R.color.darker_gray)
                    .fallback(android.R.color.darker_gray)
                    .into(image)
            }
            name.text = item.name
            itemView.setOnClickListener {
                // prevent add fragment multiple times if user clicks rapidly
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, CollectionFragment.newInstance(item))
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
    }


    inner class ProgressBarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    companion object {
        private const val ITEM_TYPE_COLLECTION = 0
        private const val ITEM_TYPE_PROGRESS_BAR = 1
    }
}