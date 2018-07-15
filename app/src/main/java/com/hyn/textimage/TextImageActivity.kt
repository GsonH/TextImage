package com.hyn.textimage

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.hyn.textimage.fragment.PropertiesBSFragment
import kotlinx.android.synthetic.main.activity_quick_image.*
import android.graphics.Bitmap



class TextImageActivity : QuickImageActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, TextImageActivity::class.java)
        }
    }

    private val imgBgText: TextView by lazy { findViewById<TextView>(R.id.imgBg) }
    lateinit var colorFragment: PropertiesBSFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imgBgText.visibility = View.VISIBLE
        colorFragment = PropertiesBSFragment(true)
        imgBgText.setOnClickListener {
            colorFragment.show(supportFragmentManager, "bgColor")
            colorFragment.setPropertiesChangeListener(object : PropertiesBSFragment.Properties{

                override fun onColorChanged(colorCode: Int) {
                    setSourceColorBitmap(colorCode)
                }

                override fun onOpacityChanged(opacity: Int) {

                }

                override fun onBrushSizeChanged(brushSize: Int) {
                }
            })
        }

        photoEditorView.post {
            setSourceColorBitmap(Color.WHITE)
        }
        toolbar.title = "纯文字生成图片"
    }

    override fun initEdit() {
    }

    private fun setSourceColorBitmap(color: Int) {
        val bitmap = Bitmap.createBitmap(photoEditorView.width, photoEditorView.height,
                Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(color)//填充颜色
        photoEditorView.source.setImageBitmap(bitmap)
    }
}
