package br.com.simplepass.loadingbuttonsample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.customViews.ProgressButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgBtnTest1.run { setOnClickListener { morphDoneAndRevert(this@MainActivity) } }
        buttonTest1.run { setOnClickListener { morphDoneAndRevert(this@MainActivity) } }

        buttonTest2.run { setOnClickListener { morphAndRevert() } }

        buttonTest3.run { setOnClickListener { morphAndRevert(100) } }

        buttonTest4.run {
            setOnClickListener {
                morphDoneAndRevert(this@MainActivity, doneTime = 100)
            }
        }

        buttonTest5.run {
            setOnClickListener {
                morphDoneAndRevert(this@MainActivity, doneTime = 100, revertTime = 200)
            }
        }
    }

    private fun ProgressButton.morphDoneAndRevert(
        context: Context,
        fillColor: Int = ContextCompat.getColor(context, android.R.color.black),
        bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_pregnant_woman_white_48dp),
        doneTime: Long = 3000,
        revertTime: Long = 4000
    ) {
        startAnimation()
        Handler().run {
            postDelayed({ doneLoadingAnimation(fillColor, bitmap) }, doneTime)
            postDelayed({ revertAnimation() }, revertTime)
        }
    }

    private fun ProgressButton.morphAndRevert(revertTime: Long = 3000) {
        startAnimation()
        Handler().postDelayed({ revertAnimation() }, revertTime)
    }
}
