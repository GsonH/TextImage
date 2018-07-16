package com.hyn.textimage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.GridLayoutManager
import com.hyn.textimage.adapter.LocalImgAdapter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mine_image.*
import java.io.File

class MineImageActivity : BaseActivity() {

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, MineImageActivity::class.java)
        }
    }

    private lateinit var adapter: LocalImgAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine_image)
        createToolbar(null)
        toolbar.title = "我的图片"
        image_list.layoutManager = GridLayoutManager(this, 2)
        loadImage()
    }


    private fun loadImage() {
        Flowable.fromCallable<ArrayList<String>> {
            val file = File(ImageUtil.IMAGE_PATH)
            val filePaths: ArrayList<String> = ArrayList()
            if (file.exists()) {
                val files = file.listFiles()
                if(files != null) {
                    for (f in files) {
                        if (f.path.endsWith(".png")) {
                            filePaths.add(f.path)
                        }
                    }
                }

            }
            filePaths
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter = LocalImgAdapter()
                    image_list.adapter = adapter
                    adapter.setData(it)
                }

    }
}
