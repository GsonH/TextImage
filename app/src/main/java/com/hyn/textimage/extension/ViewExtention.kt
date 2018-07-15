package com.hyn.textimage.extension

import android.util.SparseArray
import android.view.View
import android.widget.ImageView


fun <T : View> View.findView(viewId: Int): T {
    val viewHolder: SparseArray<View> = tag as? SparseArray<View> ?: SparseArray()
    tag = viewHolder
    var childView: View? = viewHolder.get(viewId)
    if (null == childView) {
        childView = findViewById(viewId)
        viewHolder.put(viewId, childView)
    }
    return childView as T
}
