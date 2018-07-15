package com.hyn.textimage.extension

import android.support.v7.widget.RecyclerView
import android.view.View

class ViewHolder(val convertView: View) : RecyclerView.ViewHolder(convertView) {

    fun <T : View> findView(viewId: Int): T {
        return convertView.findView(viewId)
    }
}


