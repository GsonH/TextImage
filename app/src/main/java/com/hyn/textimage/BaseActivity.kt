package com.hyn.textimage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.jaeger.library.StatusBarUtil

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }

    fun createToolbar(itemListener: Toolbar.OnMenuItemClickListener?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "图片编辑"
        toolbar.setNavigationOnClickListener {
            finish()
        }
        //左边的小箭头（注意需要在setSupportActionBar(toolbar)之后才有效果）
        //设置toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        itemListener?.let {
            //参数为菜单资源id
            toolbar.setOnMenuItemClickListener(itemListener)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.getItemId() === android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
