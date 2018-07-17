package com.hyn.textimage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.hyn.textimage.adapter.LocalImgAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
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
                if (files != null) {
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
                    if(it.isEmpty()) {
                        image_tip.visibility = View.GONE
                        no_image_tip.visibility = View.VISIBLE
                        image_list.visibility = View.GONE
                    } else {
                        image_tip.text = "照片保存在:内存卡/文字图片/  文件夹"
                        image_tip.visibility = View.VISIBLE
                        no_image_tip.visibility = View.GONE
                        image_list.visibility = View.VISIBLE
                        adapter = LocalImgAdapter()
                        image_list.adapter = adapter
                        adapter.setOnclickListener(View.OnClickListener {
                            val imgPath = it.getTag(R.integer.pos)
                            val medias = ArrayList<LocalMedia>()
                            val media = LocalMedia(imgPath.toString(), 0, PictureMimeType.ofImage(), "image/png")
                            medias.add(media)
                            PictureSelector.create(this@MineImageActivity).themeStyle(R.style.picture_default_style).openExternalPreview(0, medias)
                        })
                        adapter.setData(it)
                    }
                }

    }
}
