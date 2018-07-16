package com.hyn.textimage

import android.content.Context
import android.content.ContextWrapper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hyn.textimage.adapter.MainAdapter
import com.hyn.textimage.model.MainItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.grid) }
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 1)
        adapter = MainAdapter()
        val mainItems = ArrayList<MainItem>()
        val itemQuick = MainItem(R.drawable.kuaisufan, "图片快速添加文字", ContextCompat.getColor(recyclerView.context, R.color.colorCard1))
        val itemText = MainItem(R.drawable.gird, "文字生成图片", ContextCompat.getColor(recyclerView.context, R.color.colorCard2))
        val itemMine = MainItem(R.drawable.local_img, "我的图片", ContextCompat.getColor(recyclerView.context, R.color.colorCard4))
        val itemAbout = MainItem(R.drawable.about, "关于", ContextCompat.getColor(recyclerView.context, R.color.colorCard3))
        mainItems.add(itemQuick)
        mainItems.add(itemText)
        mainItems.add(itemMine)
        mainItems.add(itemAbout)
        recyclerView.adapter = adapter
        adapter.setData(mainItems)
        adapter.setClickListener(View.OnClickListener {
            val pos = it.getTag(R.integer.pos)
            when(pos) {
                0 -> startActivity(QuickImageActivity.createIntent(recyclerView.context))
                1 -> startActivity(TextImageActivity.createIntent(recyclerView.context))
                2 -> startActivity(MineImageActivity.createIntent(recyclerView.context))
                3 -> startActivity(AboutActivity.createIntent(recyclerView.context))
            }
        })
    }


}
