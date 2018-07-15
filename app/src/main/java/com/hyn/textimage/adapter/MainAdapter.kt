package com.hyn.textimage.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hyn.textimage.R
import com.hyn.textimage.extension.ViewHolder
import com.hyn.textimage.extension.findView
import com.hyn.textimage.model.MainItem

class MainAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var items : List<MainItem>? = null
    private var onCLick : View.OnClickListener? = null

    fun setData(item: List<MainItem>?) {
        this.items = item
        notifyDataSetChanged()
    }

    fun setClickListener(onClick: View.OnClickListener) {
        this.onCLick = onClick
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemText = holder.itemView.findView<TextView>(R.id.item_text)
        val itemIcon = holder.itemView.findView<ImageView>(R.id.item_icon)
        holder.itemView.setOnClickListener {
            onCLick?.onClick(it)
        }
        val item = items!![position]
        holder.itemView.setBackgroundColor(item.bg)
        itemText.text = item.name
        Glide.with(itemIcon).load(item.icon).into(itemIcon)
        holder.itemView.setTag(R.integer.pos, position)
    }

    override fun getItemCount(): Int {
        return items?.let { it.size } ?: 0
    }
}
