package com.hyn.textimage.adapter

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hyn.textimage.R
import com.hyn.textimage.extension.ViewHolder
import com.hyn.textimage.extension.findView

/**
 * Created by huangyanan on 2018/7/16.
 */
class LocalImgAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var localImgs: List<String>? = null
    private var onClick: View.OnClickListener? = null

    fun setData(localImgs: List<String>) {
        this.localImgs = localImgs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.local_img_item, parent, false))
    }

    override fun getItemCount() = localImgs?.size ?: 0


    fun setOnclickListener(onClick: View.OnClickListener) {
        this.onClick = onClick
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val options = RequestOptions()
                .placeholder(R.mipmap.ic_launcher)    //加载成功之前占位图
                .centerCrop()

        val icon = holder.convertView.findView<ImageView>(R.id.item_icon)
        holder.convertView.setTag(R.integer.pos, localImgs!![position])
        Glide.with(holder.convertView)
                .load(Uri.parse("file://" + localImgs!![position]))
                .apply(options)
                .into(icon)

        holder.convertView.setOnClickListener { v ->
            onClick?.let { it.onClick(v) }
        }
    }

}