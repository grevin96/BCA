package com.bca.music.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bca.music.databinding.ItemMainBinding
import com.bca.music.listener.OnSingleClickListener
import com.bca.music.model.Item
import com.bca.music.util.SharedPreferences
import com.bumptech.glide.Glide

@SuppressLint("NotifyDataSetChanged")
class MainAdapter(private val context: Context): RecyclerView.Adapter<MainAdapter.MyHolder>() {
    private lateinit var item: Item

    private var listener: OnSingleClickListener?    = null
    private var items: ArrayList<Item>              = ArrayList()

    inner class MyHolder(val binding: ItemMainBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder = MyHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount(): Int                                            = items.size

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        with(holder) {
            with(binding) {
                track.text          = items[position].trackName
                artist.text         = items[position].artistName
                collection.text     = items[position].collectionName
                music.visibility    = if (SharedPreferences.read(context).isNullOrEmpty() || SharedPreferences.read(context) != items[position].trackId) View.GONE else View.VISIBLE
                divider.visibility  = if (position == itemCount - 1) View.GONE else View.VISIBLE
                itemView.id         = 1

                Glide.with(context).load(items[position].artworkUrl100).into(image)
                itemView.setOnClickListener {
                    if (SharedPreferences.read(context).isNullOrEmpty() || SharedPreferences.read(context) != items[position].trackId) {
                        item                = items[position]
                        music.visibility    = View.VISIBLE

                        SharedPreferences.save(context, item.trackId.toString())
                        notifyDataSetChanged()
                        listener?.onClick(it)
                    }
                }
            }
        }
    }

    fun item() = item

    fun listener(listener: OnSingleClickListener) { this.listener = listener }

    fun data(items: ArrayList<Item>) {
        this.items = items

        notifyDataSetChanged()
    }
}